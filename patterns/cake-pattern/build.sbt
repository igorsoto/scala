name := "cake_pattern"

version := "0.1"

scalaVersion := "2.12.9"

val ItUnitTest = "it,test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % ItUnitTest
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % ItUnitTest
