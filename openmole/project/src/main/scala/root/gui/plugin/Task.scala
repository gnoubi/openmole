package root.gui.plugin

import root.base
import sbt._
import root.gui._

object Task extends PluginDefaults {
  implicit val artifactPrefix = Some("org.openmole.ide.plugin.task")

  lazy val exploration = OsgiProject("exploration") dependsOn (Core.implementation, base.Core.model, base.Misc.exception, base.Misc.replication % "test")

  lazy val groovy = OsgiProject("groovy") dependsOn (Core.implementation, base.Misc.workspace, base.plugin.Task.groovy, base.Misc.replication % "test")

  // lazy val imagej = OsgiProject("imagej") dependsOn (Core.implementation, base.Misc.workspace, base.plugin.Task.groovy, base.Misc.replication % "test")

  lazy val template = OsgiProject("template") dependsOn (Core.implementation, base.Misc.workspace, base.plugin.Task.template, base.Misc.replication % "test")

  lazy val moletask = OsgiProject("moletask") dependsOn (Core.implementation, base.Core.model, base.Misc.replication % "test")

  lazy val netlogo = OsgiProject("netlogo") dependsOn (Core.implementation, base.Core.model, Osgi.netlogo4,
    base.plugin.Task.netLogo4, base.plugin.Task.netLogo5, Osgi.netlogo5, Miscellaneous.tools, base.Misc.replication % "test")

  lazy val stat = OsgiProject("stat") dependsOn (Core.implementation, base.plugin.Task.stat, base.Core.model, base.Misc.replication % "test")

  lazy val tools = OsgiProject("tools") dependsOn (Core.implementation, base.plugin.Task.tools, base.Core.model, base.Misc.replication % "test")

  lazy val systemexec = OsgiProject("systemexec") dependsOn (Core.implementation, base.plugin.Task.systemexec,
    base.Core.model, Miscellaneous.tools)

  lazy val gama = OsgiProject("gama") dependsOn (Core.implementation, base.Core.model, base.plugin.Task.gama, Miscellaneous.tools, base.Misc.replication % "test")

}
