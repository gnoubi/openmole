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

sealed abstract class Beaumont extends Task {

  val weights: List[Double]
  val selected: List[(Double/*distances*/, Double/*thetas*/, List[Double]/*summaryStats*/)]
  val summaryStatsTarget: List[Double]
  /*X dans Beaumont 2002*/
  val matrix: RealMatrix
  val proto: Prototype[Any]

  def createMatrix = {

  }

  /*à déplacer en amont, sera fait par une autre tache dans le WF*/
  def calculWeight = {

  }

  def process = {

  }
}
