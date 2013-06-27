package org.openmole.plugin.method.abc

import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: backslash
 * Date: 27/06/13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
class Model {

  def toyModel() {

    val x1 = 12
    val x2 = 14
    val random = new Random()
    val y = ( x1 + x2 + random.nextInt,  x1 * x2 + random.nextInt)
  }
}
