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
  "io.spray"                    %%   "spray-can"     	    % "1.3.1",
  "io.spray"                    %%   "spray-routing" 	    % "1.3.1",
  "io.spray"                    %%   "spray-httpx"           % "1.3.0",
  "io.spray"                    %%  "spray-json"            % "1.3.1",
  "com.typesafe.akka"           %%  "akka-actor"    	    % "2.3.5",
  "io.spray"                    %%   "spray-testkit" 	    % "1.3.1"   % "test",
  "com.typesafe.akka"           %%  "akka-testkit"  	    % "2.3.5"   % "test"
)

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)
