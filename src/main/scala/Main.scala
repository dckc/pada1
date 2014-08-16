/**
 * Created by Brennan on 8/2/2014.
 */

import java.io._
import dispatch._, Defaults._
import dispatch.url
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

object Main extends App {

  // **The only required input is the web address of the Challonge bracket.** \\
  def gatherData(bracketAddress : String) = {

    // http://htmlcleaner.sourceforge.net/javause.php
    val service = url("http://challonge.com/" + bracketAddress)
    val page = Http(service OK as.String)

    val content = page()
    val doc = Jsoup.parse(content)
    val allSets = doc.select("td.core").asScala //each set can be found under the html tag "td.core"

    val csvFile = new PrintWriter("C:\\Users\\Public\\CSV_Files\\" + bracketAddress + ".csv")

    //METHOD - printColumnLabels()

    val setSchema = Array("tourney", "hosted_by",  "date_of",  "entrants",
      "match_num",
      "round_num",
      "w_tag", "l_tag",
      "w_games_won", "l_games_won",
      "w_seed", "l_seed")
    val setSchemaCSV = setSchema.mkString(",")
    csvFile.println(setSchemaCSV)
    // println(setSchemaCSV)

    //METHOD - findTournamentData(input)

    val tourney = doc.select("div#title").first().text()
    val host = doc.select(".host").select("a").text()
    val date = findTourneyDate(host, bracketAddress)
    val entrants = doc.select(".list .left-col").text().trim().split(" ")(0)
    // This returns the first element of a modified array of text.
    // It depends on Challonge being consistent with its organization of specific text.
    var match_num: Int = 0

    for (set <- allSets) {

      val setInfo = set.select(".participant-present").first()
      if (setInfo != null) {
        match_num += 1
        val dataRound = setInfo.attr("data-round")

        val top = set.select(".top_score").first()
        val bottom = set.select(".bottom_score").first()
        val isTopWinner = top.attr("class").contains("winner")
        val winnerHalf = if (isTopWinner) "top" else "bottom"
        val loserHalf = if (!isTopWinner) "top" else "bottom"
        var wTag = set.select(".match_" + winnerHalf + "_half").select("span").text()
        if (wTag.contains(",")) wTag = (wTag.split(",")(0) + ";" + wTag.split(",")(1))
        var lTag = set.select(".match_" + loserHalf + "_half").select("span").text()
        if (lTag.contains(",")) lTag = (lTag.split(",")(0) + ";" + lTag.split(",")(1))
        val wGamesWon = set.select("." + winnerHalf + "_score").text()
        val lGamesWon = set.select("." + loserHalf + "_score").text()
        val wSeed = set.select("." + winnerHalf + "_seed").text()
        val lSeed = set.select("." + loserHalf + "_seed").text()

        val setRow = Array(
          tourney,
          host,
          date,
          entrants,
          match_num.toString,
          dataRound,
          wTag,
          lTag,
          wGamesWon,
          lGamesWon,
          wSeed,
          lSeed
        )

        val setRowCSV = setRow.mkString(",")
        csvFile.println(setRowCSV)
        // println(setRowCSV)

      }
    } // ****End of for loop**** \\

    csvFile.close()

  }

  def findTourneyDate(tourneyHost : String, tourneyAddress : String) = {
    var pgNumber = 1
    var tourneyDate = ""
    val fullTourneyAddress = "http://challonge.com/" + tourneyAddress
    // Goal: When the "address" string matches the web address of the relevant tournament,
    // this value becomes non-null, the corresponding date is found, and the loop ends.

    while (tourneyDate.equals("")) {
      // println(pgNumber)

      val append = if (pgNumber == 1) "" else "?page=" + pgNumber
      val serviceD = url("http://challonge.com/users/" + tourneyHost + append)
      val pageD = Http(serviceD OK as.String)
      // Include a check to make sure the page exists?

      val contentD = pageD()
      val docD = Jsoup.parse(contentD)
      val allTourneys = docD.select("tr").asScala

      //print(allTourneys)

      for (oneTourney <- allTourneys) {

        val tourneyInfo = oneTourney.select("tr")
        if (tourneyInfo != null) {
          val address = tourneyInfo.select("td").select("a").attr("href")
          val date : String = tourneyInfo.select(".hidden-phone").last().text()
          if (address.equals(fullTourneyAddress)) tourneyDate = date
          //println(tourneyDate)
          //println("*****")
        }
      }

      pgNumber += 1

    }

    tourneyDate

  }

  gatherData("sappfe")

  println("Done.")

}



// ****Attempted to fnd the date; turns out to be pretty tricky. We'll come back to this later.**** \\

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
