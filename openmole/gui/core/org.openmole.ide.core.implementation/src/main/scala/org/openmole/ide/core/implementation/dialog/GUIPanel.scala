/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.core.implementation.dialog

import scala.swing._
import org.openmole.ide.misc.widget.MigPanel
import java.awt.{ Toolkit, BorderLayout, Event }
import org.openmole.ide.core.implementation.execution._
import java.awt.event.KeyEvent
import javax.swing.KeyStroke
import org.openide.DialogDescriptor
import org.openide.DialogDisplayer
import org.openide.NotifyDescriptor
import org.openide.NotifyDescriptor._
import org.openmole.misc.pluginmanager.PluginManager
import org.openmole.misc.workspace.Workspace
import java.io.File
import org.openmole.ide.core.implementation.preference.{ Preferences, PreferenceContent }
import org.openmole.ide.core.implementation.execution.ScenesManager
import org.openmole.ide.core.implementation.action.LoadXML
import org.openmole.ide.core.implementation.action.SaveXML
import org.openmole.ide.core.implementation.dataproxy.Proxies
import org.openmole.ide.misc.widget.PluginPanel
import org.openmole.ide.misc.widget.multirow.MultiChooseFileTextField
import org.openmole.ide.misc.widget.multirow.MultiChooseFileTextField._
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.misc.tools.io.FileUtil._
import org.openmole.misc.tools.obj.ClassUtils._
import org.openmole.ide.core.implementation.workflow.BuildMoleScene
import org.openmole.ide.core.implementation.panel.ConceptMenu

class GUIPanel extends MainFrame {
  mainframe ⇒
  title = "OpenMOLE"

  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(new Action("New Mole") {
        override def apply = DialogFactory.newTabName

        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK))
      })

      contents += new Menu("Recent files") {
        Preferences().recentFiles.foreach {
          rf ⇒
            val f = new File(rf)
            if (f.isFile) {
              contents += new MenuItem(new Action(f.getName) {
                override def apply = {
                  Proxies.instance.clearAll
                  mainframe.title = "OpenMOLE - " + LoadXML.tryFile(rf)
                }
              })
            }
        }
      }

      contents += new MenuItem(new Action("Load") {
        override def apply = {
          Proxies.instance.clearAll
          mainframe.title = "OpenMOLE - " + LoadXML.show
        }

        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK))
      })

      contents += new MenuItem(new Action("Import") {
        override def apply = LoadXML.show

        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK))
      })

      contents += new MenuItem(new Action("Save") {
        override def apply = {
          ScenesManager().saveCurrentPropertyWidget
          SaveXML.save(mainframe)
        }

        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK))
      })

      contents += new MenuItem(new Action("Save as") {
        override def apply = SaveXML.save(mainframe, SaveXML.show)
      })

      contents += new MenuItem(new Action("Export") {
        override def apply = ScenesManager().currentScene match {
          case Some(s: BuildMoleScene) ⇒ DialogFactory.exportPartialMoleExecution(s)
          case _                       ⇒ StatusBar().inform("No mole available for export")
        }
      })

      contents += new MenuItem(new Action("Reset all") {
        override def apply = {
          ScenesManager().closeAll
          Proxies.instance.clearAll
          mainframe.title = "OpenMOLE"
          Settings.currentProject = None
        }
      })
    }

    contents += new Menu("Tools") {
      contents += new MenuItem(new Action("Preferences") {
        override def apply = {
          val pc = new PreferenceContent
          val dd = new DialogDescriptor(pc.peer, "Preferences")
          dd.setOptions(List(OK_OPTION).toArray)
          if (DialogDisplayer.getDefault.notify(dd).equals(OK_OPTION)) pc.save
        }
      })
      contents += new MenuItem(new Action("Plugins") {
        override def apply = {
          val pluginPanel = new PlatformPluginPanel
          if (DialogDisplayer.getDefault.notify(new DialogDescriptor(new ScrollPane(pluginPanel) {
            verticalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
          }.peer,
            "Plugins")).equals(NotifyDescriptor.OK_OPTION)) {
            pluginPanel.saveContent
          }
        }
      })
      contents += new MenuItem(new Action("About OpenMOLE") {
        override def apply = DialogFactory.displaySplashScreen
      })
    }
  }

  peer.setLayout(new BorderLayout)

  peer.add((new MigPanel("") {
    contents += ConceptMenu.prototypeMenu
    contents += ConceptMenu.taskMenu
    contents += ConceptMenu.samplingMenu
    contents += ConceptMenu.environmentMenu
    contents += ConceptMenu.hookMenu
    contents += ConceptMenu.sourceMenu
  }).peer, BorderLayout.NORTH)

  val splitPane = new SplitPane(Orientation.Horizontal, ScenesManager().tabPane, ScenesManager().statusBar)
  splitPane.resizeWeight = 1
  //splitPane.peer.setResizeWeight(1 - (50.0 / Toolkit.getDefaultToolkit.getScreenSize.height))

  peer.add(splitPane.peer, BorderLayout.CENTER)
  StatusBar().inform("OpenMOLE - 0.10 - Gogo Gadget")

  PasswordListner.apply

  class PlatformPluginPanel extends PluginPanel("") {
    minimumSize = new Dimension(300, 400)
    preferredSize = new Dimension(300, 400)
    val pluginDirPath = Workspace.pluginDirLocation.getCanonicalPath
    val pluginMultiTextField = new MultiChooseFileTextField("Plugin",
      Workspace.pluginDirLocation.list.map {
        p ⇒ new ChooseFileTextFieldPanel(new ChooseFileTextFieldData(p))
      }.toList, minus = CLOSE_IF_EMPTY)
    contents += pluginMultiTextField.panel

    def saveContent = {
      val requiredFiles = Proxies.instance.prototypes.map {
        p ⇒ PluginManager.fileProviding(toClass(p.dataUI.typeClassString)) -> p
      }.flatMap {
        case (o, p) ⇒ o.map {
          _.getCanonicalFile -> p
        }
      }
      pluginMultiTextField.content.foreach {
        path ⇒
          val plugin = new File(path.content)
          val targetFile = new File(Workspace.pluginDirLocation + "/" + plugin.getName)
          if (!targetFile.exists) {
            if (plugin.exists) {
              if (!targetFile.exists) {
                plugin.copy(targetFile)
                PluginManager.load(targetFile)
              }
            }
            else StatusBar().warn("The file " + path.content + " does not exist. It has not been imported")
          }
          val a = Workspace.pluginDirLocation.list.map {
            f ⇒ new File(Workspace.pluginDirLocation + "/" + f)
          }
          val b = pluginMultiTextField.content.map {
            c ⇒ new File(Workspace.pluginDirLocation + "/" + new File(c.content).getName)
          }
          a diff b foreach {
            f ⇒
              val allDepending = PluginManager.allDepending(f).map {
                _.getCanonicalFile
              }
              requiredFiles.filter(x ⇒ allDepending.toList.contains(x._1)).foreach {
                case (fi, p) ⇒
                  Proxies.instance -= p
                  ConceptMenu.-=(p)
                  unloadAndDelete(fi)
                case _ ⇒
              }
              unloadAndDelete(f)
          }
      }
    }
  }

  def unloadAndDelete(f: File) = {
    PluginManager.unload(f)
    f.delete
  }
}
