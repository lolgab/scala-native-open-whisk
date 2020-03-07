val commonSettings = Seq(
  scalaVersion := "2.11.12"
)

lazy val root = project.in(file("."))
  .settings(
    commonSettings,
    name := "scala-native-open-whisk", 
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "1.0.0",
      "com.outr" %%% "scribe" % "2.7.12",
      "com.lihaoyi" %%% "utest" % "0.7.4" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    Test / nativeLinkStubs := true
  ).enablePlugins(ScalaNativePlugin)

lazy val examples = project
  .settings(commonSettings)
  .dependsOn(root)
  .enablePlugins(ScalaNativePlugin)