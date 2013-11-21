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

package org.openmole.plugin.sampling.lhs

import org.openmole.misc.tools.service.Scaling._
import org.openmole.misc.tools.service.Random._
import org.openmole.core.implementation.data._
import org.openmole.core.implementation.sampling._
import org.openmole.core.model.data._
import org.openmole.core.model.domain._
import org.openmole.core.model.sampling._
import org.openmole.misc.workspace._
import org.openmole.core.implementation.task.Task._

/**
 * Latin Hypercube Sampler from a set of factors using uniform distributions.
 */
object LHS {

  /**
   * Build a Latin Hypercube sampler with a set of factors using uniform distributions.
   * @param samples number of samples to generate
   * @param factors factors used
   * @return
   */
  def apply(samples: Int, factors: Factor[Double, Domain[Double] with Bounds[Double]]*) =
    new LHS(samples, factors: _*)

}

sealed class LHS(val samples: Int, val factors: Factor[Double, Domain[Double] with Bounds[Double]]*) extends Sampling {

  override def inputs = DataSet(factors.flatMap(_.inputs))
  override def prototypes = factors.map { _.prototype }

  override def build(context: Context): Iterator[Iterable[Variable[Double]]] = {
    val rng = newRNG(context(openMOLESeed))

    factors.map {
      f ⇒
        (0 until samples).shuffled(rng).map {
          i ⇒ Variable(f.prototype, ((i + rng.nextDouble) / samples).scale(f.domain.min(context), f.domain.max(context)))
        }
    }.transpose.toIterator
  }
}
