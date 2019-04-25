import Dependencies._

lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "kafka-streams-azure",
    organization  := "es.hablapps",
    scalaVersion  := "2.12.8",
    version       := "1.0.0-SNAPSHOT",
    Defaults.itSettings,
    test in Test := {
      val _ = (g8Test in Test).toTask("").value
    },
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case PathList("dockerfile") => MergeStrategy.discard
      case PathList("README.markdown") => MergeStrategy.discard
      case PathList("init_container.sh") => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    mainClass in assembly := Some("es.hablapps.KSMain"),
    scriptedLaunchOpts ++= List(
      "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.url(
      "typesafe",
      url("http://repo.typesafe.com/typesafe/ivy-releases/")
    )(Resolver.ivyStylePatterns),
    libraryDependencies ++= Seq(
      kafkaStreams,
      scalaTest % Test
    ),
    logLevel in assembly := Level.Debug,
    test in assembly := {}
  )


