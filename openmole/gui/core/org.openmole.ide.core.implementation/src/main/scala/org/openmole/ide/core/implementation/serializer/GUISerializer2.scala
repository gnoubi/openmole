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

package org.openmole.ide.core.implementation.serializer

import util.Try
import com.ice.tar.TarInputStream
import com.ice.tar.TarOutputStream
import com.thoughtworks.xstream.XStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import org.openmole.ide.misc.tools.util.ID
import org.openmole.ide.core.model.dataproxy._
import org.openmole.ide.core.implementation.dataproxy._
import org.openmole.misc.tools.io.FileUtil._
import org.openmole.misc.workspace.Workspace
import org.openmole.misc.tools.io.TarArchiver._
import scala.pickling._
import scala.pickling.json._
import org.openmole.misc.exception.InternalProcessingError
import collection.mutable
import scalax.io.Resource

class GUISerializer2 { serializer2 ⇒

  sealed trait SerializationState
  case class Serializing(id: ID.Type) extends SerializationState
  case class Serialized(id: ID.Type) extends SerializationState

  val serializationStates: mutable.HashMap[IDataProxyUI, SerializationState] = mutable.HashMap.empty
  val deserializationStates: mutable.HashMap[ID.Type, IDataProxyUI] = mutable.HashMap.empty

  val xstream = new XStream
  val workDir = Workspace.newDir

  implicit def myConverter[T <: IDataProxyUI] = new GUIConverter[T]

  class GUIConverter[T <: IDataProxyUI](implicit val format: PickleFormat) extends SPickler[T] {
    println("++++++++++++++ MY CONV ")
    def pickle(proxy: T, builder: PBuilder): Unit = {
      println("----------- MY PICKLE0 " + proxy)
      serializationStates.get(proxy) match {
        case None ⇒
          println("----------- None ")
          serializationStates += proxy -> new Serializing(proxy.id)
          pickle(proxy, builder)
        case Some(Serializing(id)) ⇒
          println("----------- SERIALIZIN ")
          serializationStates(proxy) = new Serialized(id)
          //builder.result
          finalize(proxy, builder)
        case Some(Serialized(id)) ⇒
          println("----------- SERIALIZED ")
          builder.beginEntry(proxy)
          builder.putField("id", b ⇒
            b.hintTag(FastTypeTag.ScalaString).beginEntry(id.toString).endEntry)
          builder.endEntry
      }
    }

    def finalize(proxy: T, builder: PBuilder) = {
      println("call pickle from finalize")
      proxy match {
        case x: IPrototypeDataProxyUI ⇒ x.pickleInto(builder)
        case x: ITaskDataProxyUI      ⇒ x.pickleInto(builder)
      }
    }

    //proxy.pickleInto(builder)
    /*
    override def marshal(
      o: Object,
      writer: HierarchicalStreamWriter,
      mc: MarshallingContext) = {
      val dataUI = o.asInstanceOf[IDataProxyUI]
      serializationStates.get(dataUI) match {
        case None ⇒
          serializationStates += dataUI -> new Serializing(dataUI.id)
          marshal(o, writer, mc)
        case Some(Serializing(id)) ⇒
          serializationStates(dataUI) = new Serialized(id)
          super.marshal(dataUI, writer, mc)
        case Some(Serialized(id)) ⇒
          writer.addAttribute("id", id.toString)
      }
    }

    override def unmarshal(
      reader: HierarchicalStreamReader,
      uc: UnmarshallingContext) = {
      if (reader.getAttributeCount != 0) {
        val dui = existing(reader.getAttribute("id"))
        dui match {
          case Some(y: IDataProxyUI) ⇒ y
          case _ ⇒
            serializer.deserializeConcept(uc.getRequiredType)
            unmarshal(reader, uc)
        }
      }
      else {
        val o = super.unmarshal(reader, uc)
        o match {
          case y: IDataProxyUI ⇒
            existing(y.id) match {
              case None ⇒ add(y)
              case _    ⇒
            }
            y
          case _ ⇒ throw new UserBadDataError("Can't load object " + o)
        }
      }
    }    */

    //  def existing(id: String) = deserializationStates.get(id)
    //  def add(e: IDataProxyUI) = deserializationStates.put(e.id, e)

  }

  def folder[T](t: T) =
    t match {
      case p: IPrototypeDataProxyUI           ⇒ "prototype"
      case p: IEnvironmentDataProxyUI         ⇒ "environment"
      case p: ISamplingCompositionDataProxyUI ⇒ "sampling"
      case p: IHookDataProxyUI                ⇒ "hook"
      case p: ISourceDataProxyUI              ⇒ "source"
      case p: ITaskDataProxyUI                ⇒ "task"
      case p: MoleData                        ⇒ "mole"
      case _                                  ⇒ "unknown"
    }

  def serializeConcept[T](set: Iterable[(T, ID.Type, String)]) = {
    if (!set.isEmpty) {
      val conceptDir = new File(workDir, folder(set.head._1))
      conceptDir.mkdirs
      set.foreach {
        case (s, id, pi) ⇒ Resource.fromFile(new File(conceptDir, id + ".json")).write(pi)
        case _           ⇒
      }
    }
  }

  def serialize(file: File, proxies: Proxies, moleScenes: Iterable[MoleData]) = {
    serializeConcept(proxies.tasks.map { s ⇒ (s, s.id, s.pickle(pickleFormat).toString) })
    serializeConcept(proxies.prototypes.map { s ⇒ (s, s.id, s.pickle.toString) })
    serializeConcept(proxies.environments.map { s ⇒ (s, s.id, s.pickle(pickleFormat).toString) })
    serializeConcept(proxies.hooks.map { s ⇒ (s, s.id, s.pickle.toString) })
    serializeConcept(proxies.sources.map { s ⇒ (s, s.id, s.pickle(pickleFormat).toString) })
    serializeConcept(proxies.samplings.map { s ⇒ (s, s.id, s.pickle(pickleFormat).toString) })

    //serializeConcept(moleScenes.map { ms ⇒ ms -> ms.id })
    val os = new TarOutputStream(new FileOutputStream(file))
    try os.createDirArchiveWithRelativePath(workDir)
    finally os.close
    clear
  }

  def read(f: File) = {
    try xstream.fromXML(f)
    catch {
      case e: Throwable ⇒
        throw new InternalProcessingError(e, "An error occurred when loading " + f.getAbsolutePath + "\n")
    }
  }

  def deserializeConcept[T](clazz: Class[_]) =
    new File(workDir, folder(clazz)).listFiles.toList.map(read).map(_.asInstanceOf[T])

  def deserialize(fromFile: String) = {
    val os = new TarInputStream(new FileInputStream(fromFile))
    os.extractDirArchiveWithRelativePathAndClose(workDir)

    val proxies: Proxies = new Proxies

    Try {
      deserializeConcept[IPrototypeDataProxyUI](classOf[IPrototypeDataProxyUI]).foreach(proxies.+=)
      deserializeConcept[ISamplingCompositionDataProxyUI](classOf[ISamplingCompositionDataProxyUI]).foreach(proxies.+=)
      deserializeConcept[IEnvironmentDataProxyUI](classOf[IEnvironmentDataProxyUI]).foreach(proxies.+=)
      deserializeConcept[IHookDataProxyUI](classOf[IHookDataProxyUI]).foreach(proxies.+=)
      deserializeConcept[ISourceDataProxyUI](classOf[ISourceDataProxyUI]).foreach(proxies.+=)
      deserializeConcept[ITaskDataProxyUI](classOf[ITaskDataProxyUI]).foreach(proxies.+=)

      val moleScenes = deserializeConcept[MoleData](classOf[MoleData])
      (proxies, moleScenes)
    }
  }

  def clear = {
    serializationStates.clear
    deserializationStates.clear
    workDir.recursiveDelete
    workDir.mkdirs
  }
}
