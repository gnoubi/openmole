package root.gui.plugin

import root.base
import sbt._
import root.gui._
import root.libraries._

package object source extends PluginDefaults {
  implicit val artifactPrefix = Some("org.openmole.ide.plugin.source")

  lazy val all = Project("gui-plugin-source", dir) aggregate (file)

  lazy val file = OsgiProject("file") dependsOn (core.implementation, miscellaneous.tools, base.plugin.source.file, opencsv)
}