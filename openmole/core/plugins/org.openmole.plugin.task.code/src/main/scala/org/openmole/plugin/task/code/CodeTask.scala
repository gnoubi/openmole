/*
 * Copyright (C) 2010 Romain Reuillon
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
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.task.code

import org.openmole.core.model.data._
import org.openmole.core.implementation.data._
import org.openmole.misc.workspace.Workspace
import org.openmole.plugin.task.external.ExternalTask

trait CodeTask extends ExternalTask {

  override def process(context: Context) = {
    val pwd = Workspace.newDir
    val links = prepareInputFiles(context, pwd.getCanonicalFile)
    fetchOutputFiles(processCode(context), pwd.getCanonicalFile, links)
  }

  def processCode(context: Context): Context

}
