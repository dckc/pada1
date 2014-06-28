package com.madmode.scgstats

import dispatch.{Http, as, url}


/**
 * Fetch some info from the web.
 * ack: http://dispatch.databinder.net/Dispatch.html
 */
object Go extends App {
  import dispatch.Defaults.executor

  // http://htmlcleaner.sourceforge.net/javause.php
  val svc = url("http://challonge.com/ARLOPMS")
  val pg = Http(svc OK as.String)
  for (content <- pg) {
    for (m <- BracketDoc.eachMatch(content)) {
      println("match top: " + m.top)
      println("match bottom: " + m.bottom)
      println("match winner: " + m.winner)

    }
    println("Done! content length:" + content.length())
  }
}
