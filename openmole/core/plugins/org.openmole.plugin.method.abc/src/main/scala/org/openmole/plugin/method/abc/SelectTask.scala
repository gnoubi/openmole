package org.openmole.plugin.method.abc

import org.openmole.core.implementation.task.Task
import org.openmole.core.model.data._
import scala.collection.immutable.List
import org.apache.commons.math3.stat.descriptive._
import org.openmole.core.model.task.PluginSet
import org.openmole.core.model.task._
import org.openmole.misc.tools.math._
import org.openmole.core.model.data

/**
 * Created with IntelliJ IDEA.
 * User: backslash
 * Date: 14/06/13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */

sealed abstract class SelectTask extends Task {

  val thetas: List[Double]
  val distances: List[Double]
  val ratio: Double
  val proto: Prototype[List[Tuple2[Double, Double]]]

  def process(context: Context) = Context {
    val selected = {
      val sortedDist = distances.sortWith(_ < _)
      val sortedThetas = thetas.sortWith(_ < _)
      sortedDist.zip(sortedThetas).slice(0, 1 + (ratio * thetas.length).toInt)
    }
    Variable(proto, selected)
  }

}
