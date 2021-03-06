/*
 * Copyright (C) 2012 Mathieu Leclaire 
 * < mathieu.leclaire at openmole.org >
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

package org.openmole.ide.plugin.domain.collection

import org.openmole.ide.misc.tools.util.Types._
import org.openmole.misc.exception.UserBadDataError
import org.openmole.plugin.domain.collection.VariableDomain
import org.openmole.core.model.data.Prototype
import org.openmole.misc.tools.obj.ClassUtils
import util.Try
import org.openmole.ide.core.implementation.data.DomainDataUI
import org.openmole.ide.core.implementation.dataproxy.PrototypeDataProxyUI
import org.openmole.ide.core.implementation.sampling.FiniteUI

object VariableDomainDataUI {
  def apply[T](prototypeArray: Option[PrototypeDataProxyUI], classString: String) = {
    new VariableDomainDataUI(prototypeArray)(ClassUtils.manifest(classString))
  }
}

class VariableDomainDataUI[S](val prototypeArray: Option[PrototypeDataProxyUI] = None)(implicit val domainType: Manifest[S])
    extends DomainDataUI with FiniteUI {
  vdomainDataUI ⇒

  val name = "Prototype Array"

  def coreObject = Try(prototypeArray match {
    case Some(p: PrototypeDataProxyUI) ⇒ VariableDomain(p.dataUI.coreObject.get.asInstanceOf[Prototype[Array[S]]])
    case _                             ⇒ throw new UserBadDataError("An array of Prototypes is required for a Prototype Array Domain")
  })

  def buildPanelUI = new VariableDomainPanelUI(this)

  def preview = "in " + prototypeArray.getOrElse("None").toString

  def coreClass = classOf[VariableDomainDataUI[S]]
}