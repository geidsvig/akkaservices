package com.akka.services

import akka.actor.{Actor, ActorRef}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent._
import scala.concurrent.duration._
import scala.reflect.ClassTag

/**
  * @author garretteidsvig on 2018-03-14.
  *
  * <p>A typesafe [[ActorRef]] container.</p>
  *
  * <p>Injecting an [[ActorRef]] this way protects against misconfiguration of services/controllers/etc
  * with the wrong references. We gain type safety at compile time.</p>
  *
  */
trait AkkaServiceRef {
  val ref: ActorRef
}

/**
  * @author garretteidsvig on 2018-03-14.
  *
  * <p>A base Akka service that wraps [[Actor]] syntax and treats asks as futures.</p>
  * <p>Asks are typed so that functions can expect explicit results.</p>
  * <p>The service level agreement timeout is explicit. Implicits should be avoided where possible to protect against unwanted override conflicts.</p>
  *
  * @param service the typesafe [[ActorRef]]
  * @param sla the Service Level Agreement for all request/response in this service.
  */
class AkkaService(service: AkkaServiceRef, sla: FiniteDuration) {
  import ExecutionContext.Implicits.global

  val timeout = Timeout(sla)

  /**
    * A standard actor tell function that hides the [[ActorRef]].
    *
    * @param message
    */
  def tell(message: Any): Unit = {
    service.ref.tell(message, ActorRef.noSender)
  }

  /**
    * A typesafe actor ask function that hides the [[ActorRef]] and type casting.
    *
    * @param message the message to send to the service actor.
    * @param ct scala generics magic. do not override.
    * @tparam T the explicit return type expected to complete this future.
    * @return the expected response type as per T
    */
  def typedAsk[T](message: Any)(implicit ct: ClassTag[T]): Future[T] = {
    //TODO add loggers and telemetry
    for {
      response <- service.ref.ask(message)(timeout).mapTo[T]
    } yield response
  }

}
