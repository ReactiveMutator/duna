package architect
package duna
package api

import java.util.UUID 
import scala.collection.mutable.HashMap
import duna.kernel.{ Computation, Task, Callback, Timer, Value, ProcessingTime }
import duna.eventSourcing.{Event, EventManager}
import duna.api.StateManager.{ Exec }
import scala.util.{Try, Success, Failure}
import java.util.concurrent.{Future, CompletableFuture}

abstract class Reactive[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]
        (manager: StateManager, private val queueSize: Int = 100){ self =>

  protected val subscriptionManager: SubscriptionManager[A] = SubscriptionManager()
  protected val uuid: UUID = UUID.randomUUID() // the unique identificator for persistence layer

  @volatile protected var task: Task[Seq[Failure[Any]]] = // contains data about current thread
          Task(CompletableFuture.completedFuture(Seq()))


  def onChange(cb: A => Unit): Obs[A] = {
      
    val res = subscriptionManager.trigger(Callback(cb))

    res
  }


    def deleteTriggers: Boolean = {

    subscriptionManager.deleteAll

  }

  protected def process(executable: () => Seq[Failure[Any]]): ProcessingTime[Task[Seq[Failure[Any]]]] = {

    if(task.isRunning){

        ProcessingTime(0, task )
      
    }else{

      val processing = Timer().elapsedTime{manager.exec(Exec{executable})}

      task = processing.result 
      
      processing
      
    }
  }
}
