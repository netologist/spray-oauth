name        := "spray-oauth-slick-plugin"

version     := "1.0-SNAPSHOT"

resolvers ++= Seq(
  "Spray repo" at "http://repo.spray.io/",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "io.spray"                %   "spray-routing" 	    % "1.2.0",
  "com.typesafe.akka"       %%  "akka-actor"    	    % "2.2.3",
  "com.typesafe.slick"      %%  "slick"    	          % "2.0.2",
  "com.typesafe.slick"      %%  "slick-extensions"    % "2.0.2",
  "joda-time"               %   "joda-time"           % "2.4",
  "org.joda"                %   "joda-convert"        % "1.6",
  "com.github.tototoshi"    %%  "slick-joda-mapper"   % "1.2.0",
  "com.h2database" % "h2" % "1.4.181" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test",
  "io.spray"                %   "spray-testkit" 	    % "1.2.0"   % "test",
  "com.typesafe.slick"      %%  "slick-testkit"    	  % "2.0.2"   % "test",
  "com.typesafe.akka"       %%  "akka-testkit"  	    % "2.2.3"   % "test"
)

seq(Revolver.settings: _*)
