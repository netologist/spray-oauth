name        := "spray-oauth"

version     := "1.0-SNAPSHOT"


resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "io.spray"                %   "spray-routing" 	    % "1.2.0",
  "io.spray"                %%  "spray-json"          % "1.2.6",
  "com.typesafe.akka"       %%  "akka-actor"    	    % "2.2.3",
  "com.github.nscala-time"  %%  "nscala-time"         % "0.8.0",
  "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test",
  "io.spray"                %   "spray-testkit" 	    % "1.2.0"   % "test",
  "com.typesafe.akka"       %%  "akka-testkit"  	    % "2.2.3"   % "test"
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")

seq(Revolver.settings: _*)
