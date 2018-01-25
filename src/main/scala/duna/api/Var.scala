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

sealed class Var[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](manager: StateManager, private val queueSize: Int = 100, initialValue: => A){ self =>
                                           
  private val uuid: UUID = UUID.randomUUID() // the unique identificator for persistence layer
  private val subscriptionManager: SubscriptionManager[A] = SubscriptionManager()
  private val eventManager: EventManager[Time, A] = EventManager(queueSize)
  private val dataManager: DataManager[Time, A] = DataManager(Time(), initialValue)
  private val reactions: HashMap[Int, Rx[A]] = HashMap()
  @volatile private var task: Task[Seq[Try[Unit]]] = 
            Task(CompletableFuture.completedFuture[Seq[Try[Unit]]](Seq(Success[Unit](()))))


  override def toString: String = {

    eventManager.toString

  }

  def deleteTriggers: Boolean = {

    subscriptionManager.deleteAll

  }

  def onChange(cb: A => Unit): Obs[A] = {

    subscriptionManager.trigger(Callback(cb))

  }
  
  def now: A = {

    task.waiting
    dataManager.read

  }

  def apply()(implicit rx: Rx[A]): A = {

    if(reactions.get(rx.hashCode) == None){
      reactions += rx.hashCode -> rx
    } 

    dataManager.read

  }
  
 // def connectedTo[B, C](destination: Var[B])(relation: Relation[C]): Edge[A, B, C] = Edge(self, destination, relation)

  def :=(newValue: => A): ProcessingTime[Task[Seq[Try[Unit]]]] = {

    // enqueue new value
    val event = Event(Time(), Computation(() => newValue))
    
    eventManager.emit(event) 

    val work = process

    reactions.map{rx => rx._2.addTask(work.result)}
    
    work
  }

  private def createExecutable = {
      eventManager.process{(time: Time, value: A) => { 

      val written = dataManager.write(time, value)

      val res: Seq[Try[Unit]] = written match {
        
        case Success(value) => {
          
          val subscriptionRes = subscriptionManager.run(value)

          subscriptionRes

        }
        case Failure(e) => {

          Seq(Failure(e))
        
        }
      }
      Seq(written.asInstanceOf[Try[Unit]]) ++ res
    }
  }
}

  private def process: ProcessingTime[Task[Seq[Try[Unit]]]] = {

    if(task.isRunning){

      ProcessingTime(0, task)
      
    }else{

      val processing = Timer().elapsedTime{manager.exec(Exec{createExecutable})}

      task = processing.result
      
      processing
      
    }
  }
  
}

object Var{
 
  def apply[A](value: => A, queueSize: Int = 100)(implicit manager: StateManager): Var[A] = {
    
    val variable: Var[A] = new Var[A](manager, queueSize, value){}
  

    variable
    
  }

}

