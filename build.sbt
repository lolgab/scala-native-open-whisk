val commonNativeSettings = scalaVersion := "2.11.12"

lazy val openwhisk = crossProject(NativePlatform, JVMPlatform)
  .settings(
    name := "scala-native-open-whisk",
    organization := "com.github.lolgab",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "1.0.0",
      "com.lihaoyi" %%% "utest" % "0.7.4" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .nativeSettings(
    Test / nativeLinkStubs := true,
    commonNativeSettings
  )

lazy val examples = crossProject(NativePlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .nativeSettings(commonNativeSettings)
  .dependsOn(openwhisk)
