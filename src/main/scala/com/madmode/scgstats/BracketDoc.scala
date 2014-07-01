package com.madmode.scgstats

import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import org.jsoup.Jsoup

/**
 * Extract structure of HTML bracket document.
 */
object BracketDoc {
  def eachMatch(content: String): Seq[Match] = {
    val doc = Jsoup.parse(content)

    for {
      table <- doc.select("table.match_table").asScala
      row <- table.select("tr").asScala
      aMatch <- row.select("td.core").asScala
      if !aMatch.select(".participant-present").isEmpty()
    } yield {
      // hmm... surely there's a more succinct way to do this...
      Match(Map(Top -> scrapePlay(aMatch.children(), Top),
        Bottom -> scrapePlay(aMatch.children(), Bottom)))
    }
  }

  def playerList(db: Seq[Match]) = {
    for { m <- db;
          (pos, play) <- m.players }
      yield play.playerName
    }

  def winLossRecord(who: String, db: Seq[Match]) : (Int, Int) = {
    val wins =
      for { m <- db;
            (pos, play) <- m.players
            if play.win == true && play.playerName == who }
      yield play
    val losses =
      for { m <- db;
            (pos, play) <- m.players
            if play.win == false && play.playerName == who }
      yield play
    (wins.size, losses.size)

  }



  def scrapePlay(aMatch: Elements, which: Position): Play = {
    val aPlay = aMatch.select(s".match_${which}_half")
    val participant = aPlay.select(".participant-present")

    // Option[Int] instead?
    def numeral(txt: String) = if (txt.length() > 0) txt.toInt else 0
    def getInt(selector: String) = {
      numeral(aPlay.select(selector).text())
    }

    Play(playerName = participant.text(),
      participantId = numeral(participant.attr("data-participant_id")),
      score = getInt(s".${which}_score"),
      seed = getInt(s".${which}_seed"),
      win = !aPlay.select(".winner").asScala.isEmpty,
      round = numeral(participant.attr("data-round")))
  }
}

sealed abstract class Position

case object Top extends Position {
  override def toString() = "top"
}

case object Bottom extends Position {
  override def toString() = "bottom"
}

// win, round should be a property of the match rather than the play.
// After all, having both players win is nonsense, as is different rounds in the same match.
case class Play(playerName: String, participantId: Int, score: Int, seed: Int, win: Boolean, round: Int)

case class Match(players: Map[Position, Play])
