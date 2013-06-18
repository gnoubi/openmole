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
import org.apache.commons.math3.linear._
import org.openmole.core.model.task.PluginSet
import org.openmole.core.model.task._
import org.openmole.misc.tools.math._
import org.openmole.core.model.data
import breeze.linalg.DenseMatrix

/*object Beaumont {

  def apply(
    name: String,
    target: Seq[Double],
    distance: Prototype[Array[Double]] = Prototype[Double]("distance").toArray)(implicit plugins: PluginSet) = {

    val _distance = distance

    new TaskBuilder { builder ⇒

      addOutput(distance)

      private val _summaryStats = ListBuffer[Prototype[Double]]()

      def addSummaryStat(p: Prototype[Double]) = {
        _summaryStats += p
        addInput(p.toArray)
      }

      def toTask =
        new DistanceTask(name) with builder.Built {
          val summaryStats = _summaryStats.toList.map(_.toArray)
          val summaryStatsTarget = target
          val distances = _distance
        }
    }
  }

}     */

sealed abstract class Beaumont(val name: String, val weights: Seq[Double], val context: Context) extends Task with Distance with Selection {

  /*d = distance; t = thetas; s = summaryStats. They are the nearest points of the target*/
  val (d, t, s) = select(context, distancesValue(context)).unzip
  val X = DenseMatrix.ones(d.toArray.length, t.toArray.length)

  /*à déplacer en amont, sera fait par une autre tache dans le WF*/
  def calculWeight = {

  }

  /*override def process(context: Context) = {

  }   */
}
