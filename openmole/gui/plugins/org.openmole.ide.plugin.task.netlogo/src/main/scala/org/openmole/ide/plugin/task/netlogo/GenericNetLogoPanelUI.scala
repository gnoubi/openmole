/*
 * Copyright (C) 2011 <mathieu.Mathieu Leclaire at openmole.org>
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
package org.openmole.ide.plugin.task.netlogo

import org.openmole.ide.core.implementation.dialog.StatusBar
import org.openmole.ide.misc.widget.ChooseFileTextField
import org.openmole.ide.misc.widget.multirow.RowWidget._
import org.openmole.ide.misc.widget.multirow.MultiChooseFileTextField
import org.openmole.ide.misc.widget.multirow.MultiChooseFileTextField._
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos._
import org.openmole.ide.core.implementation.dataproxy._
import java.util.Locale
import java.util.ResourceBundle
import org.openmole.ide.misc.widget.URL
import org.openmole.ide.misc.widget.Help
import org.openmole.ide.misc.widget.Helper
import org.openmole.ide.misc.widget.PluginPanel
import scala.swing._
import org.openmole.ide.osgi.netlogo.NetLogo
import scala.swing.FileChooser._
import java.io.File
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import scala.concurrent.stm.Ref
import scala.concurrent.stm
import org.openmole.ide.core.implementation.panelsettings.TaskPanelUI
import scala.Some
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import org.openmole.plugin.task.netlogo.NetLogoTask.Workspace

abstract class GenericNetLogoPanelUI(
    workspace: Workspace,
    lauchingCommands: String,
    prototypeMappingInput: List[(PrototypeDataProxyUI, String, Int)],
    prototypeMappingOutput: List[(String, PrototypeDataProxyUI, Int)],
    resources: List[String])(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends PluginPanel("") with TaskPanelUI {

  val (nlogoPath, workspaceEmbedded) = Util.fromWorkspace(workspace)

  val nlogoTextField = new ChooseFileTextField(nlogoPath,
    "Select a nlogo file",
    "Netlogo files",
    "nlogo",
    updateGlobals)

  val workspaceCheckBox = new CheckBox("Embed Workspace") {
    selected = workspaceEmbedded
  }

  val launchingCommandTextArea = new TextArea(lauchingCommands)

  var multiStringProto = new MultiTwoCombos[String, PrototypeDataProxyUI]("", List(), List(), "with", Seq())
  var multiProtoString = new MultiTwoCombos[PrototypeDataProxyUI, String]("", List(), List(), "with", Seq())
  val resourcesMultiTextField = new MultiChooseFileTextField("",
    resources.map {
      r ⇒ new ChooseFileTextFieldPanel(new ChooseFileTextFieldData(r))
    },
    selectionMode = SelectionMode.FilesAndDirectories,
    minus = CLOSE_IF_EMPTY)

  private def updateGlobals = {
    _globalsReporters.single() = None
    publish(UpdatedProxyEvent.task(this))
  }

  private val _globalsReporters = Ref(Option.empty[(Seq[String], Seq[String])])

  private val inputMappingPanel = new PluginPanel("")
  private val outputMappingPanel = new PluginPanel("")

  updateIOPanel

  private def updateIOPanel = future {
    StatusBar().inform("Reading the netlogo file ...")
    inputMappingPanel.contents += new Label("<html><i>Loading...</i></html>")
    outputMappingPanel.contents += new Label("<html><i>Loading...</i></html>")
    val (i, o) = buildMultis
    if (inputMappingPanel.contents.size > 0) inputMappingPanel.contents.remove(0, 1)
    if (outputMappingPanel.contents.size > 0) outputMappingPanel.contents.remove(0, 1)
    inputMappingPanel.contents += i
    outputMappingPanel.contents += o
    inputMappingPanel.revalidate
    outputMappingPanel.revalidate
    inputMappingPanel.repaint
    outputMappingPanel.repaint
    revalidate
    repaint
    StatusBar.clear
  }

  private def globalsReporters = stm.atomic {
    implicit ctx ⇒
      val path = nlogoTextField.text
      if (_globalsReporters().isEmpty && (new File(path)).isFile) {
        val nl = buildNetLogo
        nl.open(path)
        _globalsReporters() = Some((nl.globals.toList, nl.reporters.toList))
        nl.dispose
      }
      _globalsReporters()
  }

  val components = List(("Settings",
    new PluginPanel("", "[left]rel[grow,fill]", "[]20[]") {
      contents += new Label("Nlogo file")
      contents += (nlogoTextField, "growx,wrap")
      contents += (new Label("Commands"), "wrap")
      contents += (new ScrollPane(launchingCommandTextArea) {
        minimumSize = new Dimension(450, 200)
      }, "span,growx")
    }),
    ("Input mapping", inputMappingPanel),
    ("Output mapping", outputMappingPanel),
    ("Resources", new PluginPanel("wrap") {
      contents += (workspaceCheckBox, "span,growx,wrap")
      contents += resourcesMultiTextField.panel
    }))

  def buildMultis: (Component, Component) = {
    try {
      globalsReporters match {
        case Some((globals, reporters)) ⇒
          if (!(globals ++ reporters).isEmpty && !comboContent.isEmpty) {
            multiStringProto = new MultiTwoCombos[String, PrototypeDataProxyUI](
              "",
              globals ++ reporters,
              comboContent,
              "with",
              prototypeMappingOutput.map {
                m ⇒ new TwoCombosPanel(globals, comboContent, "with", new TwoCombosData(Some(m._1), Some(m._2)))
              },
              minus = CLOSE_IF_EMPTY, insets = MEDIUM)

            multiProtoString = new MultiTwoCombos[PrototypeDataProxyUI, String](
              "",
              comboContent,
              globals,
              "with",
              prototypeMappingInput.map {
                m ⇒ new TwoCombosPanel(comboContent, globals, "with", new TwoCombosData(Some(m._1), Some(m._2)))
              },
              minus = CLOSE_IF_EMPTY, insets = MEDIUM)
          }
        case None ⇒
      }
    }
    catch {
      case e: Throwable ⇒
        StatusBar().block(e.getMessage, stack = e.getStackTraceString)
    }
    (multiProtoString.panel, multiStringProto.panel)
  }

  def comboContent: List[PrototypeDataProxyUI] = Proxies.instance.prototypes.toList

  def buildNetLogo: NetLogo

  override lazy val help = new Helper(List(new URL(i18n.getString("permalinkText"), i18n.getString("permalink"))))
  add(nlogoTextField,
    new Help(i18n.getString("nlogoPath"),
      i18n.getString("nlogoPathEx"),
      List(new URL(i18n.getString("nlogoURLtext"), i18n.getString("nlogoURL")))))
  add(workspaceCheckBox,
    new Help(i18n.getString("embedWorkspace"),
      i18n.getString("embedWorkspaceEx")))
  add(launchingCommandTextArea,
    new Help(i18n.getString("command"),
      i18n.getString("commandEx")))
  add(resourcesMultiTextField,
    new Help(i18n.getString("resources"),
      i18n.getString("resourcesEx")))

}
