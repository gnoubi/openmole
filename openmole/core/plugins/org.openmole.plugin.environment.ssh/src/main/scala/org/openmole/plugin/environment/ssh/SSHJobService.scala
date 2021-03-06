/*
 * Copyright (C) 2011 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.environment.ssh

import org.openmole.core.batch.environment._
import org.openmole.core.batch.jobservice.BatchJob
import org.openmole.core.model.execution.ExecutionState
import org.openmole.misc.eventdispatcher.Event
import org.openmole.misc.eventdispatcher.EventDispatcher
import org.openmole.misc.eventdispatcher.EventListener
import org.openmole.plugin.environment.gridscale._
import fr.iscpif.gridscale.ssh.{ SSHJobService ⇒ GSSSHJobService, SSHConnectionCache, SSHJobDescription }
import java.util.concurrent.atomic.AtomicInteger
import collection.mutable
import org.openmole.misc.tools.service.Logger

object SSHJobService extends Logger

trait SSHJobService extends GridScaleJobService with SharedStorage { js ⇒

  val environment: BatchEnvironment with SSHAccess
  def nbSlots: Int

  val jobService = new GSSSHJobService with environment.ThisHost with SSHConnectionCache

  val queue = new mutable.SynchronizedQueue[SSHBatchJob]
  @transient lazy val nbRunning = new AtomicInteger

  object BatchJobStatusListner extends EventListener[BatchJob] {

    import ExecutionState._

    override def triggered(job: BatchJob, ev: Event[BatchJob]) = SSHJobService.this.synchronized {
      ev match {
        case ev: BatchJob.StateChanged ⇒
          ev.newState match {
            case DONE | KILLED | FAILED ⇒
              ev.oldState match {
                case DONE | FAILED | KILLED ⇒
                case _ ⇒
                  queue.dequeueFirst(_ ⇒ true) match {
                    case Some(j) ⇒ j.submit
                    case None    ⇒ nbRunning.decrementAndGet
                  }

              }
            case _ ⇒
          }
      }
    }
  }

  protected def _submit(serializedJob: SerializedJob) = {
    val (remoteScript, result) = buildScript(serializedJob)

    val _jobDescription = new SSHJobDescription {
      val executable = "/bin/bash"
      val arguments = remoteScript
      val workDirectory = sharedFS.root
    }

    val sshBatchJob = new SSHBatchJob {
      val jobService = js
      val jobDescription = _jobDescription
      val resultPath = result
    }

    SSHJobService.logger.fine(s"Queuing /bin/bash $remoteScript in directory ${sharedFS.root}")

    EventDispatcher.listen(sshBatchJob: BatchJob, BatchJobStatusListner, classOf[BatchJob.StateChanged])

    synchronized {
      if (nbRunning.get() < nbSlots) {
        nbRunning.incrementAndGet
        sshBatchJob.submit
      }
      else queue.enqueue(sshBatchJob)
    }
    sshBatchJob
  }

  private[ssh] def submit(description: SSHJobDescription) =
    jobService.submit(description)(authentication)

}
