/*
 * Copyright (C) 2012 Romain Reuillon
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

package org.openmole.plugin.task.stat

import org.openmole.core.model.task.PluginSet

object SumTask {

  def apply(name: String)(implicit plugins: PluginSet) = new DoubleSequenceStatTaskBuilder { builder ⇒
    def toTask = new SumTask(name) with builder.Built
  }

}

sealed abstract class SumTask(val name: String) extends DoubleSequenceStatTask {

  override def stat(seq: Array[Double]) = seq.sum

}

