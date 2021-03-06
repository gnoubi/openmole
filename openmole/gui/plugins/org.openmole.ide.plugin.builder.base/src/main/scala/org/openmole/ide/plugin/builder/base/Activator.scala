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

package org.openmole.ide.plugin.builder.base

import org.openmole.ide.core.implementation.registry.{ BuilderActivator, OSGiActivator }
import org.openmole.core.implementation.puzzle.Puzzle
import org.openmole.misc.exception.UserBadDataError
import org.openmole.ide.core.implementation.factory.BuilderFactoryUI
import org.openmole.ide.core.implementation.workflow.MoleUI
import org.openmole.ide.core.implementation.builder.BuilderPanel

class Activator extends OSGiActivator with BuilderActivator {

  override def builderFactories = List(new BuilderFactoryUI {
    def name = "Explore"
    def buildPanelUI(puzzle: List[Puzzle], puzzleSelection: Option[Puzzle], manager: MoleUI): BuilderPanel = {
      if (puzzle.isEmpty) throw new UserBadDataError("The Explore builder can not be built - it requires at list a sequence of Tasks")
      else new ExplorationBuilderPanelUI(puzzle, puzzleSelection, manager)
    }
  }, new BuilderFactoryUI {
    def name = "Explore and Merge"
    def buildPanelUI(puzzle: List[Puzzle], puzzleSelection: Option[Puzzle], manager: MoleUI) = {
      if (puzzle.length < 2) throw new UserBadDataError("The Explore and Merge  builder can not be built - it requires at list a 2 sequences of Tasks")
      else new AggregationBuilderPanelUI(puzzle, puzzleSelection, manager)
    }
  })
}
