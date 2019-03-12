name := "template-scala-parallel-vanilla"

scalaVersion := "2.11.12"
libraryDependencies ++= Seq(
  "org.apache.predictionio" %% "apache-predictionio-core" % "0.14.0" % "provided",
  "org.apache.spark"        %% "spark-mllib"              % "2.4.0" % "provided",
  "org.scalatest"           %% "scalatest"                % "3.0.5" % "test")

// SparkContext is shared between all tests via SharedSingletonContext
parallelExecution in Test := false
