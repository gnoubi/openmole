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

package org.openmole.ide.plugin.domain.range

import scala.math.Numeric._
import org.openmole.ide.misc.widget.{ URL, Help, Helper, PluginPanel }
import scala.swing.event._
import scala.swing._
import org.openmole.ide.misc.tools.util.Types
import scala.swing.event.ButtonClicked
import scala.Some

class RangeDomainPanelUI(pud: RangeDomainDataUI[_]) extends GenericRangeDomainPanelUI with Publisher {

  minField.text = pud.min
  maxField.text = pud.max
  typeCombo.peer.setModel(MyComboBox.newConstantModel(pud.availableTypes))
  typeCombo.selection.item = pud.domainType.toString.split('.').last

  val stepCheckBox = new CheckBox("Step")
  val stepField = new TextField(6) {
    text = pud.step.getOrElse("")
  }

  stepField.visible = pud.step.isDefined
  stepCheckBox.selected = pud.step.isDefined

  listenTo(`stepCheckBox`)
  reactions += {
    case ButtonClicked(`stepCheckBox`) ⇒ stepField.visible = stepCheckBox.selected
  }

  logPanel.contents += (stepCheckBox, "gap para")
  logPanel.contents += stepField

  val components = List(("", logPanel))

  help.add(stepField, new Help(i18n.getString("step"), i18n.getString("stepEx")))

  def stepContent: Option[String] = {
    if (stepCheckBox.selected) {
      if (stepField.text.isEmpty) None
      else Some(stepField.text)
    }
    else None
  }

  def saveContent = GenericRangeDomainDataUI(minField.text,
    maxField.text,
    stepContent,
    false,
    typeCombo.selection.item)
}
