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

package org.openmole.ide.core.implementation.workflow

import java.awt._
import org.netbeans.api.visual.widget._
import org.openmole.ide.misc.widget.LinkLabel

object PrototypeOnConnectorWidget {
  val darkOnLight = (new Color(218, 218, 218), new Color(0, 0, 0, 180))
  val lightOnDark = (new Color(0, 0, 0, 180), new Color(200, 200, 200))
  val lightOnGreen = (new Color(180, 200, 7), Color.white)
  val lightOnRed = (new Color(170, 0, 0), Color.white)
}

import PrototypeOnConnectorWidget._

class PrototypeOnConnectorWidget(val scene: Scene,
                                 var connectorUI: ConnectorViewUI,
                                 val link: LinkLabel,
                                 val colorPattern: (Color, Color) = PrototypeOnConnectorWidget.lightOnDark,
                                 dim: Int = 0) extends ComponentWidget(scene, link.peer) {
  link.foreground = colorPattern._2
  val pos = link.size.width / 2 + 1
  setPreferredBounds(new Rectangle(30, 30))

  override def paintBackground = {
    val g = scene.getGraphics
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON)
    g.setColor(colorPattern._1)
    g.fillOval(pos, pos, (dim + 30).toInt, 30)
    link.text = connectorUI.preview
  }

  override def paintBorder = {
    val g = scene.getGraphics
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON)
    g.setStroke(new BasicStroke(3f))
    g.setColor(colorPattern._2)
    if (colorPattern == lightOnDark)
      g.drawOval(pos, pos, 28, 28)
  }
}

