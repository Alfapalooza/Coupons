name := """coupon-service"""

version := "no-version"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.typesafe.slick" 	   %  "slick-hikaricp_2.11" % "3.2.0",
  "com.typesafe.slick" 	   %  "slick_2.11" 				  % "3.2.0",
  "com.h2database"         %  "h2"                  % "1.4.196",
  "net.codingwell"         %% "scala-guice"         % "4.1.0"
)
