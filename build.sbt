name := "biddit-platform"
organization := "com.biddit"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  // Play-Slick and Evolutions
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  // PostgreSQL driver
  "org.postgresql" % "postgresql" % "42.7.3",
  // Akka
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
  // Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)

// Add resolver for Maven Central
resolvers += "Maven Central" at "https://repo1.maven.org/maven2/"

// Override dependency conflicts
dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1"
)