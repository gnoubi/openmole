/*
 * Copyright (C) 2010 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.runtime.runtime

import com.ice.tar.TarEntry
import com.ice.tar.TarInputStream
import com.ice.tar.TarOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.UUID
import org.openmole.misc.eventdispatcher._
import org.openmole.misc.exception._
import org.openmole.misc.tools.io.FileUtil._
import org.openmole.misc.tools.io.TarArchiver._
import org.openmole.misc.tools.service._
import org.openmole.core.batch.authentication._
import org.openmole.core.batch.storage._
import org.openmole.core.implementation.execution.local._
import org.openmole.core.batch.message._
import org.openmole.misc.tools.service.Retry
import org.openmole.core.serializer._
import org.openmole.misc.hashservice._
import org.openmole.misc.pluginmanager._
import org.openmole.misc.workspace._
import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap
import util.{ Success, Failure }
import org.openmole.core.model.execution.Environment.RuntimeLog

object Runtime extends Logger {
  val NbRetry = 3

  def retry[T](f: ⇒ T) = Retry.retry(f, NbRetry)
}

class Runtime {

  import Runtime._

  def apply(storage: SimpleStorage, communicationDirPath: String, inputMessagePath: String, outputMessagePath: String, debug: Boolean) = {

    /*--- get execution message and job for runtime---*/
    val usedFiles = new HashMap[File, File]

    logger.fine("Downloading input message")

    val executionMessage = retry(Workspace.withTmpFile { executionMesageFileCache ⇒
      storage.downloadGZ(inputMessagePath, executionMesageFileCache)
      SerialiserService.deserialise[ExecutionMessage](executionMesageFileCache)
    })

    val oldOut = System.out
    val oldErr = System.err

    val out = Workspace.newFile("openmole", ".out")
    val err = Workspace.newFile("openmole", ".err")

    val outSt = new PrintStream(out)
    val errSt = new PrintStream(err)

    if (!debug) {
      System.setOut(outSt)
      System.setErr(errSt)
    }

    val beginTime = System.currentTimeMillis

    val result = try {
      logger.fine("Downloading plugins")

      val pluginDir = Workspace.newDir

      for (plugin ← executionMessage.plugins) {
        val inPluginDirLocalFile = File.createTempFile("plugin", ".jar", pluginDir)
        logger.fine("Downloading plugin" + plugin.path)
        storage.downloadGZ(plugin.path, inPluginDirLocalFile)

        if (HashService.computeHash(inPluginDirLocalFile).toString != plugin.hash)
          throw new InternalProcessingError("Hash of a plugin does't match.")

        usedFiles.put(plugin.src, inPluginDirLocalFile)
      }

      PluginManager.loadDir(pluginDir)

      /* --- Download the files for the local file cache ---*/
      logger.fine("Downloading files")

      for (repliURI ← executionMessage.files) {

        //To avoid getting twice the same plugin with different path
        if (!usedFiles.containsKey(repliURI.src)) {
          val cache = Workspace.newFile

          logger.fine("Downloading file " + repliURI.path)
          storage.downloadGZ(repliURI.path, cache)
          val cacheHash = HashService.computeHash(cache).toString

          if (cacheHash != repliURI.hash)
            throw new InternalProcessingError("Hash is incorrect for file " + repliURI.src.toString + " replicated at " + repliURI.path)

          val local = if (repliURI.directory) {
            val local = Workspace.newDir("dirReplica")
            cache.extractDirArchiveWithRelativePath(local)
            local
          }
          else {
            cache.mode = repliURI.mode
            cache
          }

          usedFiles.put(repliURI.src, local)
        }
      }

      val jobsFileCache = Workspace.newFile
      logger.fine("Downloading execution message")
      storage.downloadGZ(executionMessage.jobs.path, jobsFileCache)

      if (HashService.computeHash(jobsFileCache).toString != executionMessage.jobs.hash) throw new InternalProcessingError("Hash of the execution job does't match.")

      val tis = new TarInputStream(new FileInputStream(jobsFileCache))
      val runableTasks = tis.applyAndClose(e ⇒ { SerialiserService.deserialiseReplaceFiles[RunnableTask](tis, usedFiles) })
      jobsFileCache.delete

      val saver = new ContextSaver(runableTasks.size)
      val allMoleJobs = runableTasks.map { _.toMoleJob(saver.save) }

      val beginExecutionTime = System.currentTimeMillis

      /* --- Submit all jobs to the local environment --*/
      logger.fine("Run the jobs")
      for (toProcess ← allMoleJobs) LocalEnvironment.default.submit(toProcess)

      saver.waitAllFinished

      val endExecutionTime = System.currentTimeMillis

      logger.fine("Results " + saver.results)

      val contextResults = new ContextResults(saver.results)
      val contextResultFile = Workspace.newFile

      SerialiserService.serialiseAndArchiveFiles(contextResults, contextResultFile)
      val uploadedcontextResults = storage.child(executionMessage.communicationDirPath, Storage.uniqName("uplodedTar", ".tgz"))
      val result = new FileMessage(uploadedcontextResults, HashService.computeHash(contextResultFile).toString)

      logger.fine("Upload the results")
      retry(storage.uploadGZ(contextResultFile, uploadedcontextResults))
      contextResultFile.delete

      val endTime = System.currentTimeMillis

      Success(result -> RuntimeLog(beginTime, beginExecutionTime, endExecutionTime, endTime, LocalHostName.localHostName))
    }
    catch {
      case t: Throwable ⇒
        if (debug) logger.log(SEVERE, "", t)
        Failure(t)
    }
    finally {
      outSt.close
      errSt.close

      System.setOut(oldOut)
      System.setErr(oldErr)
    }

    logger.fine("Upload the outputs")
    val outputMessage =
      if (out.length != 0) {
        val output = storage.child(executionMessage.communicationDirPath, Storage.uniqName("output", ".txt"))
        storage.uploadGZ(out, output)
        Some(new FileMessage(output, HashService.computeHash(out).toString))
      }
      else None

    out.delete

    val errorMessage =
      if (err.length != 0) {
        val errout = storage.child(executionMessage.communicationDirPath, Storage.uniqName("outputError", ".txt"))
        storage.uploadGZ(err, errout)
        Some(new FileMessage(errout, HashService.computeHash(err).toString))
      }
      else None

    err.delete

    val runtimeResult = new RuntimeResult(outputMessage, errorMessage, result)

    logger.fine("Upload the result message")
    val outputLocal = Workspace.newFile("output", ".res")
    SerialiserService.serialise(runtimeResult, outputLocal)
    try retry(storage.uploadGZ(outputLocal, outputMessagePath))
    finally outputLocal.delete

    result
  }

}
