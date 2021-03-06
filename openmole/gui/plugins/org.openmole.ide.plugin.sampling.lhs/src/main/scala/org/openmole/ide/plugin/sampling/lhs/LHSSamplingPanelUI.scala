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

package org.openmole.ide.plugin.sampling.lhs

import scala.swing._
import java.util.Locale
import java.util.ResourceBundle
import org.openmole.ide.core.implementation.registry.KeyRegistry
import org.openmole.ide.misc.widget.Help
import org.openmole.ide.misc.widget.Helper
import org.openmole.ide.misc.widget.PluginPanel
import org.openmole.ide.misc.widget.URL
import org.openmole.ide.core.implementation.panelsettings.ISamplingPanelUI

class LHSSamplingPanelUI(cud: LHSSamplingDataUI)(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends ISamplingPanelUI {

  val sampleTextField = new TextField(cud.samples, 8)

  val components = List(("", new PluginPanel("wrap 2", "", "") {
    contents += new Label("Samples")
    contents += sampleTextField
  }))

  def domains = KeyRegistry.domains.values.map {
    _.buildDataUI
  }.toList

  override def saveContent = new LHSSamplingDataUI(sampleTextField.text)

  override lazy val help = new Helper(List(new URL(i18n.getString("permalinkText"), i18n.getString("permalink"))))

  add(sampleTextField, new Help(i18n.getString("sample"), i18n.getString("sampleEx")))
}
