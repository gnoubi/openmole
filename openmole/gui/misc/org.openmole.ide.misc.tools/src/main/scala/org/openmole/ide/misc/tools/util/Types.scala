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
package org.openmole.ide.misc.tools.util

object Types {
  val INT = "int"
  val LONG = "long"
  val DOUBLE = "double"
  val FILE = classOf[java.io.File].getSimpleName
  val STRING = classOf[String].getSimpleName
  val BIG_DECIMAL = classOf[java.math.BigDecimal].getSimpleName
  val BIG_INTEGER = classOf[java.math.BigInteger].getSimpleName

  def pretify(s: String) = {
    val c = s.split('.')
    c.last.head.toUpper.toString + c.last.tail
  }

  def pretifyWithNameSpace(s: String) = {
    val c = s.split('.')
    val start = c.init.mkString(".")
    start + {
      if (!start.isEmpty) "." else ""
    } + c.last.head.toUpper.toString + c.last.tail
  }

  def extractTypeFromArray(s: String) = s.replaceAllLiterally("Array[", "").replaceAllLiterally("]", "")

  def standardize(c: String) = {
    c match {
      case "Int"                  ⇒ "int"
      case "java.lang.Integer"    ⇒ "ims"
      case "Double"               ⇒ "double"
      case "java.lang.Double"     ⇒ "double"
      case "Long"                 ⇒ "long"
      case "java.lang.Long"       ⇒ "long"
      case "java.math.BigDecimal" ⇒ "BigDecimal"
      case "java.math.BigInteger" ⇒ "BigInteger"
      case "java.io.File"         ⇒ "File"
      case _                      ⇒ c
    }
  }
}