/*
 * Copyright (C) 2012 mathieu
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

package org.openmole.ide.misc.widget.multirow

import org.openmole.ide.misc.widget._
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.ide.misc.widget.multirow.RowWidget._
import scala.swing.MyComboBox

object MultiCombo {

  class ComboPanel[B](val comboContent: Seq[B],
                      val data: ComboData[B]) extends PluginPanel("wrap 2") with IPanel[ComboData[B]] {

    val comboBox = ContentComboBox(comboContent.sortBy { _.toString }.toList, data.comboValue)
    contents += comboBox.widget

    def content = new ComboData(comboBox.widget.selection.item.content)
  }

  class ComboData[B](val comboValue: Option[B] = None) extends IData

  class ComboFactory[B](comboContent: Seq[B]) extends IFactory[ComboData[B]] {
    def apply = new ComboPanel(comboContent, new ComboData)
  }
}

import MultiCombo._
class MultiCombo[B](title: String,
                    comboContent: Seq[B],
                    initPanels: Seq[ComboPanel[B]],
                    minus: Minus = NO_EMPTY,
                    plus: Plus = ADD) extends MultiPanel(title,
  new ComboFactory(comboContent),
  initPanels,
  minus,
  plus)