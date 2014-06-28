package com.madmode.scgstats

import scala.concurrent.Future

import javax.servlet.http.{HttpServlet, HttpServletResponse, HttpServletRequest}

/**
 * Adapt servlet interface to pure functional computation.
 * adapted from http://codereview.stackexchange.com/questions/6678/functionally-retrieving-rows-from-a-database-in-scala
 */
class FunServlet(handle: (HttpServletRequest) => Future[IO[Unit]]) extends HttpServlet {
  override def doGet(request: HttpServletRequest,
                     response: HttpServletResponse) {
    import dispatch.Defaults.executor

    for (action <- handle(request))
      action.unsafePerformIO(response)
  }
}


trait IO[A] {
  self =>

  type World = HttpServletResponse

  def unsafePerformIO(w: World): A

  def map[B](f: A => B): IO[B] = new IO[B] {
    def unsafePerformIO(w: World) = f(self.unsafePerformIO(w))
  }

  def flatMap[B](f: A => IO[B]): IO[B] = new IO[B] {
    def unsafePerformIO(w: World) = f(self.unsafePerformIO(w)).unsafePerformIO(w)
  }
}

object IO {
  type World = HttpServletResponse

  def apply[A](comp: World => A) = new IO[A] {
    def unsafePerformIO(w: World) = comp(w)
  }

  def setCT(ct: String): IO[Unit] = IO(r => r.setContentType(ct))

  def puts(line: String): IO[Unit] = IO(r => r.getWriter.println(line))

  def oneIO[A](each: Iterable[IO[Unit]]): IO[Unit] = {
    val noio = IO(_ => ())
    each.fold(noio) { (io1, io2) => io2}
  }
}
