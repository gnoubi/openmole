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

package org.openmole.ide.plugin.task.tools

import java.util.Locale
import java.util.ResourceBundle
import org.openmole.ide.core.implementation.data.TaskDataUI
import org.openmole.ide.core.implementation.panelsettings.TaskPanelUI
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos
import org.openmole.ide.core.implementation.dataproxy.{ Proxies, PrototypeDataProxyUI }
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos.{ TwoCombosData, TwoCombosPanel }
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos._
import org.openmole.ide.misc.widget.multirow.RowWidget._
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.ide.misc.tools.util.Converters._

class AssignTaskPanelUI(pud: AssignTaskDataUI)(implicit val i18n: ResourceBundle = ResourceBundle.getBundle("help", new Locale("en", "EN"))) extends TaskPanelUI {

  val prototypes = Proxies.instance.prototypes

  val multiPrototypeCombo: MultiTwoCombos[PrototypeDataProxyUI, PrototypeDataProxyUI] =
    new MultiTwoCombos("Prototypes",
      prototypes,
      prototypes,
      "to ",
      pud.protos.map {
        s ⇒
          new TwoCombosPanel(prototypes,
            prototypes,
            "to ",
            new TwoCombosData(Some(s._1), Some(s._2)))
      },
      CLOSE_IF_EMPTY,
      ADD, MEDIUM)

  lazy val components = List(("Settings", multiPrototypeCombo.panel))

  def saveContent(name: String): TaskDataUI = new AssignTaskDataUI(name, multiPrototypeCombo.content.map { c ⇒ (c.comboValue1, c.comboValue2) })
}
