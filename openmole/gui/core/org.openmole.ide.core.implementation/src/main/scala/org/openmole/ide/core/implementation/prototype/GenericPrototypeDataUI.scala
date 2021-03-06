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

package org.openmole.ide.core.implementation.prototype

import org.openmole.core.implementation.data._
import org.openmole.ide.misc.tools.util.Types._
import org.openmole.core.model.data._
import org.openmole.misc.exception.UserBadDataError
import org.openmole.ide.misc.tools.util.Types
import org.openmole.misc.tools.obj.ClassUtils._
import scala.util.Try
import org.openmole.ide.core.implementation.data.PrototypeDataUI

object GenericPrototypeDataUI {

  val baseType = List(INT,
    LONG,
    STRING,
    DOUBLE,
    FILE,
    BIG_INTEGER,
    BIG_DECIMAL)

  val upperBaseType = baseType.map { _.toUpperCase }

  var extraType = List.empty[String]

  def extra: List[GenericPrototypeDataUI[_]] = extraType map {
    stringToDataUI
  }

  def base: List[GenericPrototypeDataUI[_]] = baseType map {
    stringToDataUI
  }

  def apply[T](n: String = "", d: Int = 0)(implicit t: Manifest[T]) = new GenericPrototypeDataUI[T](n, d, t)
  def apply[T](implicit t: Manifest[T]): GenericPrototypeDataUI[T] = apply("", 0)

  def stringToDataUI(s: String): GenericPrototypeDataUI[_] = try {
    GenericPrototypeDataUI(manifest(Types.standardize(s)))
  }
  catch {
    case e: ClassNotFoundException ⇒ throw new UserBadDataError(s + " can not be loaded as a Class")
  }
}

import GenericPrototypeDataUI._

class GenericPrototypeDataUI[T](val name: String,
                                val dim: Int,
                                val `type`: Manifest[T]) extends PrototypeDataUI[T] { protoDataUI ⇒

  def newInstance(n: String, d: Int) = GenericPrototypeDataUI(n, d)(`type`)

  override def toString = canonicalClassName(typeClassString)

  def typeClassString = `type`.toString

  def coreClass = classOf[Prototype[T]]

  def coreObject = Try(Prototype[T](name)(`type`).toArray(dim).asInstanceOf[Prototype[T]])

  def fatImagePath = {
    if (upperBaseType.contains(canonicalClassName(`type`.toString).toUpperCase)) "img/" + canonicalClassName(`type`.toString).toLowerCase + "_fat.png"
    else "img/extra_fat.png"
  }

  def canonicalClassName(c: String) = Types.pretify(c.split('.').last).toString

  def buildPanelUI = new GenericPrototypePanelUI {
    lazy val dataUI = protoDataUI
  }
}
