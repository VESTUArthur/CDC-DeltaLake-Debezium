ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion :="2.12.11"
lazy val root = (project in file("."))  .settings(    name := "PING67_test"  )
val sparkVersion = "3.1.3"
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion
libraryDependencies += "io.delta" % "delta-core" % "1.0.0" % sparkVersion
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}