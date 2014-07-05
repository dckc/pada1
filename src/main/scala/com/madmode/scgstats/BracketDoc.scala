package com.madmode.scgstats

import org.jsoup.select.Elements

import org.jsoup.Jsoup
import scala.collection.JavaConverters._
import scala.util.{Try, Success, Failure}

case class Match(round: Int,
                 scores: Map[Position, (Player, Int)],
                 winner: Player)

case class Player(tag: String, seed: Int)

sealed abstract class Position

case object Top extends Position {
  override def toString() = "top"
}

case object Bottom extends Position {
  override def toString() = "bottom"
}



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
      val positions = List(Top, Bottom)
      val halves = for {
        pos <- positions
        // TODO: test to explain pattern matching in for
        // skip this half if selecting the half failed
        // TODO: skip the whole match?
        Success(h) = selectMatchHalf(aMatch.children(), pos)
      } yield h

      // TODO: test to explain unzip3
      val (eachRound, eachScore, eachWin) = halves.unzip3

      val scores: Map[Position, (Player, Int)] = Map() ++ positions.zip(eachScore)
      val winner = eachWin match {
        case List(true, _) => scores(Top)._1
        case _ => scores(Bottom)._1
      }
      val round = eachRound match {
        case List(r, _) => r
        case _ => 0 // TODO: skip match if we didn't find any halves
      }

      Match(round, scores, winner)
    }
  }

  def selectParticipant(aMatchHalf: Elements): (String, Try[Int]) = {
    val participant = aMatchHalf.select(".participant-present")
    val playerTag = participant.text()
    val round = Try(participant.attr("data-round").toInt)

    (playerTag, round)
  }

  /**
   * Select text and try to convert to Int.
   * @return Success(anInt) or Failure(whynot)
   */
  def selectInt(target: Elements, selector: String): Try[Int] = {
    Try(target.select(selector).text().toInt)
  }

  def selectMatchHalf(aMatch: Elements, which: Position): Try[(Int, (Player, Int), Boolean)] = {
    val aMatchHalf = aMatch.select(s".match_${which}_half")
    val win = aMatchHalf.select(".winner").asScala.nonEmpty

    val (tag, tryRound) = selectParticipant(aMatchHalf)

    val trySeed = selectInt(aMatchHalf, s".${which}_seed")
    val tryScore = selectInt(aMatchHalf, s".${which}_score")

    for {
      round <- tryRound
      seed <- trySeed
      score <- tryScore
    } yield (round, (Player(tag, seed), score), win)
  }
}

// TODO: move this to its own file
class GameDB(db: Seq[Match]) {
  // TODO: move these to db object with players, matches tables
  def playerList() = {
    for { m <- db;
          (pos, (player, _)) <- m.scores }
    yield player.tag
  }

  def winLossRecord(who: String) : (Int, Int) = {
    val wins =
      for { m <- db;
            (pos, (player, _)) <- m.scores
            if pos == m.winner && player.tag == who }
      yield m
    val losses =
      for { m <- db;
            (pos, (player, _)) <- m.scores
            if pos != m.winner && player.tag == who }
      yield m
    (wins.size, losses.size)
  }

}