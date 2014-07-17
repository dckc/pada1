/**
 * Regression tests for web layer
 *
 * see also BracketDocTest
 */
package com.madmode.scgstats.test

import com.madmode.scgstats._
import org.jsoup.Jsoup

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Success, Failure}


/**
 * Regression tests
 */
class BracketInfoTest  extends FlatSpec with Matchers {
  "A BracketInfo" should "handle many, many matches" in {
    val manyMatches = for { n <- 1 to 500 } yield {
      val p1 = Player("topTagIsPrettyLong" + n, n)
      val p2 = Player("bottomTagIsEvenLonger" + n, n + 1)
      Match(round = n / 10,
        scores = Map(Top -> (p1, 1),
        Bottom -> (p2, 0)),
        winner = p1)
    }

    val GB = 1024 * 1024
    BracketInfo.describeMatches(manyMatches).length should be < (1 * GB)
  }
}
