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
package org.openmole.ide.core.implementation.dataproxy

import org.openmole.ide.core.implementation.data.SourceDataUI
import org.openmole.ide.core.implementation.panel.IOFacade
import org.openmole.ide.misc.tools.util.ID
import org.openmole.ide.core.implementation.serializer.Update

object SourceDataProxyUI {
  def apply(d: SourceDataUI,
            g: Boolean = false) = new SourceDataProxyUI(d, g)

  @deprecated("Used for deserialiation purposes")
  private def annonymous = new SourceDataProxyUI(???, ???) with Update[SourceDataProxyUI] {
    def update = new SourceDataProxyUI(dataUI, generated, id)
  }
}

class SourceDataProxyUI(var dataUI: SourceDataUI,
                        val generated: Boolean,
                        override val id: ID.Type = ID.newId) extends DataProxyUI with IOFacade {
  type DATAUI = SourceDataUI
}