package architect
package duna
package api

import java.util.UUID 
import duna.kernel.{ Task, Callback, Timer, ProcessingTime }
import scala.util.{Try, Success, Failure}
import java.util.concurrent.CompletableFuture

abstract class Reactive[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]
        (manager: StateManager, private val queueSize: Int){ self =>

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
         
      val processing = Timer().elapsedTime{manager.exec(executable)}
      
      task = processing.result

      processing
      
    }
  }
}
