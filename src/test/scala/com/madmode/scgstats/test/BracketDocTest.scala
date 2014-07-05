/**
 * adapted from [ScalaTest quick start][1]. See
 * also [ScalaTest support in the IntelliJ Scala plugin][2].
 *
 * I'm not quite sure how this "... should ..." syntax works. I'm just
 * using it, monkey-see-monkey-do style, from the ScalaTest docs.
 *
 * [1]: http://www.scalatest.org/quick_start
 * [2]: http://www.scalatest.org/user_guide/using_scalatest_with_intellij
 */
package com.madmode.scgstats.test

import com.madmode.scgstats._
import org.jsoup.Jsoup

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Success, Failure}


class BracketDocTest extends FlatSpec with Matchers {
  // triple-quotes are scala syntax for multi-line strings
  val txt1 = """
      <html>
        <head><title>ABC</title></head>
        <body class='nom-nom'>abc...
        <p class="nom-nom">def...</p>
        </body>
      </html>
    """


  "An HTML document" should "have text and tags (with attributes) that delimit elements" in {
    val doc = Jsoup.parse(txt1)

    doc.text() should include ("ABC")
    doc.text() should include ("abc")

    val htmlElt = doc.children().first()
    val bodyElt = htmlElt.children().get(1)  // counting from 0: head, body
    bodyElt.tagName() shouldBe "body"
    bodyElt.attr("class") shouldBe "nom-nom"

  }


  // cf. http://jsoup.org/cookbook/extracting-data/selector-syntax
  "A CSS Selector" should "concisely query an HTML document" in {

    val doc = Jsoup.parse(txt1)

    // query by tag name
    doc.select("body").size() shouldBe 1

    // query by class attribute
    doc.select(".nom-nom").text() should include ("abc")
    doc.select(".nom-nom").size() shouldBe 2

    // query by tag name and class attribute
    doc.select("p.nom-nom").text() shouldBe "def..."

  }


  "A Position" should "have a string form that matches challonge usage" in {
    Top.toString() shouldBe "top"
    Bottom.toString() shouldBe "bottom"
  }


  //http://docs.scala-lang.org/overviews/core/string-interpolation.html
  "A Scala processed literal" should "interpolate values from surrounding scope" in {
    val thing1 = "abc"
    val thing2 = "xyz"
    val result = s"before ${thing1} during ${thing2} after"
    result shouldBe "before abc during xyz after"

    val position = Top
    s"div.match_${position.toString()}_half" shouldBe "div.match_top_half"

    // recall automatic coercion to string such as...
    "abc" + 1 shouldBe "abc1"

    // Likewise, we can leave out the toString() call on position:
    s"div.match_${position}_half" shouldBe "div.match_top_half"
  }


  val matchHalfMarkup = """
    <div class='match_top_half'>
      <div class='top_seed'>8</div>
      <div class='top_score winner'>2</div>
      <div class='inner_content no_user p15741900 participant-present' data-participant_id='15741900' data-round='1'>
        <span style='' title='Deezus'>Deezus</span>
      </div>
    </div>"""

  "Match half elements" should "have a player tag and tries at id, round" in {
    val position = Top
    val aMatchHalf = Jsoup.parse(matchHalfMarkup).select(s"div.match_${position}_half")

    BracketDoc.selectParticipant(aMatchHalf) shouldBe ("Deezus", Success(1))
  }

  "Querying for an Int" should "either succeed or fail" in {
    val winlose = Jsoup.parse( """
    <div><b>xyz</b><em>23</em></div>""").select("div")

    val take1 = BracketDoc.selectInt(winlose, "div")

    (take1 match {
      case Failure(ex) => ex.toString()
    }) should include("NumberFormatException")

    val take2 = BracketDoc.selectInt(winlose, "em")
    (take2 match {
      case Success(i) => i
    }) shouldBe 23

  }

  "Match half elements" should "have a round, player, score, and win" in {
    val position = Top
    val aMatchHalf = Jsoup.parse(matchHalfMarkup).select(s"div.match_${position}_half")

    BracketDoc.selectMatchHalf(aMatchHalf, Top) shouldBe Success((1,(Player("Deezus",8),2),true))
  }

  "An empty BracketDoc" should "have an empty sequence of matches" in {
    BracketDoc.eachMatch("").isEmpty shouldBe true
  }

  val txt2 = """
  <table border='0' cellpadding='0' cellspacing='0' class='match_table match_qtip match-tipsy pid15741900 pid15742643 admin_editable' id='match_qtip_22380224'>
    <tr>
      <td style='padding:0; vertical-align:middle'>
        <div class='btn-group'>
          <a class='btn btn-link match_identifier dropdown-toggle' data-href='/matches/22380224' data-toggle='modal'>
            B
            <span class='caret'></span>
          </a>
          <ul class='dropdown-menu enabled'>
            <li>
              <a href="#" data-href="/matches/22380224" data-toggle="modal">Match Details</a>
            </li>
          </ul>
        </div>
      </td>
      <td class='core' id='match_qtip_22380224_' style='padding:0'>
        <div class='match_top_half'>
          <div class='top_seed'>8</div>
          <div class='top_score winner'>2</div>
          <div class='inner_content no_user p15741900 participant-present' data-participant_id='15741900' data-round='1'>
          <span style='' title='Deezus'>Deezus</span></div>
        </div>
        <div class='match_bottom_half'>
          <div class='bottom_seed'>25</div>
          <div class='bottom_score'>0</div>
          <div class='inner_content no_user p15742643 participant-present' data-participant_id='15742643' data-round='1'>
          <span style='' title='Lumia'>Lumia</span></div>
        </div>
        <div class='shadow'></div>
      </td>"""

  "A simple BracketDoc" should "have one match with a couple players" in {

    BracketDoc.eachMatch(txt2).head shouldBe Match(1,
      Map(Top -> (Player("Deezus", 8), 2),
        Bottom -> (Player("Lumia", 25), 0)), Top)


  }
}
