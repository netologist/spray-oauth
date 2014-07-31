name        := "spray-oauth2-salat-plugin"

version     := "0.1"



resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Novus Release Repository" at "http://repo.novus.com/releases/"
)

libraryDependencies ++= Seq(
  "io.spray"                %   "spray-routing" 	    % "1.2.0",
  "com.novus"               %%  "salat"                 % "1.9.6",
  "org.mongodb" 		    %%  "casbah" 			    % "2.6.4",
  "com.github.nscala-time"  %%  "nscala-time"           % "0.8.0",
  "io.spray"                %   "spray-testkit" 	    % "1.2.0"   % "test"
)

seq(Revolver.settings: _*)