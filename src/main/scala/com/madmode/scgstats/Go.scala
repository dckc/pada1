package com.madmode.scgstats

import dispatch.{Http, as, url}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.collection.JavaConverters._

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
    val doc = Jsoup.parse(content);

    for (match_ <- doc.select("td.core").asScala) {
      val top = match_.select(".match_top_half")
      val bottom = match_.select(".match_bottom_half")
      val winner = match_.select(".winner")

      println("match top: " + top.text())
      println("match bottom: " + bottom.text())
      println("match winner: " + winner.text())

    }
    println("Done! content length:" + content.length())
  }
}
