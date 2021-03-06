/*
 * Copyright (C) 2011 <mathieu.Mathieu Leclaire at openmole.org>
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

package org.openmole.ide.plugin.environment.ssh

import java.util.Locale
import java.util.ResourceBundle
import org.openmole.ide.misc.widget.Help
import org.openmole.ide.misc.widget.Helper
import org.openmole.ide.misc.widget.PluginPanel
import org.openmole.ide.misc.widget.URL
import scala.swing.Label
import scala.swing.TextField
import java.awt.Dimension
import org.openmole.ide.core.implementation.panelsettings.EnvironmentPanelUI

class SSHEnvironmentPanelUI(pud: SSHEnvironmentDataUI)(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends PluginPanel("fillx", "[left][grow,fill]", "") with EnvironmentPanelUI {

  implicit def stringToStringOpt(s: String) = s.isEmpty match {
    case true  ⇒ None
    case false ⇒ Some(s)
  }

  implicit def stringToIntOpt(s: String) = try {
    Some(s.toInt)
  }
  catch {
    case e: NumberFormatException ⇒ None
  }

  implicit def stringToInt(s: String) = try {
    s.toInt
  }
  catch {
    case e: NumberFormatException ⇒ 0
  }

  val loginTextField = new TextField(pud.login, 15)
  val hostTextField = new TextField(pud.host, 15)
  val nbSlotTextField = new TextField(pud.nbSlots.toString, 15)
  val portTextField = new TextField(pud.port.toString, 15)
  val dirTextField = new TextField(pud.dir, 15)
  val openMOLEMemoryTextField = new TextField(pud.openMOLEMemory.getOrElse("").toString, 15)
  val threadTextField = new TextField(pud.threads.getOrElse("").toString, 15)

  val components = List(("Settings", new PluginPanel("wrap 2") {
    minimumSize = new Dimension(300, 200)
    contents += (new Label("Login"), "gap para")
    contents += loginTextField

    contents += (new Label("Host"), "gap para")
    contents += hostTextField

    contents += (new Label("Port"), "gap para")
    contents += portTextField

    contents += (new Label("Number of slots"), "gap para")
    contents += nbSlotTextField

    contents += (new Label("Directory"), "gap para")
    contents += dirTextField
  }), ("Options", new PluginPanel("wrap 2") {
    contents += (new Label("Runtime memory"), "gap para")
    contents += openMOLEMemoryTextField

    contents += (new Label("Threads"), "gap para")
    contents += threadTextField
  }))

  override lazy val help = new Helper(List(new URL(i18n.getString("permalinkText"), i18n.getString("permalink"))))
  add(loginTextField,
    new Help(i18n.getString("login"),
      i18n.getString("loginEx")))
  add(hostTextField,
    new Help(i18n.getString("host"),
      i18n.getString("hostEx")))
  add(nbSlotTextField,
    new Help(i18n.getString("nbSlot"),
      i18n.getString("nbSlotEx")))
  add(dirTextField,
    new Help(i18n.getString("category"),
      i18n.getString("dirEx")))
  add(openMOLEMemoryTextField,
    new Help(i18n.getString("runtimeMemory"),
      i18n.getString("runtimeMemoryEx")))

  override def saveContent(name: String) = new SSHEnvironmentDataUI(name,
    loginTextField.text,
    hostTextField.text,
    nbSlotTextField.text,
    portTextField.text,
    dirTextField.text,
    openMOLEMemoryTextField.text,
    threadTextField.text)
}