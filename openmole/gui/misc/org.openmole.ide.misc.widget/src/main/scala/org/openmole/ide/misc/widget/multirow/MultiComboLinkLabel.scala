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

import javax.swing._
import org.openmole.ide.misc.widget.ContentAction
import org.openmole.ide.misc.widget.LinkLabel
import org.openmole.ide.misc.widget.ContentAction
import org.openmole.ide.misc.widget._
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.ide.misc.widget.multirow.RowWidget.Plus
import org.openmole.ide.misc.widget.multirow.RowWidget._
import scala.swing.MyComboBox
import scala.swing.event.SelectionChanged

object MultiComboLinkLabel {

  class ComboLinkLabelPanel[A](val comboContent: Seq[(A, ContentAction[A])],
                               val image: Icon,
                               val data: ComboLinkLabelData[A]) extends PluginPanel("wrap 2") with IPanel[ComboLinkLabelData[A]] {
    val filterComboBox = FilterComboBox(comboContent.sortBy { _._1.toString }.map(c ⇒ c._1))
    data.content match {
      case Some(x: A) ⇒
        filterComboBox.combo.selection.item = x
      case _ ⇒
    }

    val linkLabel = new LinkLabel("", action) { icon = image }

    contents += filterComboBox
    contents += linkLabel

    listenTo(filterComboBox.combo)
    filterComboBox.combo.selection.reactions += {
      case SelectionChanged(filterComboBox.combo) ⇒
        linkLabel.action = action
    }

    def action = comboContent.filter { cc ⇒ cc._1 == filterComboBox.combo.selection.item }.head._2

    def content = new ComboLinkLabelData(Some(filterComboBox.combo.selection.item))
  }

  class ComboLinkLabelData[A](val content: Option[A] = None) extends IData

  class ComboLinkLabelFactory[A](comboContent: Seq[(A, ContentAction[A])],
                                 image: Icon) extends IFactory[ComboLinkLabelData[A]] {
    def apply = new ComboLinkLabelPanel(comboContent, image, new ComboLinkLabelData)
  }
}

import MultiComboLinkLabel._
class MultiComboLinkLabel[A](title: String,
                             comboContent: Seq[(A, ContentAction[A])],
                             initPanels: Seq[ComboLinkLabelPanel[A]],
                             image: Icon,
                             minus: Minus = NO_EMPTY,
                             plus: Plus = ADD,
                             insets: RowInsets = REGULAR) extends MultiPanel(title,
  new ComboLinkLabelFactory(comboContent, image),
  initPanels,
  minus,
  plus,
  insets)