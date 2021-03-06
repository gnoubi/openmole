/*
 * Copyright (C) 2011 Romain Reuillon
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

package org.openmole.plugin.hook.display

import org.openmole.core.model.data._
import org.openmole.core.implementation.tools._
import org.openmole.core.implementation.mole._
import org.openmole.core.model.mole._

object DisplayHook {

  def apply(toDisplay: String) =
    new HookBuilder {
      def toHook = new DisplayHook(toDisplay) with Built
    }

}

abstract class DisplayHook(toDisplay: String) extends Hook {

  override def process(context: Context, executionContext: ExecutionContext) = {
    executionContext.out.println(VariableExpansion(context, toDisplay))
    context
  }

}
