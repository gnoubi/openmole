package root.gui.plugin

import sbt._
import root.gui._

object Builder extends PluginDefaults {
  implicit val artifactPrefix = Some("org.openmole.ide.plugin.builder")

  lazy val base = OsgiProject("base") dependsOn (Core.implementation, root.base.plugin.Builder.base)
}