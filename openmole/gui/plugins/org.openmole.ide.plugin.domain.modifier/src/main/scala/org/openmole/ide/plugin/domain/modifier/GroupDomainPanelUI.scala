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

package org.openmole.ide.plugin.domain.modifier

import java.util.Locale
import java.util.ResourceBundle
import org.openmole.ide.misc.tools.util.Types._
import org.openmole.ide.misc.tools.util.ClassLoader._
import org.openmole.ide.misc.widget.{ URL, Help, Helper, PluginPanel }
import swing.{ MyComboBox, TextField }
import org.openmole.ide.core.implementation.execution.ScenesManager
import org.openmole.ide.core.implementation.data.DomainDataUI
import org.openmole.ide.core.implementation.panelsettings.IDomainPanelUI

class GroupDomainPanelUI(pud: GroupDomainDataUI[_])(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends IDomainPanelUI {

  val sizeTextField = new TextField(pud.size, 6)

  val components = List(("", new PluginPanel("wrap") {
    contents += sizeTextField
  }))

  def saveContent = {
    GroupDomainDataUI(sizeTextField.text, ModifierDomainDataUI.computeClassString(pud), pud.previousDomain)
  }

  override lazy val help = new Helper(List(new URL(i18n.getString("groupPermalinkText"), i18n.getString("groupPermalink"))))

  add(sizeTextField, new Help(i18n.getString("groupSize"), i18n.getString("groupSizeEx")))

}
