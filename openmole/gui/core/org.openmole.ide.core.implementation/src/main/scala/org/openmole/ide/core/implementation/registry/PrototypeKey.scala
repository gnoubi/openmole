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

package org.openmole.ide.core.implementation.registry

import org.openmole.misc.tools.obj.ClassUtils
import org.openmole.core.model.data._
import org.openmole.ide.core.implementation.dataproxy.{ PrototypeDataProxyUI, Proxies }
import org.openmole.ide.core.implementation.data.EmptyDataUIs
import org.openmole.ide.core.implementation.prototype.GenericPrototypeDataUI
import concurrent.stm._

object PrototypeKey {

  /*val _cacheMap: Ref[Option[Map[PrototypeKey, PrototypeDataProxyUI]]] = Ref(None)

  def invalidateCache = _cacheMap.single() = None

  def cacheMap: Map[PrototypeKey, PrototypeDataProxyUI] = atomic { implicit actx ⇒
    _cacheMap() match {
      case Some(m) ⇒ m
      case None ⇒
        val m = Map(KeyPrototypeGenerator(EmptyDataUIs.emptyPrototypeProxy) -> EmptyDataUIs.emptyPrototypeProxy) ++
          Proxies.instance.prototypes.map { p ⇒ KeyPrototypeGenerator(p) -> p }
        _cacheMap() = Some(m)
        m
    }
  }

  def inCacheMap(k: PrototypeKey) = atomic { implicit actx ⇒ cacheMap.contains(k) }    */

  def apply(proxy: PrototypeDataProxyUI): PrototypeKey =
    new PrototypeKey(proxy.dataUI.name, KeyGenerator.stripArrays(proxy.dataUI.`type`)._1.runtimeClass, proxy.dataUI.dim)

  def apply(proto: Prototype[_]): PrototypeKey = {
    val (manifest, dim) = KeyGenerator.stripArrays(proto.`type`)
    new PrototypeKey(proto.name, manifest.runtimeClass, dim)
  }

  def apply(proto: Prototype[_], dim: Int): PrototypeKey = {
    val (manifest, _) = KeyGenerator.stripArrays(proto.`type`)
    new PrototypeKey(proto.name, manifest.runtimeClass, dim)
  }
  /*def prototype(key: PrototypeKey): PrototypeDataProxyUI = {
    buildUnknownPrototype(key)
    cacheMap(key)
  }

  def prototype(proto: Prototype[_]): PrototypeDataProxyUI = {
    buildUnknownPrototype(proto)
    cacheMap(KeyPrototypeGenerator(proto))
  } */

  def build(k: PrototypeKey): PrototypeDataProxyUI =
    build(k.name, k.dim, KeyGenerator.stripArrays(ClassUtils.manifest(k.protoClass))._1)

  def build(p: Prototype[_]): PrototypeDataProxyUI = {
    val (_, dim) = KeyGenerator(p)
    build(p.name, dim, KeyGenerator.stripArrays(p.`type`)._1)
  }

  def build(name: String, dim: Int, m: Manifest[_]): PrototypeDataProxyUI = {
    val proxy = PrototypeDataProxyUI(GenericPrototypeDataUI(name, dim)(KeyGenerator.stripArrays(m)._1), true)
    //  val proxy = PrototypeDataProxyUI(GenericPrototypeDataUI(name, dim)(m), true)
    proxy
  }

  /*def isPrototype(k: PrototypeKey): Boolean = inCacheMap(k)

  def isPrototype(p: Prototype[_]): Boolean = isPrototype(KeyPrototypeGenerator(p))

  def isPrototype(p: PrototypeDataProxyUI): Boolean = isPrototype(KeyPrototypeGenerator(p))*/
}

case class PrototypeKey(name: String, protoClass: Class[_], dim: Int)
