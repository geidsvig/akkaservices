package com.akka.services

import ExampleAkkaService._

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.scalatest._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * @author garretteidsvig on 2018-03-14.
  *
  * Proof that actors can be treated like any typical scala future function.
  * All actor management is abstracted away.
  *
  */
class AkkaServiceTest extends TestKit(ActorSystem("AkkaServices")) with FlatSpecLike with Matchers {
  import scala.concurrent.ExecutionContext.Implicits.global

  "A AkkaService" should "handle an actor tell command" in {
    val testActor = TestActorRef[ExampleAkkaServiceActor]
    val serviceRef = ExampleAkkaServiceRef(testActor)

    val service = new ExampleAkkaService(serviceRef)
    service.updateSomething(2)

    val probe = TestProbe()

    service.getSomething().map {
      case GetSomethingResponse(thing) =>
        probe.ref ! (thing == 2)
      case _ =>
        probe.ref ! false
    }

    probe.expectMsg(true)
  }

}

/**
  * @author garretteidsvig on 2018-03-14.
  *
  */
object ExampleAkkaService {
  case class ExampleAkkaServiceRef(ref: ActorRef) extends AkkaServiceRef

  /*
  Example actor service messages
   */
  case class UpdateSomething(thing: Int)
  case class GetSomething()
  case class GetSomethingResponse(thing: Int)
}

/**
  * @author garretteidsvig on 2018-03-14.
  *
  */
class ExampleAkkaServiceActor extends Actor {
  var thing: Int = 0

  def receive = {
    case UpdateSomething(thatThing) =>
      this.thing = thatThing

    case GetSomething() =>
      sender ! GetSomethingResponse(this.thing)
  }
}

/**
  * @author garretteidsvig on 2018-03-14.
  *
  * @param serviceActor
  */
class ExampleAkkaService(serviceActor: ExampleAkkaServiceRef) extends AkkaService(serviceActor, 100 milliseconds) {

  /**
    * A functional client side api call to the underlying actor service.
    *
    * @param thing
    */
  def updateSomething(thing: Int): Unit = {
    tell(UpdateSomething(thing))
  }

  /**
    * A functional client side api call to the underlying actor service.
    *
    * @return
    */
  def getSomething(): Future[GetSomethingResponse] = {
    typedAsk[GetSomethingResponse](GetSomething())
  }

}

