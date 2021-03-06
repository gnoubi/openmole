/*
 * Copyright (C) 2012 mathieu
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

package org.openmole.ide.plugin.domain.range

import org.openmole.misc.exception.UserBadDataError
import org.openmole.ide.misc.tools.util.Types._
import org.openmole.ide.core.implementation.dialog.StatusBar
import org.openmole.ide.misc.tools.util.Types
import org.openmole.ide.core.implementation.data.DomainDataUI
import org.openmole.ide.core.implementation.sampling.FiniteUI

object GenericRangeDomainDataUI {

  def apply[S](min: String = "0", max: String = "", step: Option[String] = None, log: Boolean, classString: String): DomainDataUI =
    if (log) {
      Types.standardize(classString) match {
        case DOUBLE      ⇒ new DoubleLogarithmRangeDataUI(min, max, step)
        case BIG_DECIMAL ⇒ new BigDecimalLogarithmRangeDataUI(min, max, step)
        case x: Any      ⇒ throw new UserBadDataError("The type " + x + " is not supported in logarithm scale")
      }
    }
    else RangeDomainDataUI(min, max, step, classString)
}

abstract class GenericRangeDomainDataUI extends DomainDataUI with FiniteUI {

  override def isAcceptable(domain: DomainDataUI) = {
    StatusBar().warn("Only modifier Domain (Map, Take, Group, ...) can take another Domain as input")
    super.isAcceptable(domain)
  }

  def preview =
    if (step.isDefined) "[" + min + "," + max + stepString + "]"
    else "[" + min + "," + max + "]"

  def stepString = {
    if (step.isDefined) {
      if (step.get.isEmpty) ""
      else "," + step.get
    }
    else ""
  }

  def min: String

  def max: String

  def step: Option[String]
}
