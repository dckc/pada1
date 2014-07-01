package com.madmode.scgstats.test

import com.madmode.scgstats._
import org.scalatest.{FlatSpec, Matchers}


/**
 * adapted from [ScalaTest quick start][1]. See
 * also [ScalaTest support in the IntelliJ Scala plugin][2].
 *
 * [1]: http://www.scalatest.org/quick_start
 * [2]: http://www.scalatest.org/user_guide/using_scalatest_with_intellij
 */
class BracketDocTest extends FlatSpec with Matchers {
  "An empty BracketDoc" should "have an empty sequence of matches" in {
    BracketDoc.eachMatch("").isEmpty shouldBe true
  }

  val doc1 = """
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
          <div class='inner_content no_user p15741900 participant-present' data-participant_id='15741900' data-round='1'><span style='' title='Deezus'>Deezus</span></div>
        </div>
        <div class='match_bottom_half'>
          <div class='bottom_seed'>25</div>
          <div class='bottom_score'>0</div>
          <div class='inner_content no_user p15742643 participant-present' data-participant_id='15742643' data-round='1'><span style='' title='Lumia'>Lumia</span></div>
        </div>
        <div class='shadow'></div>
      </td>"""

  "A simple BracketDoc" should "have one match with a couple players" in {
    // actually the 0, 0 are wrong...
    BracketDoc.eachMatch(doc1).head shouldBe Match(
      Map(Top -> Play("Deezus", 15741900, 0, 0, true, 1),
        Bottom -> Play("Lumia", 15742643, 0, 0, false, 1)))

  }
}
