package com.madmode.scgstats

import scala.concurrent.Future

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import dispatch.{Http, as, Req, url}
import dispatch.Defaults.executor


/**
 * Web page derived from tournament bracket
 */
class BracketInfo extends HttpServlet {
  override def doGet(request: HttpServletRequest,
                     response: HttpServletResponse) {
    for (action <- handle(request))
      action.unsafePerformIO(response)
  }

  // adapted from http://codereview.stackexchange.com/questions/6678/functionally-retrieving-rows-from-a-database-in-scala
  type World = HttpServletResponse

  trait IO[A] {
    self =>

    def unsafePerformIO(w: World): A

    def map[B](f: A => B): IO[B] = new IO[B] {
      def unsafePerformIO(w: World) = f(self.unsafePerformIO(w))
    }

    def flatMap[B](f: A => IO[B]): IO[B] = new IO[B] {
      def unsafePerformIO(w: World) = f(self.unsafePerformIO(w)).unsafePerformIO(w)
    }
  }

  object IO {
    def apply[A](comp: World => A) = new IO[A] {
      def unsafePerformIO(w: World) = comp(w)
    }
  }

  def setCT(ct: String): IO[Unit] = IO(r => r.setContentType(ct))

  def puts(line: String): IO[Unit] = IO(r => r.getWriter.println(line))

  def oneIO[A](each: Iterable[IO[Unit]]): IO[Unit] = {
    val noio = IO(_ => ())
    each.fold(noio) { (io1, io2) => io2}
  }

  def matches(tourneyName: String) = {
    for {
      content <- Http(url("http://challonge.com/" + tourneyName) OK as.String)
    } yield BracketDoc.eachMatch(content)
  }

  def handle(request: HttpServletRequest): Future[IO[Unit]] = {
    for (ms <- matches(request.getParameter("tourney"))) yield for {
      io1 <- setCT("text/plain; charset='utf-8'")
      eachLine = for (m <- ms) yield puts("match top: " + m.top + " match bottom: " + m.bottom + " match winner: " + m.winner)
      io2 <- oneIO(eachLine)
    } yield ()
  }

}
