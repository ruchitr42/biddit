name := "biddit-platform"
organization := "com.biddit"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

// Core Dependencies (cleaned & aligned for Play 2.8.x)

libraryDependencies ++= Seq(
  guice,

  // ✅ Play-Slick (works with Play 2.8.x)
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",

  // ✅ PostgreSQL driver
  "org.postgresql" % "postgresql" % "42.7.3",

  // ✅ Akka (stay within Play's range to avoid conflicts)
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",   // Compatible with Play 2.8
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
  "com.typesafe.akka" %% "akka-http" % "10.2.6",          // Optional, compatible with 2.6.x

  // ✅ Other libraries (optional additions)
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",        // For password hashing
  "com.pauldijou" %% "jwt-play" % "5.0.0",                // JWT support (works with Play 2.8.x)

  // ✅ Testing
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)

// Optional: Override dependency conflicts
dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1"
)
