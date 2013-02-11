import sbt._
import Keys._

object Dependencies {
    val antlr = "org.antlr" % "antlr" % "3.5" % "compile"

    val commonDeps = Seq(
        antlr
    )
}

object BuildSbtAntlr extends Build {
    import Dependencies._

    val sbtAntlr = Project(
        id = "sbt-antlr",
        base = file("."),

        settings = Defaults.defaultSettings ++ Seq(
            organization := "com.github.stefri",
            version := "0.4-SNAPSHOT",
            sbtPlugin := true,
            
            scalacOptions := Seq("-deprecation", "-unchecked"),

            libraryDependencies ++= commonDeps,

            publishTo <<= (version) { (v: String) =>
                val repoSuffix = if (v.contains("-SNAPSHOT")) "snapshots" else "releases"
                val resolver = Resolver.file("gh-pages",
                    new File("/Users/steffen/dev/stefri.github.com/repo", repoSuffix))
                Some(resolver)
            }
        )
    )
}
