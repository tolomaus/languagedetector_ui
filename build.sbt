name := "languagedetector_ui"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.2"

//nlp - stanford
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models"

// language detection
libraryDependencies += "com.optimaize.languagedetector" % "language-detector" % "0.6"

// https://mvnrepository.com/artifact/io.eels/eel-core_2.11
libraryDependencies += "io.eels" %% "eel-components" % "1.1.1" exclude("org.slf4j", "slf4j-log4j12")

javaOptions in Runtime += "-Djava.util.logging.config.file=conf/logging.properties"
javaOptions in Test += "-Dconfig.resource=test.conf"

// enable both lines when remote debugging unit tests (note: add the -D system properties mentioned in this file to the sbt command)
//Keys.fork in Test := false
//Keys.parallelExecution in Test := false

