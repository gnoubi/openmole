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

package org.openmole.ide.plugin.task.gama

import org.openmole.core.model.task.ITask
import org.openmole.plugin.task.gama.GamaTask
import org.openmole.ide.core.implementation.builder.{ PuzzleUIMap, SceneFactory }
import org.openmole.ide.core.implementation.factory.TaskFactoryUI
import java.io.File

class GamaTaskFactoryUI extends TaskFactoryUI {
  override def toString = "Gama"

  def buildDataUI = new GamaTaskDataUI

  def buildDataProxyUI(task: ITask, uiMap: PuzzleUIMap) = {
    val t = SceneFactory.as[GamaTask](task)
    uiMap.task(task, x ⇒ new GamaTaskDataUI(t.name, t.toto.toList.map { t ⇒
      uiMap.prototypeUI(t).get
    })
    )
  }
}
