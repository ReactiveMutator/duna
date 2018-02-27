val duna = (
    project in file(".")
).settings(
    scalaVersion := "2.12.4",
    name := "duna",
    organization := "com.github.reactivemutator",
    version := "0.0.1",
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    libraryDependencies ++= Seq(
        "org.scalactic" %% "scalactic" % "3.0.4",
        "org.scalatest" % "scalatest_2.12" % "3.0.4" % "test",
        "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"),
    logBuffered in Test := false
    
)
enablePlugins(TutPlugin)