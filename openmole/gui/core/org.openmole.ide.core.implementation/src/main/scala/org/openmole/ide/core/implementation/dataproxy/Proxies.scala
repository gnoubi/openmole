/*
 * Copyright (C) 2011 Mathieu Mathieu Leclaire <mathieu.Mathieu Leclaire at openmole.org>
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.core.implementation.dataproxy

import org.openmole.ide.core.model.dataproxy._
import org.openmole.ide.core.implementation.panel.ConceptMenu
import org.openmole.ide.misc.tools.util._
import org.openmole.misc.tools.obj.ClassUtils._
import org.openmole.ide.core.implementation.builder.Builder
import concurrent.stm._
import org.openmole.ide.core.implementation.registry.PrototypeKey

object Proxies {
  var instance = new Proxies
}

class Proxies {

  private val _tasks = TMap[ID.Type, ITaskDataProxyUI]()
  private val _prototypes = TMap[ID.Type, IPrototypeDataProxyUI]()
  private val _samplings = TMap[ID.Type, ISamplingCompositionDataProxyUI]()
  private val _environments = TMap[ID.Type, IEnvironmentDataProxyUI]()
  private val _hooks = TMap[ID.Type, IHookDataProxyUI]()
  private val _sources = TMap[ID.Type, ISourceDataProxyUI]()

  def dataUIs = atomic { implicit ctx ⇒
    _tasks.values ++ _prototypes.values ++ _samplings.values ++ _environments.values ++ _hooks.values ++ _sources.values
  }

  def tasks = _tasks.single.values.toList
  def prototypes = _prototypes.single.values.toList
  def samplings = _samplings.single.values.toList
  def environments = _environments.single.values.toList
  def hooks = _hooks.single.values.toList
  def sources = _sources.single.values.toList

  def task(id: ID.Type) = _tasks.single.get(id)
  def prototype(id: ID.Type) = _prototypes.single.get(id)
  def sampling(id: ID.Type) = _samplings.single.get(id)
  def environment(id: ID.Type) = _environments.single.get(id)
  def hook(id: ID.Type) = _hooks.single.get(id)
  def source(id: ID.Type) = _sources.single.get(id)

  def prototype(p: PrototypeKey) =
    _prototypes.single.map { case (_, v) ⇒ PrototypeKey(v) -> v }.get(p)

  def prototypeOrElseCreate(k: PrototypeKey) = atomic { implicit ctx ⇒
    prototype(k).getOrElse {
      val p = PrototypeKey.build(k)
      this += p
      p
    }
  }

  def +=(t: ITaskDataProxyUI) = _tasks.single put (t.id, t)
  def -=(t: ITaskDataProxyUI) = _tasks.single remove (t.id)
  def contains(t: ITaskDataProxyUI) = _tasks.single.contains(t.id)

  def +=(t: IPrototypeDataProxyUI) = _prototypes.single put (t.id, t)
  def -=(t: IPrototypeDataProxyUI) = _prototypes.single remove (t.id)
  def contains(t: IPrototypeDataProxyUI) = _prototypes.single.contains(t.id)

  def +=(t: ISamplingCompositionDataProxyUI) = _samplings.single put (t.id, t)
  def -=(t: ISamplingCompositionDataProxyUI) = _samplings.single remove (t.id)
  def contains(t: ISamplingCompositionDataProxyUI) = _samplings.single.contains(t.id)

  def +=(t: IEnvironmentDataProxyUI) = _environments.single put (t.id, t)
  def -=(t: IEnvironmentDataProxyUI) = _environments.single remove (t.id)
  def contains(t: IEnvironmentDataProxyUI) = _environments.single.contains(t.id)

  def +=(t: IHookDataProxyUI) = _hooks.single put (t.id, t)
  def -=(t: IHookDataProxyUI) = _hooks.single remove (t.id)
  def contains(t: IHookDataProxyUI) = _hooks.single.contains(t.id)

  def +=(t: ISourceDataProxyUI) = _sources.single put (t.id, t)
  def -=(t: ISourceDataProxyUI) = _sources.single remove (t.id)
  def contains(t: ISourceDataProxyUI) = _sources.single.contains(t.id)

  def allPrototypesByName = prototypes.map {
    _.dataUI.name
  }

  def classPrototypes(prototypeClass: Class[_]): List[IPrototypeDataProxyUI] =
    classPrototypes(prototypeClass, prototypes.toList)

  def classPrototypes(prototypeClass: Class[_],
                      protoList: List[IPrototypeDataProxyUI]): List[IPrototypeDataProxyUI] = protoList.filter {
    p ⇒ assignable(prototypeClass, p.dataUI.coreObject.get.`type`.runtimeClass)
  }

  def getOrGenerateSamplingComposition(p: ISamplingCompositionDataProxyUI) =
    if (contains(p)) p
    else Builder.samplingCompositionUI(true)

  def clearAll: Unit = atomic { implicit actx ⇒
    ConceptMenu.clearAllItems
    List(_tasks, _prototypes, _environments, _samplings, _hooks, _sources).foreach {
      _.clear()
    }
  }
}

