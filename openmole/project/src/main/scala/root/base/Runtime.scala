package root.base

import org.openmole.buildsystem.OMKeys._

import root.BaseDefaults
import sbt._
import Keys._
import root.Libraries._
import root.libraries.Apache
import root.ThirdParties._

object Runtime extends BaseDefaults {
  implicit val artifactPrefix = Some("org.openmole.runtime")

  override def dir = super.dir / "runtime"

  val dbserver = OsgiProject("dbserver") dependsOn (db4o, xstream, Misc.replication) settings (bundleType += "dbserver")

  val runtime = OsgiProject("runtime", singleton = true) dependsOn (Core.implementation, Core.batch, Core.serializer,
    Misc.logging, scalaLang, scopt, Misc.hashService, Misc.eventDispatcher, Misc.exception) settings
    (libraryDependencies += "org.eclipse.core" % "org.eclipse.equinox.app" % "1.3.100.v20120522-1841" % "provided",
      bundleType += "runtime")

  val daemon = OsgiProject("daemon", singleton = true) dependsOn (Core.model, Core.implementation, Core.batch, Misc.workspace,
    Misc.fileService, Misc.exception, Misc.tools, Misc.logging, plugin.Environment.desktopgrid, scalaLang, Apache.logging,
    jodaTime, Misc.hashService, scopt) settings (
      libraryDependencies += "org.eclipse.core" % "org.eclipse.equinox.app" % "1.3.100.v20120522-1841" % "provided",
      includeGridscaleSSH,
      bundleType += "daemon"
    )

  override def OsgiSettings = super.OsgiSettings ++ Seq(bundleType := Set())

}
