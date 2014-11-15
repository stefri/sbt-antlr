sbtPlugin := true

name := "sbt-antlr"

organization := "com.github.stefri"

version := "0.5.2"

libraryDependencies += "org.antlr" % "antlr" % "3.5.2" % "compile"

scalacOptions := Seq("-deprecation", "-unchecked")

publishTo <<= version { v => Some {
  val repoSuffix = if (v contains "-SNAPSHOT") "snapshots" else "releases"
  Resolver.file("gh-pages", new File("/Users/steffen/dev/stefri.github.com/repo", repoSuffix))
}}

credentials += Credentials(Path.userHome / ".config" / "xsbt-sh" / "nexus.config")

crossBuildingSettings

CrossBuilding.crossSbtVersions := Seq("0.12", "0.13")
