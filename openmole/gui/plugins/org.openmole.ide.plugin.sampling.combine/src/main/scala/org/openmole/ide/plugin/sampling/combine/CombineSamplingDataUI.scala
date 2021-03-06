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

import org.openmole.core.model.sampling.{ Factor, Sampling }
import org.openmole.plugin.sampling.combine.CombineSampling
import org.openmole.ide.misc.widget.{ URL, Helper }
import org.openmole.ide.core.implementation.sampling.{ Ordering, FiniteUI, SamplingUtils }
import org.openmole.ide.core.implementation.data.{ SamplingDataUI, DomainDataUI }
import java.util.{ Locale, ResourceBundle }

import org.openmole.ide.core.implementation.panel.NoParameterSamplingPanelUI

class CombineSamplingDataUI extends SamplingDataUI with Ordering {
  val name = "Combine"

  def coreObject(factorOrSampling: List[Either[(Factor[_, _], Int), (Sampling, Int)]]) = util.Try {
    CombineSampling(SamplingUtils.toOrderedSamplings(factorOrSampling): _*)
  }

  def buildPanelUI = new NoParameterSamplingPanelUI(this) {
    val i18n = ResourceBundle.getBundle("help", new Locale("en", "EN"))
    override lazy val help = new Helper(List(new URL(i18n.getString("combinePermalinkText"),
      i18n.getString("combinePermalink"))))
  }

  override def imagePath = "img/combineSampling.png"

  def fatImagePath = "img/combineSampling_fat.png"

  override def isAcceptable(domain: DomainDataUI) = domain match {
    case f: FiniteUI ⇒ true
    case _           ⇒ false
  }

  def isAcceptable(sampling: SamplingDataUI) = true

  def preview = name

  def coreClass = classOf[CombineSampling]
}