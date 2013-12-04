/*
 * Copyright (C) 04/12/13 Romain Reuillon
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.task.gama

import org.openmole.core.implementation.task._
import org.openmole.core.implementation.data._
import org.openmole.core.model.data._
import org.openmole.core.model.task._
import scala.collection.mutable.ListBuffer

object GamaTask {

  def apply(name: String)(implicit plugins: PluginSet) = new TaskBuilder {
    val toto = ListBuffer[Prototype[Double]]()

    def addToto(t: Prototype[Double]) = {
      addInput(t)
      addOutput(t)
      toto += t
    }

    def toTask = new GamaTask(name, toto.toList) with Built
  }

}

abstract class GamaTask(val name: String, val toto: List[Prototype[Double]]) extends Task {

  protected def process(context: Context) =
    Context(toto.map { t ⇒ Variable(t, context(t) * 2) })

}
