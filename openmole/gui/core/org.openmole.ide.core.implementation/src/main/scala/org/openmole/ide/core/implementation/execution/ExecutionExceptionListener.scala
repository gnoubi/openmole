/*
 * Copyright (C) 2011 mathieu
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

package org.openmole.ide.core.implementation.execution

import org.openmole.core.model.mole.IMoleExecution._
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.misc.eventdispatcher.Event
import org.openmole.misc.eventdispatcher.EventListener
import org.openmole.misc.exception.ExceptionUtils
import org.openmole.core.model.mole.IMoleExecution.JobFailed
import org.openmole.core.model.mole.IMoleExecution.ExceptionRaised
import org.openmole.core.model.mole.IMoleExecution.HookExceptionRaised
import org.openmole.core.model.mole.IMoleExecution.SourceExceptionRaised

class ExecutionExceptionListener(exeManager: ExecutionManager) extends EventListener[IMoleExecution] {

  override def triggered(execution: IMoleExecution, event: Event[IMoleExecution]) = synchronized {
    event match {
      case j: JobFailed ⇒
        exeManager.executionJobExceptionTextArea.warn("Job failed for capsule " + j.capsule, None, ExceptionUtils.prettify(j.exception))
      case e: ExceptionRaised ⇒
        exeManager.executionJobExceptionTextArea.warn(e.level + ": Exception managing job " + e.moleJob, None, ExceptionUtils.prettify(e.exception))
      case h: HookExceptionRaised ⇒
        exeManager.executionJobExceptionTextArea.warn(h.level + ": Exception in hook " + h.hook, None, ExceptionUtils.prettify(h.exception))
      case s: SourceExceptionRaised ⇒
        exeManager.executionJobExceptionTextArea.warn(s.level + ": Exception in source " + s.source, None, ExceptionUtils.prettify(s.exception))
    }
    exeManager.tabbedPane.selection.index = 1
  }
}
