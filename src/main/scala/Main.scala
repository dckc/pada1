/**
 * Created by Brennan on 8/2/2014.
 */

import dispatch._, Defaults._
import dispatch.url
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

object Main extends App {

  // http://htmlcleaner.sourceforge.net/javause.php
  val svc = url("http://challonge.com/dzcscgpm3")
  val pg = Http(svc OK as.String)

  for (content <- pg) {
    val doc = Jsoup.parse(content)
    val allSets = doc.select("td.core").asScala
    var match_num: Int = 0

    val setSchema = Array("tourney", "hosted_by", "entrants", "match_num", "round_num", "w_tag", "l_tag", "w_games_won", "l_games_won", "w_seed", "l_seed")
    // Include "date_of" column later.
    val setSchemaCSV = setSchema.mkString(",")
    println(setSchemaCSV)

    val tourney = doc.select("div#title").first().text()
    val host = doc.select(".host").select("a").text()
    val entrants = doc.select(".list .full-width").text().trim().split(" ")(0)
    // This returns the first element of a modified array of text.
    // It depends on Challonge being consistent with its organization of specific text.


    for  (set <- allSets) {

      val setInfo = set.select(".participant-present").first()
      if (setInfo != null) {
        match_num += 1
        val dataRound = setInfo.attr("data-round")

        val top = set.select(".top_score").first()
        val bottom = set.select(".bottom_score").first()
        val isTopWinner = top.attr("class").contains("winner")
        val winnerHalf = if (isTopWinner) "top" else "bottom"
        val loserHalf = if (!isTopWinner) "top" else "bottom"

        val setRow = Array(
          tourney,
          host,
          entrants,
          match_num.toString,
          dataRound,
          set.select(".match_" + winnerHalf + "_half").select("span").text(),
          set.select(".match_" + loserHalf + "_half").select("span").text(),
          set.select("." + winnerHalf + "_score").text(),
          set.select("." + loserHalf + "_score").text(),
          set.select("." + winnerHalf + "_seed").text(),
          set.select("." + loserHalf + "_seed").text()
        )

        val setRowCSV = setRow.mkString(",")


        println(setRowCSV)
        // println(match_num)

      }

    }

    // ***Attempted to fnd the date; turns out to be pretty tricky. We'll come back to this later.***

    /* def FindStartDate (tourneyAddress: String) : String = {
      import dispatch._, Defaults._
      import dispatch.url

      val logAddress = tourneyAddress + "/log"

      val svc2 = url(logAddress)
      val pg2 = Http(svc2 OK as.String)

      for (content <- pg2) {
        val doc2 = Jsoup.parse(content)
        val startDate = doc2.select()
      }
    } */


   }

}
