/*
 * Copyright (C) 2012 Mathieu Leclaire 
 * < mathieu.leclaire at openmole.org >
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
package org.openmole.ide.plugin.domain.file

import swing.{ TextField, Label }
import org.openmole.ide.misc.widget.{ Help, URL, Helper, PluginPanel }
import java.util.{ Locale, ResourceBundle }
import org.openmole.ide.core.implementation.panelsettings.IDomainPanelUI

class SelectFileDomainPanelUI(val dataUI: SelectFileDomainDataUI)(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends IDomainPanelUI with FileDomainPanelUI {

  val dirTField = directoryTextField(dataUI.directoryPath)
  val pathTextField = new TextField(8) { text = dataUI.path }

  val sfpanel = new PluginPanel("wrap") {
    contents += FileDomainPanelUI.panel(List((dirTField, "Directory"), (pathTextField, "Reg Exp")))
  }

  val components = List(("", sfpanel))

  override def toString = dataUI.name

  def saveContent = new SelectFileDomainDataUI(dirTField.text, pathTextField.text)

  override lazy val help = new Helper(List(new URL(i18n.getString("permalinkText"), i18n.getString("permalink"))))

  add(dirTField, new Help(i18n.getString("category"), i18n.getString("dirEx")))
  add(pathTextField, new Help(i18n.getString("singleFilePath"), i18n.getString("singleFilePathEx")))

}