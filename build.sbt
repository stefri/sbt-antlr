sbtPlugin := true

name := "sbt-antlr"

organization := "com.github.stefri"

version := "0.5.3"

libraryDependencies += "org.antlr" % "antlr" % "3.5.2" % "compile"

scalacOptions := Seq("-deprecation", "-unchecked")

publishTo <<= version { v => Some {
  val repoSuffix = if (v contains "-SNAPSHOT") "snapshots" else "releases"
  Resolver.file("gh-pages", new File("/Users/steffen/projekte/stefri.github.com/repo", repoSuffix))
}}

crossBuildingSettings

CrossBuilding.crossSbtVersions := Seq("0.12", "0.13")
