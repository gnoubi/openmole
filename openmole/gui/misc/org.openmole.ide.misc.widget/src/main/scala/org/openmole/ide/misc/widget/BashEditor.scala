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

package org.openmole.ide.misc.widget

import jsyntaxpane.lexers.BashLexer
import jsyntaxpane.DefaultSyntaxKit
import scala.swing.{ TextArea, EditorPane, ScrollPane }

class BashEditor extends ScrollPane {
  val editor = new TextArea
  editor.columns = 30
  editor.lineWrap = true
  viewportView = editor

  override def enabled_=(b: Boolean): Unit = {
    super.enabled = b
    editor.enabled = b
  }
}
