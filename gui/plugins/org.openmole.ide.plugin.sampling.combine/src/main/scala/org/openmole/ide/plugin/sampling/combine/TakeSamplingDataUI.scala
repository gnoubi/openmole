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
package org.openmole.ide.plugin.sampling.combine

import org.openmole.ide.core.model.data.{ IFactorDataUI, IDomainDataUI, ISamplingDataUI }
import org.openmole.core.model.sampling.Sampling
import org.openmole.plugin.sampling.combine.TakeSampling
import org.openmole.misc.exception.UserBadDataError
import org.openmole.ide.misc.widget.{ URL, Helper }

class TakeSamplingDataUI(val size: String = "1") extends ISamplingDataUI {

  val name = "Take"

  def coreObject(factors: List[IFactorDataUI], samplings: List[Sampling]) =
    samplings.headOption match {
      case Some(s: Sampling) ⇒ new TakeSampling(s, size.toInt)
      case x: Any ⇒ throw new UserBadDataError("A Sampling is required as input of a Take Sampling " + x)
    }

  def buildPanelUI = new TakeSamplingPanelUI(this) {
    override val help = new Helper(List(new URL(i18n.getString("takePermalinkText"),
      i18n.getString("takePermalink"))))
  }

  def imagePath = "img/takeSampling.png"

  def fatImagePath = "img/takeSampling_fat.png"

  override def isAcceptable(domain: IDomainDataUI) = false

  def isAcceptable(sampling: ISamplingDataUI) = true

  override def inputNumberConstrainst = Some(1)

  def preview = "Take (" + size + ")"

  def coreClass = classOf[TakeSampling]
}