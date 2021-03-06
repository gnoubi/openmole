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

package org.openmole.ide.core.implementation.execution

import org.openmole.ide.core.implementation.workflow._
import java.awt._
import java.util.concurrent.atomic.AtomicInteger
import org.openmole.ide.core.implementation.data.{ IExplorationTaskDataUI, CheckData }
import org.openmole.ide.core.implementation.dialog.StatusBar
import org.openmole.ide.misc.widget.MigPanel
import org.openmole.ide.misc.tools.image.Images._
import scala.swing.Action
import scala.swing.Button
import scala.swing.Label
import scala.swing.TabbedPane
import org.openmole.misc.exception.UserBadDataError
import concurrent.stm._
import util.{ Failure, Success }
import scala.Some
import org.openmole.ide.core.implementation.dataproxy.{ TaskDataProxyUI, DataProxyUI }
import org.openmole.ide.core.implementation.sampling.SamplingCompositionPanelUI
import org.openmole.ide.core.implementation.panel.SamplingCompositionPanel
import scala.concurrent.ExecutionContext.Implicits.global
import org.openmole.ide.misc.tools.image.Images
import scala.util.Failure
import scala.Some
import scala.util.Success
import scala.List

object ScenesManager {
  val instance = new ScenesManager

  def apply() = instance
}

class ScenesManager {
  val tabPane = new TabbedPane {
    val centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment.getCenterPoint
    val (x, y) = (centerPoint.x - 125, centerPoint.y - 125)

    override def paintComponent(g: Graphics2D) = {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON)
      g.setColor(new Color(255, 255, 255, 128))
      g.setColor(new Color(120, 120, 120))
      g.setFont(new Font("Helvetica", Font.PLAIN, 25))
      g.drawString("No Moles are open", x, y)
      g.drawLine(x - 15, y + 8, x + 250, y + 8)
      g.setFont(new Font(g.getFont.getFamily, Font.PLAIN, 15))
      g.drawString("Create a new Mole, Ctrl + N", x, y + 25)
      g.drawString("Load a Project, Ctrl + L", x, y + 45)
      g.drawString("Create new Tasks, Prototypes, etc", x, y + 65)
      super.paintComponent(g)
    }
  }

  val statusBar = new StatusBar
  var countBuild = new AtomicInteger
  var countExec = new AtomicInteger
  val _selection: Ref[Option[List[CapsuleUI]]] = Ref(None)

  def invalidateSelection = _selection.single() = None

  def invalidateMoles = moleScenes.foreach {
    _.dataUI.invalidateCache
  }

  def selection = atomic {
    implicit actx ⇒
      _selection() match {
        case Some(_) ⇒
        case None ⇒
          _selection() = Some(capsules.filter {
            _.selected
          })
      }
      _selection().get
  }

  PasswordListner.apply

  def isInSelection(capsule: CapsuleUI) = selection.contains(capsule)

  def buildMoleSceneContainers = tabPane.pages.flatMap(_.content match {
    case x: BuildMoleSceneContainer ⇒ List(x)
    case _                          ⇒ Nil
  })

  def currentSceneContainer: Option[ISceneContainer] = {
    if (tabPane.peer.getTabCount == 0) None
    else tabPane.selection.page.content match {
      case x: ISceneContainer ⇒ Some(x)
      case _                  ⇒ None
    }
  }

  def currentScene = currentSceneContainer match {
    case Some(sc: ISceneContainer) ⇒ Some(sc.scene)
    case _                         ⇒ None
  }

  def displayExtraPropertyPanel(proxy: DataProxyUI) = {
    currentScene.getOrElse(addBuildSceneContainer("Mole").scene).displayPropertyPanel(proxy)
  }

  def currentPanels = List(currentScene).flatten.map {
    _.currentPanels
  }

  def closePropertyPanels = List(currentScene).flatten.map {
    _.closePropertyPanels
  }

  def currentSamplingCompositionPanelUI = currentPanels.map {
    _ match {
      case scp: SamplingCompositionPanel ⇒ Some(scp.panelSettings)
      case _                             ⇒ None
    }
  }

  def closePropertyPanel = List(currentScene).flatten.foreach {
    _.closePropertyPanels
  }

  def changeSelection(widget: CapsuleUI) = {
    widget.selected = !widget.selected
    invalidateSelection
  }

  def addToSelection(widget: CapsuleUI) = {
    widget.selected = true
    invalidateSelection
  }

  def removeFromSelection(widget: CapsuleUI) = {
    widget.selected = false
    invalidateSelection
  }

  def clearSelection = {
    selection.foreach {
      _.selected = false
    }
    invalidateSelection
  }

  def pasteCapsules(ms: BuildMoleScene,
                    point: Point) = {
    val copied = selection.map {
      z ⇒
        z -> z.copy(ms)
    }.toMap

    val dx = (point.x - selection.map {
      _.widget.getPreferredLocation.x
    }.min).toInt
    val dy = (point.y - selection.map {
      _.widget.getPreferredLocation.y
    }.min).toInt

    copied.foreach {
      case (old, neo) ⇒
        val p = new Point((old.widget.getPreferredLocation.x + dx).toInt, (old.widget.getPreferredLocation.y + dy).toInt)
        ms.add(neo._1, p)
        neo._1.environment_=(old.dataUI.environment)
        old.dataUI.task match {
          case Some(t: TaskDataProxyUI) ⇒ neo._1.encapsule(t)
          case _                        ⇒
        }
        ms.refresh
      case _ ⇒
    }

    val islots = selection.flatMap {
      _.inputSlots
    }
    selection.headOption match {
      case Some(c: CapsuleUI) ⇒
        val connectors = c.scene.dataUI.connectors.values.toList
        connectors.foreach {
          con ⇒
            if (selection.contains(con.source) && islots.contains(con.target)) {
              con match {
                case (t: TransitionUI) ⇒
                  val transition = new TransitionUI(
                    copied(t.source)._1,
                    copied(t.target.capsule)._1.inputSlots.find {
                      s ⇒ t.target.index == s.index
                    }.get,
                    t.transitionType,
                    t.condition,
                    t.filteredPrototypes)
                  ms.add(transition)
                case (t: DataChannelUI) ⇒
                  val dc = new DataChannelUI(
                    copied(t.source)._1,
                    copied(t.target.capsule)._1.inputSlots.find {
                      s ⇒ t.target.index == s.index
                    }.get,
                    t.filteredPrototypes)
                  ms.add(dc)
              }
            }
        }
        ms.refresh
      case _ ⇒
    }
  }

  def closeAll = tabPane.pages.clear

  def saveCurrentPropertyWidget = currentSceneContainer match {
    case Some(x: ISceneContainer) ⇒ x.scene.savePropertyPanel(0)
    case _                        ⇒ None
  }

  def invalidateSceneCaches = moleScenes foreach {
    _.dataUI.invalidateCache
  }

  def refreshScenes = moleScenes foreach {
    _.refresh
  }

  def moleScenes = buildMoleSceneContainers.map {
    _.scene
  }

  def capsules: List[CapsuleUI] = moleScenes.map {
    _.dataUI.capsules.values
  }.toList.flatten

  def capsules(p: TaskDataProxyUI) = moleScenes.flatMap {
    _.dataUI.capsules.values
  }.filter {
    _.dataUI.task.isDefined
  }.filter {
    p == _.dataUI.task.get
  }

  def explorationCapsules = moleScenes.flatMap {
    _.dataUI.capsules.values
  }.filter {
    _.dataUI.task.isDefined
  }.flatMap {
    c ⇒
      c.dataUI.task.get.dataUI match {
        case x: IExplorationTaskDataUI ⇒ List((c, x))
        case _                         ⇒ Nil
      }
  }.toList

  def transitions = moleScenes.flatMap {
    _.dataUI.connectors.values
  }.flatMap {
    _ match {
      case t: TransitionUI ⇒ Some(t)
      case _               ⇒ None
    }
  }
    .toList

  def addBuildSceneContainer: BuildMoleSceneContainer = addBuildSceneContainer(BuildMoleScene(""))

  def addBuildSceneContainer(name: String): BuildMoleSceneContainer = addBuildSceneContainer(BuildMoleScene(name))

  def addBuildSceneContainer(ms: BuildMoleScene): BuildMoleSceneContainer = {
    val container = new BuildMoleSceneContainer(ms)
    val page = new TabbedPane.Page(ms.dataUI.name, container)
    addTab(page, ms.dataUI.name, new Action("") {
      def apply = {
        tabPane.pages.remove(page.index)
        // container.stopAndCloseExecutions
      }
    })
    container
  }

  def addExecutionSceneContainer(bmsc: BuildMoleSceneContainer) =
    CheckData.fullCheck(bmsc.scene) match {
      case Success(_) ⇒
        if (StatusBar().isValid) {
          val clone = bmsc.scene.copyScene
          clone.dataUI.name = {
            bmsc.scene.dataUI.name + "_" + countExec.incrementAndGet
          }
          val page = new TabbedPane.Page(clone.dataUI.name, new MigPanel(""))
          val container = new ExecutionMoleSceneContainer(clone, page, bmsc)
          page.content = container

          addTab(page, clone.dataUI.name, new Action("") {
            def apply = {
              container.stop
              container.save
              tabPane.pages.remove(page.index)
            }
          })

          tabPane.selection.index = page.index
        }
        else
          StatusBar().block("The Mole can not be built due to the previous errors")
      case Failure(t: Throwable) ⇒ StatusBar().block(t.getMessage)
    }

  def addTab(page: TabbedPane.Page, title: String, action: Action) = {
    tabPane.pages += page
    tabPane.peer.setTabComponentAt(tabPane.peer.getTabCount - 1, new CloseableTab(title, action).peer)
    tabPane.selection.page = page
  }

  class CloseableTab(title: String,
                     action: Action) extends MigPanel("") {
    background = new Color(0, 0, 0, 0)
    contents += new Label(title)
    contents += new Button(action) {
      preferredSize = new Dimension(20, 20)
      maximumSize = new Dimension(20, 20)
      icon = CLOSE_TAB
    }
  }

}