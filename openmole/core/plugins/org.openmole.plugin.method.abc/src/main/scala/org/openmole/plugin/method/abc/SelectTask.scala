/*Copyright (C) 2013 Fabien De Vienne
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

package org.openmole.plugin.method.abc

import org.openmole.core.implementation.task.Task
import org.openmole.core.model.data._
import scala.collection.immutable.List

sealed abstract class SelectTask extends Task {

  val thetas: List[Double]
  val distances: List[Double]
  val summaryStats: List[List[Double]]
  val ratio: Double
  val proto: Prototype[List[(Double, Double, List[Double])]]

  def process(context: Context) = Context {
    val selected = (distances, thetas, summaryStats).zipped.toList.sortWith(_._1 < _._1).slice(0, 1 + (ratio * thetas.length).toInt)
    Variable(proto, selected)
  }

}
