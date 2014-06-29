logLevel := Level.Warn

// per https://github.com/spray/spray-template/
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")

// per https://devcenter.heroku.com/articles/getting-started-with-scala

resolvers += Classpaths.typesafeReleases

addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.10.0")