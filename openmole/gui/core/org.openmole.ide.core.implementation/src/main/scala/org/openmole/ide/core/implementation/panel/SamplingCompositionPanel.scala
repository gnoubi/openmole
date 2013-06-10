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

package org.openmole.ide.core.implementation.panel

import java.awt.Dimension
import java.awt.BorderLayout
import javax.swing._
import javax.swing.ScrollPaneConstants._
import org.openmole.ide.core.implementation.execution.ScenesManager
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import org.openmole.ide.core.implementation.dataproxy.{ UpdatedProxyEvent, Proxies }
import org.openmole.ide.core.implementation.dialog.DialogFactory
import org.openmole.ide.core.model.dataproxy.{ IDataProxyUI, ISamplingCompositionDataProxyUI }
import org.openmole.ide.core.model.workflow.IMoleScene
import org.openmole.ide.misc.widget.{ MainLinkLabel, PluginPanel }
import org.netbeans.api.visual.widget.Scene
import org.openmole.ide.misc.widget.multirow.ComponentFocusedEvent
import scala.swing.Component
import scala.swing.Action
import scala.swing.event.FocusGained

class SamplingCompositionPanel(proxy: ISamplingCompositionDataProxyUI,
                               scene: IMoleScene,
                               val index: Int) extends BasePanel(Some(proxy), scene) { sCPanel ⇒
  iconLabel.icon = new ImageIcon(ImageIO.read(proxy.dataUI.getClass.getClassLoader.getResource(proxy.dataUI.fatImagePath)))
  val panelUI = proxy.dataUI.buildPanelUI
  def created = Proxies.instance.contains(proxy)

  tabbedPane.preferredSize = new Dimension(0, 0)

  mainPanel.contents += new NewConceptPanel(this) { addPrototype }

  peer.add(mainPanel.peer, BorderLayout.NORTH)
  peer.add(new PluginPanel("wrap") {
    contents += tabbedPane
    contents += panelUI.help

    panelUI match {
      case s: Scene ⇒ peer.add(new JScrollPane(s.createView) {
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER)
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER)
      })
      case _ ⇒
    }
  }.peer, BorderLayout.CENTER)

  listenTo(nameTextField)
  listenTo(panelUI.help.components.toSeq: _*)
  reactions += {
    case FocusGained(source: Component, _, _)     ⇒ panelUI.help.switchTo(source)
    case ComponentFocusedEvent(source: Component) ⇒ panelUI.help.switchTo(source)
    case UpdatedProxyEvent(p: IDataProxyUI, _) ⇒
      scene.removeAll(index + 1)
  }

  def create = {
    Proxies.instance += proxy
    ConceptMenu.samplingMenu.popup.contents += ConceptMenu.addItem(nameTextField.text, proxy)
  }

  def delete = {
    val toBeRemovedSamplings = ScenesManager.explorationCapsules.filter { case (c, d) ⇒ d.sampling == Some(proxy) }
    toBeRemovedSamplings match {
      case Nil ⇒
        scene.closePropertyPanel(index)
        Proxies.instance -= proxy
        ConceptMenu.removeItem(proxy)
        true
      case _ ⇒
        if (DialogFactory.deleteProxyConfirmation(proxy)) {
          toBeRemovedSamplings.foreach { case (c, d) ⇒ c.scene.graphScene.removeNodeWithEdges(c.scene.manager.removeCapsuleUI(c)) }
          delete
        }
        else false
    }
  }

  def save = proxy.dataUI = panelUI.saveContent(nameTextField.text)
}