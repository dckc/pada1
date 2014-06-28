package com.madmode.scgstats

/**
 * Fetch some info from the web.
 * ack: http://dispatch.databinder.net/Dispatch.html
 */
object Go extends App {
  import dispatch._, Defaults._
  val svc = url("http://api.hostip.info/country.php")
  val country = Http(svc OK as.String)
  for (c <- country)
    println("Hi!" + c)
}
