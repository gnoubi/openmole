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

package org.openmole.ide.core.implementation.data

import org.openmole.core.model.data._
import org.openmole.ide.misc.tools.util.Types
import scala.util.Try
import org.openmole.ide.core.implementation.prototype.GenericPrototypePanelUI

trait PrototypeDataUI[T] extends DataUI with ImageView {
  def name: String

  override def toString = if (dim > 0) Types.pretify(name) + "[" + dim + "]" else Types.pretify(name)

  def `type`: Manifest[T]

  def typeClassString: String

  def coreObject: Try[Prototype[T]]

  def dim: Int

  def buildPanelUI: GenericPrototypePanelUI
}
