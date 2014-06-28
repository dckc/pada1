name := "pada1"

version := "1.0"

// We get these from http://search.maven.org/ .

libraryDependencies += "net.databinder.dispatch" % "dispatch-core_2.10" % "0.11.1"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.3"

// per http://code.google.com/p/simple-build-tool/wiki/WebApplications
// and http://code.google.com/p/simple-build-tool/wiki/WebApplicationExample

// val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided"