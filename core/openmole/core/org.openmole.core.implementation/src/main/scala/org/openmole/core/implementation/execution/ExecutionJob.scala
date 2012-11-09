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

package org.openmole.core.implementation.execution

import org.openmole.core.model.execution._
import org.openmole.core.model.tools.ITimeStamp
import org.openmole.misc.eventdispatcher.EventDispatcher
import scala.collection.mutable.ListBuffer
import org.openmole.core.implementation.tools.TimeStamp
import org.openmole.core.model.execution.ExecutionState._

trait ExecutionJob extends IExecutionJob {
  val timeStamps: ListBuffer[ITimeStamp[ExecutionState]] = new ListBuffer

  private var _state: ExecutionState = READY

  override def state = _state

  def state_=(state: ExecutionState) =
    synchronized {
      if (!this.state.isFinal) {
        timeStamps += (new TimeStamp(state))
        EventDispatcher.trigger(environment, new Environment.JobStateChanged(this, state, this.state))
        _state = state
      }
    }

}