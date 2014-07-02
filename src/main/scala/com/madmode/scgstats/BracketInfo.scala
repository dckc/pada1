package com.madmode.scgstats

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import spray.http.HttpMethods.GET
import spray.http.MediaTypes._
import spray.http.{StatusCodes, HttpRequest, HttpResponse, HttpEntity, Uri}
import spray.routing._

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import reflect.ClassTag

// http://stackoverflow.com/a/15585940/846824


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

  import scala.concurrent.ExecutionContext.Implicits.global
  private implicit val timeout: Timeout = Timeout(15.seconds)

  def myWebClient: ActorRef

  def log: akka.event.LoggingAdapter

  val myRoute =
    path("") {
      getFromResource("com/madmode/scgstats/index.html")
    } ~
    path("stats") {
      get {
        parameter('tourney) { tourneyName =>
          log.info("tourney: {}", tourneyName)

          val getTourney: HttpRequest = HttpRequest(GET, Uri("http://challonge.com/" + tourneyName))

          onComplete((myWebClient ask getTourney).mapTo[HttpResponse]) {
            case Success(r) => r.status match {
              case StatusCodes.OK => respondWithMediaType(`text/plain`) {
                complete(describeMatches(r.entity))
              }
              case _ => complete(StatusCodes.BadRequest,
                s"bad tourney: ${tourneyName} => ${r.status}: ${r.status.reason}")
            }
            case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    }

  def describeMatches(pg: HttpEntity): String = {
    val em = BracketDoc.eachMatch(pg.asString)
    val db = new GameDB(em)
    val tags = db.playerList().sorted.distinct
    val setCount = for {tag <- tags} yield (tag, db.winLossRecord(tag))
    setCount.mkString("\n")
  }
}
