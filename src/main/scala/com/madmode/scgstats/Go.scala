package com.madmode.scgstats

import dispatch.{Http, as, url}
import org.htmlcleaner.HtmlCleaner

/**
 * Fetch some info from the web.
 * ack: http://dispatch.databinder.net/Dispatch.html
 */
object Go extends App {
  import dispatch.Defaults.executor

  // http://htmlcleaner.sourceforge.net/javause.php
  val svc = url("http://htmlcleaner.sourceforge.net/javause.php")
  val pg = Http(svc OK as.String)
  for (content <- pg) {
    val cleaner = new HtmlCleaner();
    val doc = cleaner.clean(content);

    for (elem <- doc.getElementsByName("a", true)) {
      println("anchor: " + elem.getText)
    }

    println("Done! content length:" + content.length())
  }
}
