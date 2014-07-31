name            := "spray-oauth2-demo"

version         := "0.1"

unmanagedResourceDirectories in Compile <++= baseDirectory { base =>
  Seq( base / "src/main/webapp" )
}

resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "io.spray"                    %   "spray-can"     	    % "1.2.0",
  "io.spray"                    %   "spray-routing" 	    % "1.2.0",
  "io.spray"                    %   "spray-httpx"           % "1.2.0",
  "io.spray"                    %%  "spray-json"            % "1.2.5",
  "com.typesafe.akka"           %%  "akka-actor"    	    % "2.2.3",
  "io.spray"                    %   "spray-testkit" 	    % "1.2.0"   % "test",
  "com.typesafe.akka"           %%  "akka-testkit"  	    % "2.2.3"   % "test"
)

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)
