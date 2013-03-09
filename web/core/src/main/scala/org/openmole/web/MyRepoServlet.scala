package org.openmole.web

import _root_.akka.actor.{ Props, ActorSystem }
import org.scalatra._
import org.scalatra.servlet._
import scalate.ScalateSupport
import servlet._
import java.io._
import java.security.MessageDigest
import concurrent.{ Await, ExecutionContext, Future }
import scala.util.matching.Regex
import concurrent.Future
import concurrent.duration._
import org.openmole.ide.core.implementation.execution._
import org.openmole.ide.core.implementation.serializer._
import org.openmole.core.serializer.SerializerService
import com.thoughtworks.xstream.mapper.CannotResolveClassException
import javax.servlet.{ MultipartConfigElement, ServletException }
import scala.collection.JavaConversions._

import java.util.Properties
import org.openmole.web.{ Suppliers, SlickSupport, Coffees }
import org.slf4j.LoggerFactory

import slick.driver.H2Driver.simple._
import com.jolbox.bonecp._
import java.sql.SQLException
import java.util.UUID

import org.json4s.{ DefaultFormats, Formats }

// JSON handling support from Scalatra
import org.scalatra.json._

//import scala.slick.session.Database
import Database.threadLocalSession
import java.io.IOException

class MyRepoServlet(val system: ActorSystem) extends ScalatraServlet with ScalateSupport with FileUploadSupport with FutureSupport with SlickSupport with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024), fileSizeThreshold = Some(1024 * 1024 * 1024)))

  protected implicit def executor: ExecutionContext = system.dispatcher

  val digest = MessageDigest.getInstance("MD5")
  def md5(t: String): String = digest.digest(t.getBytes).map("%02x".format(_)).mkString

  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f ⇒ r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_, r))
  }

  get("/uploadMole") {
    contentType = "text/html"
    new AsyncResult() {
      val is = Future {
        ssp("/uploadMole", "body" -> "Please upload an om file below!")
      }
    }
  }

  get("/tagtest") {
    contentType = "text/html"
    new AsyncResult() {
      val is = Future {
        ssp("/tagtest", "body" -> "Please enter tag below !")
      }
    }
  }

  get("/json") {
    contentType = "application/json"
    new AsyncResult() {
      val is = Future {
        val term = params.get("term")
        println("result = " + term)
        //val result = "[\"scala\", \"test\"]"
        //result
        Query(Tags)
        //parsedBody.extract[Tag]
      }
    }

  }

  //DATABASE UTILS

  get("/create-table") {
    db withSession {
      (Workflows.ddl ++ Tags.ddl ++ WFTag.ddl).create
    }
  }

  get("/drop-tables") {
    db withSession {
      (Workflows.ddl ++ Tags.ddl ++ WFTag.ddl).drop
    }
  }

  get("/wf-by/:tag/") {
    val tag: String = params.getOrElse("tag", halt(400))
  }

  get("/insert-sample-tag") {
    db withSession {
      // Insert some initial tags
      val tagsId = Tags.forInsert returning Tags.id insertAll (
        Tag(None, "0.8-alpha-1", "version"),
        Tag(None, "0.8-alpha-2", "version"),
        Tag(None, "0.8-alpha-3", "version"),
        Tag(None, "0.8-alpha-4", "version"))

      //println("tagID = " + tagsId.seq.mkString(" ; "))

    }
  }

  def createFile(name: String): File = {

    //    println(" system property = " + System.getProperty("user.dir"))

    val ftest = new File(servletContext.getRealPath("/")).getAbsolutePath()
    val path: String = servletContext.getRealPath("/")
    val file = new File(path + "/repository/" + name)
    println("FILE name = " + file.getName + " / " + file.getPath)

    /*try {
      //file.createNewFile()
      //file.setWritable(true)
    } catch {
      case e: IOException ⇒ println("Error " + e)
    } */
    file
  }

  // This method processes the uploaded file in some way.
  def processFile(upload: FileItem, folderUuid: String) = {

    // http://stackoverflow.com/questions/2637643/how-do-i-list-all-files-in-a-subdirectory-in-scala

    val path: String = "/tmp/"

    val (sucess: Boolean, folderFile: File) = try {
      val folderFile = new File(path + folderUuid)
      if (folderFile.exists()) {
        (true, folderFile)
      } else {
        (folderFile.mkdirs(), folderFile)
      }

    } catch {
      case e: IOException ⇒ println("Error " + e)
    }

    if (sucess) {
      try {
        val file: File = new File(folderFile.getAbsolutePath + "/" + upload.getName)
        println("try to write to >> " + file.getAbsolutePath)
        upload.write(file)
      } catch {
        case e: IOException ⇒ println("Error " + e)
      }
    }
  }

  post("/uploadMole") {
    contentType = "text/html"

    implicit def timeout = Duration(10, SECONDS)

    val x = new AsyncResult() {
      val is = Future {
        try {

          // tag identification
          val tags: String = params.getOrElse("tags", halt(400))
          println("tags ID : " + tags)

          // try to create folder, copy file
          val document: FileItem = fileParams("file")
          println("file name: " + document.name)

          //generate UUID for wf folder creation
          val uuid = UUID.randomUUID()
          processFile(document, uuid.toString)

          //new GUISerializer(tempFile.toString).unserialize
          //ScenesManager.moleScenes.map { s ⇒ println("name : " + s.manager.name) }
        } catch {
          case e: IOException ⇒ println("IO Error " + e)
          case s: SecurityException ⇒ println("Security Error " + s)
        }

        //tempFile.deleteOnExit

        //val data = fileParams.get("file")
        //processFile(data)

        //file.setWritable(true)

        /*val out = new FileOutputStream("/tmp/test.om")
            File.copy(x, out)
            out.close()*/
        /*
        fileParams.get("file") match {
          case Some(file) ⇒
            try {
              //val f: File = createFile("task.om")
              //val f = createFile("task.om")
              //file.write(f)


              println("datafile = " + f)
              new GUISerializer(f.toString).unserialize
              ScenesManager.moleScenes.map { s ⇒ println("name : " + s.manager.name) }

            } catch {
              case e: CannotResolveClassException ⇒ None -> "The uploaded xml was not a valid serialized object."
            }
          case None ⇒ println("Error when read file")
        }
  */

      }
    }

    println(x.timeout)
    x
  }

}