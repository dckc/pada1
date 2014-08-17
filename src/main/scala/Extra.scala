/**
 * Created by Brennan on 8/2/2014.
 */
import java.io._
import dispatch._, Defaults._
import dispatch.url
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

object Extra extends App {

  // http://htmlcleaner.sourceforge.net/javause.php

  val svc = url("http://challonge.com/dzpm6")
  val page = Http(svc OK as.String)

  val content = page()
    val doc = Jsoup.parse(content)
    val allSets = doc.select("td.core").asScala
    var match_num: Int = 0


    /*
    for (set <- allSets) {
      match_num += 1
      val setInfo = set.select(".participant-present").first()
      val dataRound = setInfo.attr("data-round")

      val top = set.select(".top_score").first()
      val bottom = set.select(".bottom_score").first()

      val isTopWinner = top.attr("class").contains("winner")
      val winnerHalf = if (isTopWinner) "top" else "bottom"
      val loserHalf = if (!isTopWinner) "top" else "bottom"

      val wTag = set.select(".match_" + winnerHalf + "_half").select("span").text()
      val lTag = set.select(".match_" + loserHalf + "_half").select("span").text()

      val wGamesWon = set.select("." + winnerHalf + "_score").text()
      val lGamesWon = set.select("." + loserHalf + "_score").text()

      val wSeed = set.select("." + winnerHalf + "_seed").text()
      val lSeed = set.select("." + loserHalf + "_seed").text()

    } */

    /*
    def FindStartDate (tourneyAddress: String) = {
      import dispatch._, Defaults._
      import dispatch.url

      val logAddress = tourneyAddress + "/log"

      val svc2 = url(logAddress)
      val pg2 = Http(svc2 OK as.String)

      for (content <- pg2) yield {
        val doc2 = Jsoup.parse(content)
        val timestamps = doc2.select("tr")
        println(timestamps.text())

        val theRowIWant = timestamps.asScala.find(row => row.text().contains("The organizer started this tournament."))
        theRowIWant
      }

    }

    for (dateOf <- FindStartDate("http://challonge.com/dzcscgpm3")){
      println(dateOf)
    }
    */


    val tourney = doc.select("div#title").first().text()
    val host = doc.select(".host").select("a").text()
    // val tourneyType = doc.select(".list .full-width").text().trim().split(" ")(0)
    val entrants = doc.select(".list .left-col").text().trim().split(" ")(0)
    println(entrants)

    // println(tourneyType)


    /*
    val array1 : Array[String] = Array("first", "second", "third")
    val array2 : Array[String] = Array("first", "second", "third")
    val array3 : Array[String] = Array("first", "second", "third")
    val list1 : List[Array[String]] = List(array1, array2, array3)

    println("Please work " + array1(0))
    println("Please please work " + list1(0))
    */

    /*
    val example1 = new File("example1.csv")

    val string1 = "first,second,third"
    val string2 = "first,second,third"
    val string3 = "first,second,third"
    val arrayString2 = Array(string1, string2, string3)

    println(arrayString2(0))
    println(arrayString2(1))
    println(arrayString2(2))

   def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }

    val data = Array(arrayString2)
    printToFile(example1)(p => {
      data.foreach(p.println)
    })
    */

    def findTourneyDate(tourneyHost : String, tourneyAddress : String) : String = {
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
            val date: String = tourneyInfo.select(".hidden-phone").last().text()
            if (address.equals(fullTourneyAddress)) tourneyDate = date
            //println(tourneyDate)
            //println(date)
            //println("*****")
          }
        }

        pgNumber += 1

      }

      tourneyDate

    }

    val date =
     findTourneyDate("kcsmashbros", "diordie")
    // print(date)
    // Expected result date: 12-28-13

  println("Done.")


  }

