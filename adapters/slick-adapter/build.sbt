name        := "spray-oauth-slick-plugin"

version     := "1.0-SNAPSHOT"


resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "io.spray"                %   "spray-routing" 	    % "1.2.0",
  "com.typesafe.akka"       %%  "akka-actor"    	    % "2.2.3",
  "io.spray"                %   "spray-testkit" 	    % "1.2.0"   % "test",
  "com.typesafe.akka"       %%  "akka-testkit"  	    % "2.2.3"   % "test"
)

seq(Revolver.settings: _*)
