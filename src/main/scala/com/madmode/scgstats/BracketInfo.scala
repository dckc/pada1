package com.madmode.scgstats

import scala.util.{Try, Success, Failure}

import akka.event.Logging
import akka.actor.{ActorRef, Props, Actor}
import spray.routing._
import spray.routing.directives.ParameterDirectives.parameter
import spray.http._
import spray.http.StatusCodes
import MediaTypes._

// recommended practice for Actor constructor args
// http://doc.akka.io/docs/akka/snapshot/scala/actors.html#Recommended_Practices
object BracketInfoActor {
  def props(web: ActorRef) = Props(new BracketInfoActor(web))
}

// taken from spray.io example
// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class BracketInfoActor(aWebClient: ActorRef) extends Actor with BracketInfo {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)

  def myWebClient = aWebClient

  val log = Logging(context.system, this)
}


// this trait defines our service behavior independently from the service actor
/**
 * Web page derived from tournament bracket
 */
trait BracketInfo extends HttpService {

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  import akka.util.Timeout
  import scala.concurrent.duration._
  import HttpMethods.GET
  import akka.pattern.ask


  private implicit val timeout: Timeout = Timeout(15.seconds)

  def myWebClient: ActorRef

  def log: akka.event.LoggingAdapter

  val myRoute =
    path("stats") {
      get {
        parameter('tourney) { tourneyName =>
          log.info("tourney: {}", tourneyName)

          val getTourney: HttpRequest = HttpRequest(GET, Uri("http://challonge.com/" + tourneyName))

          onComplete((myWebClient ? getTourney).mapTo[HttpResponse]) {
            case Success(r) => r.status match {
              case StatusCodes.OK => respondWithMediaType(`text/plain`) {
                complete(describeMatches(r.entity))
              }
              case _ => complete(StatusCodes.BadRequest,
                s"bad tourney: ${tourneyName} => ${r.status}: ${r.message}}")
            }
            case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    }

  def describeMatches(pg: HttpEntity): String = {
    val lines = for (m <- BracketDoc.eachMatch(pg.asString))
    yield "match top: " + m.top + " match bottom: " + m.bottom + " match winner: " + m.winner

    lines.mkString("\n")
  }
}
