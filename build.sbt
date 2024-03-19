ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"
libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.2.0"


lazy val root = (project in file("."))
  .settings(
    name := "WorkingWithAPI"
  )
