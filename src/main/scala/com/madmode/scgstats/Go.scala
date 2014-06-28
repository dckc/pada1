package com.madmode.scgstats

/**
 * Created by connolly on 6/27/14.
 */
object Go extends App {
  import dispatch._, Defaults._
  val svc = url("http://api.hostip.info/country.php")
  val country = Http(svc OK as.String)
  for (c <- country)
    println("Hi!" + c)
}
