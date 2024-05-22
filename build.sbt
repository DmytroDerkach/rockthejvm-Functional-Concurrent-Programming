ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "advanced-scala2"
  )


libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.11",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3"
)
