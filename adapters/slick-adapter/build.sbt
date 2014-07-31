name        := "spray-oauth2-slick-plugin"

version     := "0.1"


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
