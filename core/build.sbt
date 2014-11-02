name        := "spray-oauth"

version     := "1.0-SNAPSHOT"


resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "io.spray"                %%   "spray-routing" 	    % "1.3.1",
  "io.spray"                %%  "spray-json"          % "1.3.1",
  "com.typesafe.akka"       %%  "akka-actor"    	    % "2.3.5",
  "com.github.nscala-time"  %% "nscala-time"          % "1.4.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "io.spray"                %%   "spray-testkit" 	    % "1.3.1"   % "test",
  "com.typesafe.akka"       %%  "akka-testkit"  	    % "2.3.5"   % "test"
)

seq(Revolver.settings: _*)
