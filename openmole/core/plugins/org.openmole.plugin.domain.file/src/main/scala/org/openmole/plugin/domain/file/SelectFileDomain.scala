/*
 * Copyright (C) 2012 reuillon
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

package org.openmole.plugin.domain.file

import java.io.File
import org.openmole.core.model.data._
import org.openmole.core.model.domain._
import org.openmole.core.implementation.tools._

object SelectFileDomain {
  def apply(base: File, path: String) = new SelectFileDomain(base, path)
}

class SelectFileDomain(val base: File, val path: String) extends Domain[File] with Finite[File] {

  override def computeValues(context: Context): Iterable[File] =
    List(new File(base, VariableExpansion(context, path)))

}
