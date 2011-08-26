/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.implementation.control

import scala.swing.Orientation
import scala.swing.Button
import org.openmole.ide.misc.eventlistener.PasswordListener
import org.openmole.misc.eventdispatcher.EventDispatcher
import org.openmole.misc.tools.service.Priority
import org.openmole.misc.workspace.Workspace
import scala.swing.BoxPanel
import scala.swing.event.ButtonClicked

object ExecutionBoard extends BoxPanel(Orientation.Horizontal){
  val startButton = new Button("start")
  val stopButton = new Button("stop")
  contents.append(startButton,stopButton)
  
  listenTo(`startButton`)
  reactions += {
    case ButtonClicked(`startButton`) => if(TabManager.currentExecutionManager.isDefined) TabManager.currentExecutionManager.get.start
    case ButtonClicked(`stopButton`) =>if(TabManager.currentExecutionManager.isDefined) TabManager.currentExecutionManager.get.cancel
  }
  
  EventDispatcher.registerForObjectChanged(Workspace.instance, Priority.NORMAL, PasswordListener , Workspace.PasswordRequiered)
  
  def activate(b:Boolean) = {
    startButton.enabled = b
    stopButton.enabled = b
  }
}
