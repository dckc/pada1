package com.madmode.scgstats

import scala.concurrent.Future

import javax.servlet.http.HttpServletRequest

import dispatch.{Http, as, url}


/**
 * Web page derived from tournament bracket
 */
class BracketInfo extends FunServlet(BracketInfo.handle) {
}

object BracketInfo {

  import IO.{setCT, puts, oneIO}
  import dispatch.Defaults.executor

  def handle(request: HttpServletRequest): Future[IO[Unit]] = {
    val bracketRequest = Http(url("http://challonge.com/" + request.getParameter("tourney")) OK as.String)

    val matches = for {
      content <- bracketRequest
    } yield BracketDoc.eachMatch(content)

    for (ms <- matches) yield for {
      io1 <- setCT("text/plain; charset='utf-8'")
      eachLine = for (m <- ms) yield puts("match top: " + m.top + " match bottom: " + m.bottom + " match winner: " + m.winner)
      io2 <- oneIO(eachLine)
    } yield ()
  }

}
