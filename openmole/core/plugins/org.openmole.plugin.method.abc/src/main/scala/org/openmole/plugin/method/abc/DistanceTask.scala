/*Copyright (C) 2013 Mathieu Leclaire
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
import org.apache.commons.math3.stat.descriptive._
import org.openmole.core.model.task.PluginSet
import org.openmole.core.model.task._
import org.openmole.misc.tools.math._
import org.openmole.core.model.data

sealed abstract class DistanceTask(val name: String, implicit val plugins: PluginSet) extends Task {

  val thetas: List[Double]
  val summaryStats: List[List[Double]]
  val summaryStatsTarget: List[Double]
  val proto: Prototype[List[Double]]

  override def process(context: Context) = Context {
    val distances = {
      for (s ← summaryStats) yield {
        val variance = new DescriptiveStatistics(s.toArray).getVariance()
        (s.zip(summaryStatsTarget) map {
          case (a: Double, b: Double) ⇒ (Math.pow((a - b), 2) / variance)
        }).reduceLeft(_ + _)
      }
    }
    Variable(proto, distances)
  }
}