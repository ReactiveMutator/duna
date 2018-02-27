
lazy val root = (
    project in file(".")
).aggregate(dunaJS, dunaJVM).settings(
    publish := {},
    publishLocal := {}
)

lazy val duna = crossProject.in(file(".")).
  settings(
    name := "duna",
    organization := "com.github.reactivemutator",
    version := "0.0.1",
    scalaVersion := "2.12.4",
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    libraryDependencies ++= Seq(
        "org.scalactic"  %%% "scalactic" % "3.0.5",
        "org.scalatest" %%% "scalatest" % "3.0.5" % "test",
        "org.scalacheck"  %%% "scalacheck" % "1.13.4" % "test"),
    logBuffered in Test := false
  ).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val dunaJVM = duna.jvm
lazy val dunaJS = duna.js


enablePlugins(TutPlugin)
