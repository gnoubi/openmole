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

package org.openmole.core.implementation.transition

import org.openmole.misc.exception._
import org.openmole.core.implementation.tools.ContextAggregator._
import org.openmole.core.implementation.tools._
import org.openmole.core.implementation.validation.TypeUtil._
import org.openmole.core.model.data._
import org.openmole.core.model.mole._
import org.openmole.core.model.tools._
import org.openmole.core.model.transition._
import scala.collection.mutable.Buffer
import scala.collection.mutable.ListBuffer

class Transition(
    val start: ICapsule,
    val end: Slot,
    val condition: ICondition = ICondition.True,
    val filter: IFilter[String] = Filter.empty) extends ITransition {

  private def nextTaskReady(ticket: ITicket, subMole: ISubMoleExecution): Boolean = {
    val registry = subMole.transitionRegistry
    val mole = subMole.moleExecution.mole
    mole.inputTransitions(end).forall(registry.isRegistred(_, ticket))
  }

  protected def submitNextJobsIfReady(context: Buffer[Variable[_]], ticket: ITicket, subMole: ISubMoleExecution) = subMole.transitionRegistry.synchronized {
    val moleExecution = subMole.moleExecution
    val registry = subMole.transitionRegistry
    val mole = subMole.moleExecution.mole

    registry.register(this, ticket, context)
    if (nextTaskReady(ticket, subMole)) {
      val combinaison =
        mole.inputDataChannels(end).toList.flatMap { _.consums(ticket, moleExecution) } ++
          mole.inputTransitions(end).toList.flatMap(registry.remove(_, ticket).getOrElse(throw new InternalProcessingError("BUG context should be registred")).toIterable)

      val newTicket =
        if (mole.slots(end.capsule).size <= 1) ticket
        else moleExecution.nextTicket(ticket.parent.getOrElse(throw new InternalProcessingError("BUG should never reach root ticket")))

      val toAggregate = combinaison.groupBy(_.prototype.name)

      val toArrayManifests =
        Map.empty[String, Manifest[_]] ++ computeManifests(mole)(end).filter(_.toArray).map(ct ⇒ ct.name -> ct.manifest)

      val newContext = aggregate(end.capsule.inputs(mole), toArrayManifests, combinaison)
      Some((end.capsule, newContext, newTicket))
    } else None
  } match {
    case Some((capsule, context, ticket)) ⇒ subMole.submit(capsule, context, ticket)
    case None ⇒
  }

  override def perform(context: Context, ticket: ITicket, subMole: ISubMoleExecution) =
    if (isConditionTrue(context)) _perform(context.filterNot { case (n, _) ⇒ filter(n) }, ticket, subMole)

  override def isConditionTrue(context: Context): Boolean = condition.evaluate(context)

  override def data(mole: IMole) = start.outputs(mole).filterNot(d ⇒ filter(d.prototype.name))

  protected def _perform(context: Context, ticket: ITicket, subMole: ISubMoleExecution) = submitNextJobsIfReady(ListBuffer() ++ context.values, ticket, subMole)

  override def toString = this.getClass.getSimpleName + " from " + start + " to " + end

}
