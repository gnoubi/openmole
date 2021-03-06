/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.plugin.task.moletask

import org.openmole.ide.core.implementation.execution.ScenesManager
import org.openmole.ide.misc.tools.util._
import org.openmole.core.model.task._
import org.openmole.ide.core.implementation.data.{ CapsuleDataUI, TaskDataUI }
import org.openmole.core.implementation.task.MoleTask
import org.openmole.misc.exception.UserBadDataError
import scala.collection.JavaConversions._
import org.openmole.ide.core.implementation.builder.MoleFactory
import util.{ Success, Failure }
import org.openmole.ide.core.implementation.workflow.MoleUI
import org.openmole.ide.core.implementation.dataproxy.{ PrototypeDataProxyUI, TaskDataProxyUI }
import org.openmole.ide.core.implementation.serializer.Update

object MoleTaskDataUI {
  def manager(i: ID.Type): Option[MoleUI] = ScenesManager().moleScenes.map {
    _.dataUI
  }.filter {
    _.id == i
  }.headOption

  def capsule(t: TaskDataProxyUI, manager: MoleUI): Option[CapsuleDataUI] =
    manager.capsules.values.map {
      _.dataUI
    }.filter {
      _.task.isDefined
    }.filter {
      _.task.get == t
    }.headOption

  def emptyMoleSceneManager = new MoleUI("")
}

import MoleTaskDataUI._

class MoleTaskDataUI010(val name: String = "",
                        val mole: Option[ID.Type] = None,
                        val finalCapsule: Option[TaskDataProxyUI] = None,
                        val implicits: Iterable[PrototypeDataProxyUI] = Iterable(),
                        val inputs: Seq[PrototypeDataProxyUI] = Seq.empty,
                        val outputs: Seq[PrototypeDataProxyUI] = Seq.empty,
                        val inputParameters: Map[PrototypeDataProxyUI, String] = Map.empty) extends TaskDataUI {

  def coreObject(plugins: PluginSet) = util.Try {
    mole match {
      case Some(x: ID.Type) ⇒ manager(x) match {
        case Some(y: MoleUI) ⇒
          finalCapsule match {
            case Some(z: TaskDataProxyUI) ⇒
              MoleTaskDataUI.capsule(z, y) match {
                case Some(w: CapsuleDataUI) ⇒
                  MoleFactory.buildMole(y) match {
                    case Success((m, capsMap, errs)) ⇒
                      val builder =
                        MoleTask(name, m, capsMap.find {
                          case (k, _) ⇒ k.dataUI == w
                        }.get._2)(plugins)
                      implicits foreach (p ⇒ builder.addImplicit(p.dataUI.coreObject.get.name))
                      initialise(builder)
                      builder.toTask
                    case Failure(l) ⇒ throw new UserBadDataError(l)
                  }
                case _ ⇒ throw new UserBadDataError("No final Capsule is set in the " + name + "Task")
              }
            case _ ⇒ throw new UserBadDataError("A capsule (in the " + name + "Task) without taskMap can not be run")
          }
        case _ ⇒ throw new UserBadDataError("No Mole is set in the " + name + "Task")
      }
      case _ ⇒ throw new UserBadDataError("No Mole is set in the " + name + "Task")
    }
  }

  def coreClass = classOf[MoleTask]

  override def imagePath = "img/mole.png"

  override def fatImagePath = "img/mole_fat.png"

  def buildPanelUI = new MoleTaskPanelUI(this)

  def doClone(ins: Seq[PrototypeDataProxyUI],
              outs: Seq[PrototypeDataProxyUI],
              params: Map[PrototypeDataProxyUI, String]) = new MoleTaskDataUI010(name, mole, finalCapsule, implicits, ins, outs, params)
}

class MoleTaskDataUI(name: String = "",
                     mole: Option[ID.Type] = None,
                     finalCapsule: Option[TaskDataProxyUI] = None,
                     inputs: Seq[PrototypeDataProxyUI] = Seq.empty,
                     outputs: Seq[PrototypeDataProxyUI] = Seq.empty,
                     inputParameters: Map[PrototypeDataProxyUI, String] = Map.empty) extends Update[MoleTaskDataUI010] {
  def update = new MoleTaskDataUI010(name, mole, finalCapsule, Iterable(), inputs, outputs, inputParameters)
}