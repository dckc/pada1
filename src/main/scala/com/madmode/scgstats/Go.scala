package com.madmode.scgstats

import util.Properties

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http


/**
 * Fetch some info from the web.
 * ack: spray.io Boot example
 */
object Go extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  //val service = system.actorOf(Props[MyServiceActor], "demo-service")
  val service = system.actorOf(BracketInfoActor.props(IO(Http)), "demo-service")


  // start a new HTTP server on port 8080 with our service actor as the handler
  val port = Properties.envOrElse("PORT", "8080").toInt
  IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = port)
}
