/*
 * Copyright (C) 2011 Mathieu Leclaire
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

package org.openmole.ide.plugin.domain.modifier

import java.math.BigDecimal
import java.math.BigInteger
import org.openmole.core.model.domain.{ Discrete, Domain }
import org.openmole.ide.misc.tools.util.Types._
import org.openmole.misc.exception.UserBadDataError
import org.openmole.plugin.domain.modifier.GroupDomain
import java.io.File
import org.openmole.ide.misc.tools.util.Types
import org.openmole.ide.core.implementation.data.DomainDataUI
import org.openmole.ide.core.implementation.sampling.FiniteUI

object GroupDomainDataUI {
  def empty = apply("1", DOUBLE, List())

  def apply(size: String,
            classString: String,
            previousDomain: List[DomainDataUI]): GroupDomainDataUI[_] = {
    Types.standardize(classString) match {
      case INT         ⇒ new GroupDomainDataUI[Int](size, previousDomain)
      case DOUBLE      ⇒ new GroupDomainDataUI[Double](size, previousDomain)
      case BIG_DECIMAL ⇒ new GroupDomainDataUI[BigDecimal](size, previousDomain)
      case BIG_INTEGER ⇒ new GroupDomainDataUI[BigInteger](size, previousDomain)
      case LONG        ⇒ new GroupDomainDataUI[Long](size, previousDomain)
      case STRING      ⇒ new GroupDomainDataUI[String](size, previousDomain)
      case FILE        ⇒ new GroupDomainDataUI[File](size, previousDomain)
      case x: Any      ⇒ throw new UserBadDataError("The type " + x + " is not supported")
    }
  }
}

case class GroupDomainDataUI[S](val size: String = "0",
                                var previousDomain: List[DomainDataUI] = List.empty)(implicit val domainType: Manifest[S])
    extends ModifierDomainDataUI with FiniteUI {

  val name = "Group"

  def preview = "Group (" + size + ")"

  override def coreObject = util.Try {
    val valid = validPreviousDomains
    if (valid._1) GroupDomain(valid._2.head.asInstanceOf[Domain[S] with Discrete[S]], size.toInt)
    else throw new UserBadDataError("No input domain has been found, it is required for a Group Domain.")
  }

  def buildPanelUI = new GroupDomainPanelUI(this)

  def coreClass = classOf[GroupDomainDataUI[S]]

  override def toString = "Group"

  def clone(pD: List[DomainDataUI]) = pD.headOption match {
    case Some(d: DomainDataUI) ⇒ GroupDomainDataUI(size, Types.pretify(d.domainType.toString), pD)
    case _                     ⇒ GroupDomainDataUI(size, DOUBLE, List())
  }

}
