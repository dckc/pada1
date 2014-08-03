/**
 * Created by Brennan on 8/2/2014.
 */
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

object Extra extends App {

  import dispatch._, Defaults._

  // http://htmlcleaner.sourceforge.net/javause.php
  val svc = url("http://challonge.com/dzpm6")
  val pg = Http(svc OK as.String)

  for (content <- pg) {
    val doc = Jsoup.parse(content)
    val allSets = doc.select("td.core").asScala
    var match_num: Int = 0

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

      println(wSeed)
      println(lSeed)
      println("")


    }


    // doc.select("td.core").text()
  }
}
