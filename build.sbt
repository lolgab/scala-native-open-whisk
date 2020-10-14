val commonJVMSettings = Seq(scalaVersion := "2.13.3")
val commonNativeSettings = Seq(scalaVersion := "2.11.12")

val openwhisk = crossProject(NativePlatform, JVMPlatform)
  .settings(
    name := "scala-native-open-whisk",
    organization := "com.github.lolgab",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "1.2.2",
      "com.lihaoyi" %%% "utest" % "0.7.5" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(commonJVMSettings)
  .nativeSettings(
    Test / nativeLinkStubs := true,
    commonNativeSettings
  )

val examples = crossProject(NativePlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(test := {})
  .jvmSettings(commonJVMSettings)
  .nativeSettings(commonNativeSettings)
  .dependsOn(openwhisk)
