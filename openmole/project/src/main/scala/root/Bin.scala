package root

import sbt._
import Keys._

import org.openmole.buildsystem.OMKeys._
import org.openmole.buildsystem._, Assembly._
import Libraries._
import com.typesafe.sbt.osgi.OsgiKeys._
import sbt.inc.Analysis
import sbtunidoc.Plugin._
import UnidocKeys._

object Bin extends Defaults(Base, Gui, Libraries, ThirdParties, Web, Application) {
  val dir = file("bin")

  private val equinoxDependencies = libraryDependencies ++= Seq(
    "org.eclipse.core" % "org.eclipse.equinox.app" % "1.3.100.v20120522-1841" intransitive (),
    "org.eclipse.core" % "org.eclipse.core.contenttype" % "3.4.200.v20120523-2004" intransitive (),
    "org.eclipse.core" % "org.eclipse.core.jobs" % "3.5.300.v20120912-155018" intransitive (),
    "org.eclipse.core" % "org.eclipse.core.runtime" % "3.8.0.v20120912-155025" intransitive (),
    "org.eclipse.core" % "org.eclipse.equinox.common" % "3.6.100.v20120522-1841" intransitive (),
    "org.eclipse.core" % "org.eclipse.equinox.launcher" % "1.3.0.v20120522-1813" intransitive (),
    "org.eclipse.core" % "org.eclipse.equinox.registry" % "3.5.200.v20120522-1841" intransitive (),
    "org.eclipse.core" % "org.eclipse.equinox.preferences" % "3.5.1.v20121031-182809" intransitive (),
    "org.eclipse.core" % "org.eclipse.osgi" % "3.8.2.v20130124-134944" intransitive (),
    "org.bouncycastle" % "bcpkix-jdk15on" % bouncyCastleVersion
  )

  private lazy val openmolePluginDependencies = libraryDependencies ++= Seq(
    "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.ssh" % gridscaleVersion intransitive (), //TODO deal with these
    "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.http" % gridscaleVersion intransitive (),
    "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.pbs" % gridscaleVersion intransitive (),
    "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.dirac" % gridscaleVersion intransitive (),
    "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.glite" % gridscaleVersion intransitive ()
  )

  lazy val uiProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "core") sendTo "plugins"

  lazy val pluginProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "plugin", true) sendTo "openmole-plugins"

  lazy val guiPluginProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a.contains("guiPlugin"), true) sendTo "openmole-plugins-gui"

  lazy val openmole = AssemblyProject("openmole", "plugins", settings = resAssemblyProject ++ uiProjects ++ pluginProjects ++ guiPluginProjects ++ dbserverProjects ++ zipProject, depNameMap =
    Map("""org\.eclipse\.equinox\.launcher.*\.jar""".r -> { s ⇒ "org.eclipse.equinox.launcher.jar" }, """org\.eclipse\.(core|equinox|osgi)""".r -> { s ⇒ s.replaceFirst("-", "_") })
  ) settings (
    equinoxDependencies, libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale" % gridscaleVersion intransitive (),
    resourceSets <++= (baseDirectory, zip in openmoleRuntime, downloadUrls in openmoleRuntime) map { (bd, zipFile, downloadUrls) ⇒
      Set(bd / "resources" -> "", zipFile -> "runtime") ++ downloadUrls.map(_ -> "runtime")
    },
    resourceSets <+= (baseDirectory) map { _ / "db-resources" -> "dbserver/bin" },
    resourceSets <+= (copyDependencies in openmolePlugins) map { _ -> "openmole-plugins" },
    tarGZName := Some("openmole"),
    dependencyFilter := DependencyFilter.fnToModuleFilter { m ⇒ m.organization == "org.eclipse.core" || m.organization == "fr.iscpif.gridscale.bundle" || m.organization == "org.bouncycastle" }
  ) //todo, add dependency mapping or something

  lazy val openmolePlugins = AssemblyProject("openmole-plugins") settings (openmolePluginDependencies, //TODO: This project is only necessary thanks to the lack of dependency mapping in AssemblyProject
    dependencyFilter := DependencyFilter.fnToModuleFilter { m ⇒ m.extraAttributes get ("project-name") map (_ == projectName) getOrElse (m.organization == "fr.iscpif.gridscale.bundle") }
  )

  lazy val dbserverProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "dbserver") sendTo "dbserver/lib"

  lazy val runtimeProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "runtime") sendTo "plugins"

  lazy val java368URL = new URL("http://maven.iscpif.fr/public/com/oracle/java-jre-linux-i386/7-u10/java-jre-linux-i386-7-u10.tgz")
  lazy val javax64URL = new URL("http://maven.iscpif.fr/public/com/oracle/java-jre-linux-x64/7-u10/java-jre-linux-x64-7-u10.tgz")

  lazy val openmoleRuntime = AssemblyProject("runtime", "plugins", depNameMap = Map("""org\.eclipse\.equinox\.launcher.*\.jar""".r -> { s ⇒ "org.eclipse.equinox.launcher.jar" },
    """org\.eclipse\.(core|equinox|osgi)""".r -> { s ⇒ s.replaceFirst("-", "_") }), settings = resAssemblyProject ++ zipProject ++ urlDownloadProject ++ runtimeProjects) settings
    (equinoxDependencies, resourceDirectory <<= baseDirectory / "resources",
      urls <++= target { t ⇒ Seq(java368URL -> t / "jvm-386.tar.gz", javax64URL -> t / "jvm-x64.tar.gz") },
      libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale" % gridscaleVersion intransitive (),
      tarGZName := Some("runtime"),
      resourceSets <+= baseDirectory map { _ / "resources" -> "." },
      dependencyFilter := DependencyFilter.fnToModuleFilter { m ⇒ (m.organization == "org.eclipse.core" || m.organization == "fr.iscpif.gridscale.bundle") })

  lazy val daemonProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ (a contains "core") || (a contains "daemon")) sendTo "plugins"

  lazy val openmoleDaemon = AssemblyProject("daemon", "plugins", settings = resAssemblyProject ++ daemonProjects, depNameMap =
    Map("""org\.eclipse\.equinox\.launcher.*\.jar""".r -> { s ⇒ "org.eclipse.equinox.launcher.jar" }, """org\.eclipse\.(core|equinox|osgi)""".r -> { s ⇒ s.replaceFirst("-", "_") })) settings
    (resourceSets <+= baseDirectory map { _ / "resources" -> "." }, equinoxDependencies, includeGridscale, includeGridscaleSSH, dependencyFilter := DependencyFilter.fnToModuleFilter { m ⇒ m.extraAttributes get ("project-name") map (_ == projectName) getOrElse (m.organization == "org.eclipse.core" || m.organization == "fr.iscpif.gridscale.bundle") })

  lazy val docProj = Project("documentation", dir / "documentation") aggregate ((Base.subProjects ++ Gui.subProjects ++ Web.subProjects ++ Application.subProjects): _*) settings (
    unidocSettings: _*
  ) settings (compile := Analysis.Empty, scalacOptions in (ScalaUnidoc, unidoc) += "-Ymacro-no-expand",
      unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(Libraries.subProjects: _*) -- inProjects(ThirdParties.subProjects: _*)
    )
}
