package root

import sbt._
import Keys._
import com.typesafe.sbt.osgi.OsgiKeys
import OsgiKeys._
import root.libraries._
import org.openmole.buildsystem.OMKeys._

/**
 * Created with IntelliJ IDEA.
 * User: luft
 * Date: 3/17/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
object Libraries extends Defaults(Apache) {

  val dir = file("libraries")

  val gridscaleVersion = "1.59"

  val bouncyCastleVersion = "1.49"

  lazy val includeGridscale = libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale" % gridscaleVersion

  lazy val includeGridscaleSSH = libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.ssh" % gridscaleVersion

  lazy val includeGridscalePBS = libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.pbs" % gridscaleVersion

  lazy val includeGridscaleGlite = libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.glite" % gridscaleVersion

  lazy val includeGridscaleDirac = libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.dirac" % gridscaleVersion

  lazy val includeGridscaleHTTP = libraryDependencies += "fr.iscpif.gridscale.bundle" % "fr.iscpif.gridscale.http" % gridscaleVersion

  lazy val includeBouncyCastle = libraryDependencies += "org.bouncycastle" % "bcprov-jdk15on" % bouncyCastleVersion

  lazy val includeOsgi = libraryDependencies <+= (osgiVersion) { oV ⇒ "org.eclipse.core" % "org.eclipse.osgi" % oV }

  /*lazy val all = Project(id = "openmole-libraries",
    base = file("libraries")) aggregate (jetty, scalatra, logback, h2, bonecp, slick, slf4j, xstream, groovy,
      objenesis, scalaLang, Apache.all, jodaTime, gnuCrypto, db4o, jasypt, robustIt, netlogo4, netlogo5, opencsv,
      netlogo4_noscala, netlogo5_noscala, guava, jsyntaxpane, gral, miglayout, netbeans, mgo, jline, jacksonJson, scalaCompiler)*/

  lazy val jetty = OsgiProject("org.eclipse.jetty", exports = Seq("org.eclipse.jetty.*", "javax.*")) settings
    (libraryDependencies ++= Seq("org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106",
      "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016"))

  lazy val scalatra = OsgiProject("org.scalatra",
    buddyPolicy = Some("global"),
    exports = Seq("org.scalatra.*, org.fusesource.*"),
    privatePackages = Seq("!scala.*", "!org.slf4j.*", "!org.json4s", "*")) settings
    (libraryDependencies ++= Seq("org.scalatra" %% "scalatra" % "2.2.1",
      "org.scalatra" %% "scalatra-scalate" % "2.2.1",
      "org.scalatra" %% "scalatra-json" % "2.2.1")) dependsOn (slf4j)

  lazy val jacksonJson = OsgiProject("org.json4s") settings (
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.4",
    exportPackage += "com.fasterxml.*"
  )

  lazy val logback = OsgiProject("ch.qos.logback", exports = Seq("ch.qos.logback.*", "org.slf4j.impl")) settings
    (libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9")

  lazy val h2 = OsgiProject("org.h2", buddyPolicy = Some("global"), privatePackages = Seq("META-INF.*")) settings
    (libraryDependencies += "com.h2database" % "h2" % "1.3.170")

  lazy val bonecp = OsgiProject("com.jolbox.bonecp", buddyPolicy = Some("global")) settings
    (libraryDependencies += "com.jolbox" % "bonecp" % "0.8.0-rc1")

  lazy val slick = OsgiProject("com.typesafe.slick", exports = Seq("scala.slick.*")) settings
    (libraryDependencies += "com.typesafe.slick" %% "slick" % "1.0.0")

  lazy val slf4j = OsgiProject("org.slf4j") settings (libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.2")

  lazy val xstream = OsgiProject("com.thoughtworks.xstream", buddyPolicy = Some("global"), privatePackages = Seq("!scala.*", "*")) settings
    (libraryDependencies ++= Seq("com.thoughtworks.xstream" % "xstream" % "1.4.4",
      "net.sf.kxml" % "kxml2" % "2.3.0"), bundleType += "dbserver")

  lazy val groovy = OsgiProject("org.codehaus.groovy", buddyPolicy = Some("global"), exports = Seq("groovy.*", "org.codehaus.*"),
    privatePackages = Seq("!scala.*,*")) settings (libraryDependencies ++= Seq("org.codehaus.groovy" % "groovy-all" % "2.1.9",
      "org.fusesource.jansi" % "jansi" % "1.2.1"))

  lazy val objenesis = OsgiProject("org.objenesis") settings (libraryDependencies += "org.objenesis" % "objenesis" % "1.2")

  lazy val scalaLang = OsgiProject("org.scala-lang.scala-library", exports = Seq("akka.*", "com.typesafe.*", "scala.*", "scalax.*"),
    privatePackages = Seq("*"), buddyPolicy = Some("global")
  ) settings
    (libraryDependencies <++= (scalaVersion) { sV ⇒
      Seq("org.scala-lang" % "scala-library" % sV,
        "org.scala-lang" % "scala-reflect" % sV,
        "org.scala-lang" % "scala-actors" % sV,
        "org.scala-lang" % "jline" % sV,
        "com.typesafe.akka" %% "akka-actor" % "2.1.4",
        "com.typesafe.akka" %% "akka-transactor" % "2.1.4",
        "com.typesafe" % "config" % "1.0.0",
        "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2")
    }, bundleType += "dbserver")

  lazy val scalaCompiler = OsgiProject("org.scala-lang.scala-compiler", exports = Seq("scala.reflect.*", "scala.tools.*"),
    privatePackages = Seq("!scala.*", "*"), buddyPolicy = Some("global")) settings (libraryDependencies <<= scalaVersion { s ⇒ Seq("org.scala-lang" % "scala-compiler" % s) })

  lazy val jodaTime = OsgiProject("org.joda.time") settings (libraryDependencies += "joda-time" % "joda-time" % "1.6")

  lazy val gnuCrypto = OsgiProject("org.gnu.crypto") settings (
    libraryDependencies += "org.gnu.crypto" % "gnu-crypto" % "2.0.1",
    exportPackage += "gnu.crypto.*"
  )

  lazy val jasypt = OsgiProject("org.jasypt.encryption", exports = Seq("org.jasypt.*")) settings (libraryDependencies += "org.jasypt" % "jasypt" % "1.8")

  lazy val db4o = OsgiProject("com.db4o", buddyPolicy = Some("global")) settings
    (libraryDependencies += "com.db4o" % "db4o-full-java5" % "8.1-SNAPSHOT", bundleType += "dbserver")

  lazy val robustIt = OsgiProject("uk.com.robustit.cloning", exports = Seq("com.rits.*")) settings
    (libraryDependencies += "uk.com.robust-it" % "cloning" % "1.7.4")

  lazy val netlogo4_noscala = OsgiProject("ccl.northwestern.edu.netlogo4.noscala", exports = Seq("org.nlogo.*"),
    privatePackages = Seq("!scala.*", "*")) settings
    (libraryDependencies ++=
      Seq("ccl.northwestern.edu" % "netlogo" % "4.1.3",
        "org.picocontainer" % "picocontainer" % "2.8",
        "org.objectweb" % "asm" % "3.1",
        "org.objectweb" % "asm-commons" % "3.1"), version := "4.1.3", scalaVersion := "2.10.0", bundleType := Set("all"),
        ivyScala ~= { (is: Option[IvyScala]) ⇒ //should disable the binary compat warnings this causes
          for (i ← is) yield i.copy(checkExplicit = false)
        })

  lazy val netlogo5_noscala = OsgiProject("ccl.northwestern.edu.netlogo5.noscala", exports = Seq("org.nlogo.*"),
    privatePackages = Seq("!scala.*", "*")) settings
    (libraryDependencies ++=
      Seq("ccl.northwestern.edu" % "netlogo" % "5.0.4",
        "org.picocontainer" % "picocontainer" % "2.13.6",
        "org.objectweb" % "asm-all" % "3.3.1"), version := "5.0.3", scalaVersion := "2.10.0", bundleType := Set("all"),
        ivyScala ~= { (is: Option[IvyScala]) ⇒ //See netlogo4_noscala
          for (i ← is) yield i.copy(checkExplicit = false)
        })

  lazy val netlogo4 = OsgiProject("ccl.northwestern.edu.netlogo4", exports = Seq("org.nlogo.*"),
    privatePackages = Seq("*")) settings
    (libraryDependencies ++=
      Seq("ccl.northwestern.edu" % "netlogo" % "4.1.3",
        "org.picocontainer" % "picocontainer" % "2.8",
        "org.objectweb" % "asm" % "3.1",
        "org.objectweb" % "asm-commons" % "3.1"), version := "4.1.3", scalaVersion := "2.8.0", bundleType := Set("plugin"))

  lazy val netlogo5 = OsgiProject("ccl.northwestern.edu.netlogo5", exports = Seq("org.nlogo.*"),
    privatePackages = Seq("*")) settings
    (libraryDependencies ++=
      Seq("ccl.northwestern.edu" % "netlogo" % "5.0.4",
        "org.scala-lang" % "scala-library" % "2.9.2",
        "org.objectweb" % "asm-all" % "3.3.1",
        "org.picocontainer" % "picocontainer" % "2.13.6"), version := "5.0.3", scalaVersion := "2.9.2", bundleType := Set("plugin"))

  lazy val guava = OsgiProject("com.google.guava",
    exports = Seq("com.google.common.*"), privatePackages = Seq("!scala.*", "*")) settings (libraryDependencies ++=
      Seq("com.google.guava" % "guava" % "14.0.1", "com.google.code.findbugs" % "jsr305" % "1.3.9")
    )

  lazy val jsyntaxpane = OsgiProject("jsyntaxpane", privatePackages = Seq("!scala.*", "*")) settings
    (libraryDependencies += "jsyntaxpane" % "jsyntaxpane" % "0.9.6")

  lazy val gral = OsgiProject("de.erichseifert.gral", privatePackages = Seq("!scala.*", "*")) settings
    (libraryDependencies += "de.erichseifert.gral" % "gral-core" % "0.9-SNAPSHOT")

  lazy val miglayout = OsgiProject("net.miginfocom.swing.miglayout", exports = Seq("net.miginfocom.*")) settings
    (libraryDependencies += "com.miglayout" % "miglayout" % "3.7.4")

  lazy val netbeans = OsgiProject("org.netbeans.api", exports = Seq("org.netbeans.*", "org.openide.*")) settings
    (libraryDependencies ++= Seq("org.netbeans.api" % "org-netbeans-api-visual" % "RELEASE73",
      "org.netbeans.api" % "org-netbeans-modules-settings" % "RELEASE73"))

  lazy val mgo = OsgiProject("fr.iscpif.mgo") settings (libraryDependencies += "fr.iscpif" %% "mgo" % "1.66-SNAPSHOT", bundleType := Set("plugin"))

  lazy val opencsv = OsgiProject("au.com.bytecode.opencsv") settings (libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.0", bundleType := Set("plugin"))

  lazy val jline = OsgiProject("net.sourceforge.jline") settings (libraryDependencies += "jline" % "jline" % "0.9.94", exportPackage := Seq("jline.*"))

  lazy val arm = OsgiProject("com.jsuereth.scala-arm") settings (libraryDependencies += "com.jsuereth" %% "scala-arm" % "1.3", exportPackage := Seq("resource.*"))

  lazy val scalajHttp = OsgiProject("org.scalaj.scalaj-http") settings (libraryDependencies += "org.scalaj" %% "scalaj-http" % "0.3.10", exportPackage := Seq("scalaj.http.*"))

  lazy val scalaz = OsgiProject("org.scalaz", exports = Seq("scalaz.*")) settings
    (libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.0.4")

  override def OsgiSettings = super.OsgiSettings ++ Seq(bundleType := Set("core")) //TODO make library defaults
}
