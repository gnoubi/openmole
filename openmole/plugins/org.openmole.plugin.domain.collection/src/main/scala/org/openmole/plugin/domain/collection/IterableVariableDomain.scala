/*
 * Copyright (C) 2010 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.domain.collection

import org.openmole.core.model.data.Context
import org.openmole.core.model.data.Prototype
import org.openmole.core.model.domain.IDomain

import org.openmole.core.model.domain.IIterable
import scala.collection.JavaConversions._

sealed class IterableVariableDomain[T](variable: Prototype[java.lang.Iterable[_ <: T]]) extends IDomain[T] with IIterable[T] {

  override def iterator(context: Context): Iterator[T] = context.valueOrException(variable).iterator

}
