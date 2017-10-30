name := "coupon-rule-engine"

version := "1.0"
enablePlugins(ScalaJSPlugin)
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.webjars" % "envjs" % "1.2",
  "com.beachape" %% "enumeratum" % "1.5.12"
)

scalaJSUseMainModuleInitializer := false