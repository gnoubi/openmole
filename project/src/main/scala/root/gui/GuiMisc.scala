package root.gui

import sbt._
import root.base
import root.libraries._
import root.thirdparties._

package object misc extends GuiDefaults {
  override val dir = super.dir / "misc"

  lazy val all = Project("gui-misc", dir) aggregate (tools, widget, visualization)

  lazy val tools = OsgiProject("org.openmole.ide.misc.tools") dependsOn
    (base.core.implementation, provided(base.misc.workspace))

  lazy val widget = OsgiProject("org.openmole.ide.misc.widget") dependsOn
    (base.misc.workspace, base.core.model, misc.tools, jsyntaxpane,
      miglayout, netbeans, scalaSwing, base.misc.exception)

  lazy val visualization = OsgiProject("org.openmole.ide.misc.visualization") dependsOn
    (base.core.model, widget, provided(gral))
}