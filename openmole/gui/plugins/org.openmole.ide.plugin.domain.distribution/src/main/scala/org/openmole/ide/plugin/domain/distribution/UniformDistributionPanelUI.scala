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

package org.openmole.ide.plugin.domain.distribution

import scala.swing._
import event.SelectionChanged
import org.openmole.ide.misc.widget.{ Help, URL, Helper, PluginPanel }
import java.util.{ Locale, ResourceBundle }
import org.openmole.ide.misc.tools.util.Types._
import org.openmole.ide.core.implementation.panelsettings.IDomainPanelUI

class UniformDistributionPanelUI(pud: UniformDistributionDataUI[_])(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends Publisher with IDomainPanelUI {

  val typeCombo = new MyComboBox(List(INT, LONG))
  val maxField = new TextField(pud.max.getOrElse("").toString, 6)

  val initialType = pud.availableTypes.head
  typeCombo.selection.item = initialType

  val maxPanel = new PluginPanel("wrap 2")

  val mainPanel = new PluginPanel("wrap 2") {
    contents += typeCombo
    contents += maxPanel
  }

  val components = List(("", mainPanel))

  setContents(initialType)

  listenTo(`typeCombo`)
  typeCombo.selection.reactions += {
    case SelectionChanged(`typeCombo`) ⇒ setContents(typeCombo.selection.item)
  }

  def setContents(t: String) = {
    maxPanel.contents.removeAll
    t match {
      case INT ⇒
        maxPanel.contents += (new Label("Max"), "gap para")
        maxPanel.contents += (maxField, "wrap")
      case LONG ⇒ maxPanel.contents += new Label("<html><i>No more information is required for this Domain</i></html>")
      case _    ⇒
    }
    mainPanel.revalidate
    mainPanel.repaint
  }

  def saveContent = UniformDistributionDataUI({
    if (maxField.text.isEmpty) scala.None else Some(maxField.text.toInt)
  }, typeCombo.selection.item)

  override lazy val help = new Helper(List(new URL(i18n.getString("permalinkText"), i18n.getString("permalink"))))

  add(maxField, new Help(i18n.getString("max"), i18n.getString("maxEx")))

}
