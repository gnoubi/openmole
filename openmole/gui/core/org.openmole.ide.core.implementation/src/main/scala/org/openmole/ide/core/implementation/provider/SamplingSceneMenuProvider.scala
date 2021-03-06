/*
 * Copyright (C) 2012 mathieu
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

package org.openmole.ide.core.implementation.provider

import java.awt.Point
import org.netbeans.api.visual.widget.Widget
import scala.swing.Action
import scala.swing.Menu
import scala.swing.MenuItem
import org.openmole.ide.core.implementation.execution.ScenesManager
import org.openmole.ide.core.implementation.registry.KeyRegistry
import org.openmole.ide.core.implementation.sampling._
import scala.Some
import org.openmole.ide.core.implementation.data.FactorDataUI
import org.openmole.ide.core.implementation.workflow.{ ISceneContainer, MoleScene }

class SamplingSceneMenuProvider(panelScene: SamplingCompositionPanelUI) extends GenericMenuProvider {

  override def getPopupMenu(widget: Widget,
                            point: Point) = {
    items.clear
    val itAddFactor = new MenuItem(new Action("Add Domain") {
      def apply = {
        closeExtraPanel
        val domainFactories = KeyRegistry.domains.values
        panelScene.addDomain(DomainProxyUI(domainFactories.map {
          _.buildDataUI
        }.filter(_.name == "Range").headOption.getOrElse(domainFactories.head.buildDataUI)),
          point, true)
      }
    })

    val samplingMenu = new MenuItem(new Action("Add Sampling") {
      def apply = {
        closeExtraPanel
        val samplingFactories = KeyRegistry.samplings.values
        panelScene.addSampling(SamplingProxyUI(samplingFactories.map {
          _.buildDataUI
        }.filter(_.name == "Complete").headOption.getOrElse(samplingFactories.head.buildDataUI)),
          point, true)
      }
    })

    items += (itAddFactor.peer, samplingMenu.peer)
    super.getPopupMenu(widget, point)
  }

  def closeExtraPanel =
    ScenesManager().currentSceneContainer match {
      case Some(x: ISceneContainer) ⇒ x.scene.closePropertyPanel
      case _                        ⇒
    }
}
