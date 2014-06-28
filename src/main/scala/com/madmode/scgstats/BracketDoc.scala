package com.madmode.scgstats

import scala.collection.JavaConverters._
import org.jsoup.Jsoup

/**
 * Extract structure of HTML bracket document.
 */
object BracketDoc {
  def eachMatch(content: String) = {
    val doc = Jsoup.parse(content)

    for {
      match_ <- doc.select("td.core").asScala
    } yield new {
      val top = match_.select(".match_top_half").text()
      val bottom = match_.select(".match_bottom_half").text()
      val winner = match_.select(".winner").text()
    }

  }
}
