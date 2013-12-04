/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openmole.ide.plugin.task.gama

import java.io.File
import org.openmole.core.model.data._
import org.openmole.core.model.task._
import org.openmole.ide.core.implementation.data.TaskDataUI
import org.openmole.plugin.task.gama.GamaTask
import org.openmole.ide.core.implementation.dataproxy.PrototypeDataProxyUI
import org.openmole.ide.core.implementation.serializer.Update

class GamaTaskDataUI(val name: String = "",
                     val toto: Seq[PrototypeDataProxyUI] = Seq.empty,
                     val inputs: Seq[PrototypeDataProxyUI] = Seq.empty,
                     val outputs: Seq[PrototypeDataProxyUI] = Seq.empty,
                     val inputParameters: Map[PrototypeDataProxyUI, String] = Map.empty) extends TaskDataUI {

  def coreObject(plugins: PluginSet) = util.Try {
    val gtBuilder = GamaTask(name)(plugins)
    toto.foreach {
      p ⇒ gtBuilder.addToto(p.dataUI.coreObject.get.asInstanceOf[Prototype[Double]])
    }
    initialise(gtBuilder)
    gtBuilder.toTask
  }

  def coreClass = classOf[GamaTask]

  override def imagePath = "img/groovyTask.png"

  def fatImagePath = "img/groovyTask_fat.png"

  def buildPanelUI = new GamaTaskPanelUI(this)

  def doClone(ins: Seq[PrototypeDataProxyUI],
              outs: Seq[PrototypeDataProxyUI],
              params: Map[PrototypeDataProxyUI, String]) = new GamaTaskDataUI(name, toto, ins, outs, params)

}
