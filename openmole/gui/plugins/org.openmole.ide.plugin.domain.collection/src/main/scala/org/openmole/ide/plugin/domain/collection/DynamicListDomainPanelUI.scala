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

package org.openmole.ide.plugin.domain.collection

import org.openmole.ide.misc.widget.{ Help, URL, Helper, PluginPanel }
import swing.ScrollPane.BarPolicy._
import swing._
import java.awt.Color
import java.util.{ Locale, ResourceBundle }
import org.openmole.ide.core.implementation.panelsettings.IDomainPanelUI

class DynamicListDomainPanelUI(pud: DynamicListDomainDataUI[_])(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends IDomainPanelUI {

  val typeCombo = new MyComboBox(pud.availableTypes)
  val textArea = new TextArea(pud.values.mkString("\n"), 10, 20) {
    override val foreground = Color.black
  }

  typeCombo.selection.item = pud.domainType.toString.split('.').last

  val components = List(("", new PluginPanel("wrap") {
    contents += typeCombo
    contents += new ScrollPane(textArea) {
      horizontalScrollBarPolicy = Never
      verticalScrollBarPolicy = AsNeeded
    }
  }))

  def saveContent = DynamicListDomainDataUI(textArea.text.split('\n').toList,
    typeCombo.selection.item)

  override lazy val help = new Helper(List(new URL(i18n.getString("valueListPermalinkText"), i18n.getString("valueListPermalink"))))

  add(textArea, new Help(i18n.getString("valueList"), i18n.getString("valueListEx")))

}
