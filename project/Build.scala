import sbt._
import Keys._

import projectRoot._

object HelloBuild extends Libraries with Web with Application {
    lazy val root = Project(id = "root",
                            base = file(".")) aggregate(libraries, web, application)

}