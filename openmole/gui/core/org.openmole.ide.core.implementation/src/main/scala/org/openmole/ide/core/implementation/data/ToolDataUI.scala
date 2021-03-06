/*
 * Copyright (C) 2011 <mathieu.Mathieu Leclaire at openmole.org>
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
package org.openmole.ide.core.implementation.data

import org.openmole.core.model.data.Prototype
import org.openmole.ide.core.implementation.registry.PrototypeKey
import org.openmole.core.model.mole.{ Hooks, Sources, IMole }
import org.openmole.core.model.transition.{ IAggregationTransition, ITransition }
import org.openmole.ide.core.implementation.dataproxy.{ Proxies, PrototypeDataProxyUI }
import org.openmole.core.implementation.mole.Mole
import scala.util._
import org.openmole.ide.core.implementation.dialog.StatusBar

object ToolDataUI {
  def implicitPrototypes(coreInputs: Unit ⇒ List[Prototype[_]],
                         prototypesIn: Seq[PrototypeDataProxyUI],
                         coreOutputs: Unit ⇒ List[Prototype[_]],
                         prototypesOut: Seq[PrototypeDataProxyUI]) = {
    def protoFilter(lP: Seq[Prototype[_]], protos: Seq[PrototypeDataProxyUI]) = {
      lP.map { i ⇒ PrototypeKey(i) }.toList.diff(protos.map {
        p ⇒ PrototypeKey(p)
      }).map { Proxies.instance.prototypeOrElseCreate }
    }

    (protoFilter(coreInputs(), prototypesIn), protoFilter(coreOutputs(), prototypesOut))
  }

  def computePrototypeFromAggregation(mole: IMole, sources: Sources, hooks: Hooks) = {
    mole.transitions.foreach {
      _ match {
        case t: ITransition with IAggregationTransition ⇒ t.data(mole, sources, hooks).foreach { d ⇒
          Proxies.instance.prototypeOrElseCreate(d.prototype)
        }
        case _ ⇒
      }
    }
  }

  def buildUpLevelPrototypes(mole: IMole, sources: Sources, hooks: Hooks) = {
    Try(Mole.levels(mole)) match {
      case Success(levels) ⇒ levels.foreach {
        case (c, level) ⇒
          c.outputs(mole, sources, hooks).foreach { d ⇒
            Proxies.instance.prototypeOrElseCreate(d.prototype, level)
          }
      }
      case Failure(e) ⇒ StatusBar().warn(e)
    }
  }
}