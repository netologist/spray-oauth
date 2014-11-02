name        := "spray-oauth-salat-plugin"

version     := "1.0-SNAPSHOT"



resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Novus Release Repository" at "http://repo.novus.com/releases/"
)

libraryDependencies ++= Seq(
  "io.spray"                %%   "spray-routing" 	    % "1.2.0",
  "com.novus"               %%  "salat"                 % "1.9.9",
  "org.mongodb" 		    %%  "casbah" 			    % "2.7.3",
  "com.github.nscala-time"  %%  "nscala-time"           % "1.4.0",
  "io.spray"                %%   "spray-testkit" 	    % "1.3.1"   % "test"
)

seq(Revolver.settings: _*)
