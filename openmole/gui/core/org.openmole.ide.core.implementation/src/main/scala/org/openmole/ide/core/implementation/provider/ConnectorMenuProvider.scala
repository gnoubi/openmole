/*
 * Copyright (C) 2011 Mathieu Mathieu Leclaire <mathieu.Mathieu Leclaire at openmole.org>
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.implementation.provider

import java.awt.Point
import javax.swing.JMenuItem
import org.netbeans.api.visual.widget.Widget
import org.openmole.ide.core.implementation.action.AddTransitionConditionAction
import org.openmole.ide.core.implementation.action.ChangeTransitionAction
import org.openmole.ide.core.implementation.action.RemoveTransitionAction
import org.openmole.ide.core.implementation.workflow._
import scala.swing.Action
import scala.swing.Menu
import scala.swing.MenuItem
import org.openmole.ide.core.implementation.commons.{ EndTransitionType, AggregationTransitionType, ExplorationTransitionType, SimpleTransitionType }

class ConnectorMenuProvider(scene: MoleScene,
                            connectionWidget: ConnectorWidget) extends GenericMenuProvider {

  var itTransitionOrDataChannelMenu = new MenuItem("to ")
  var itCond = new MenuItem(new AddTransitionConditionAction(connectionWidget))
  var itChangeTransition = new Menu("to ")

  val itRem = new JMenuItem("Remove")
  itRem.addActionListener(new RemoveTransitionAction(scene, connectionWidget.connector.id))

  items += itRem

  override def getPopupMenu(widget: Widget, point: Point) = {
    items -= (itTransitionOrDataChannelMenu.peer,
      itChangeTransition.peer,
      itCond.peer)

    connectionWidget.connector match {
      case x: TransitionUI ⇒
        itChangeTransition.peer.removeAll
        List(SimpleTransitionType, ExplorationTransitionType, AggregationTransitionType, EndTransitionType).filterNot(_ == x.transitionType).foreach { ttype ⇒
          itChangeTransition.peer.add(new MenuItem(new ChangeTransitionAction(connectionWidget, ttype)).peer)
        }

        items += (itCond.peer,
          itChangeTransition.peer)

        itTransitionOrDataChannelMenu = new MenuItem(new Action("to Data channel") {
          override def apply {
            val newC = new DataChannelUI(x.source,
              x.target,
              x.filteredPrototypes)
            scene.dataUI.changeConnector(x, newC)
            connectionWidget.setConnnector(newC)
          }
        })
      case x: DataChannelUI ⇒
        itTransitionOrDataChannelMenu = new MenuItem(new Action("to Transition") {
          override def apply {
            val newC = new TransitionUI(x.source,
              x.target,
              SimpleTransitionType,
              None,
              x.filteredPrototypes)
            scene.dataUI.changeConnector(x, newC)
            connectionWidget.setConnnector(newC)
          }
        })
      case _ ⇒
    }
    items += itTransitionOrDataChannelMenu.peer
    super.getPopupMenu(widget, point)
  }
}