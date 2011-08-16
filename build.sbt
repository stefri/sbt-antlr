organization := "com.github.stefri"

name := "sbt-antlr"

version := "0.1-SNAPSHOT"

sbtPlugin := true

scalacOptions := Seq("-deprecation", "-unchecked")

javacOptions := Seq("-source", "1.6", "-target", "1.6")

libraryDependencies += "org.antlr" % "antlr" % "3.3" % "compile"