package architect
package duna
package api

import java.util.UUID 


import scala.collection.mutable.HashMap
import duna.kernel.{ Computation, Task, Callback, Timer }
import duna.eventSourcing.{Event, EventManager}
import duna.api.StateManager.{ Exec }

sealed class Var[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](manager: StateManager, private val queueSize: Int = 100, initialValue: => A){ self =>
                                           
  private val uuid: UUID = UUID.randomUUID() // the unique identificator for persistence layer
  private val subscriptionManager: SubscriptionManager[A] = SubscriptionManager()
  private val eventManager: EventManager[Time, A] = EventManager(queueSize)
  private val dataManager: DataManager[Time, A] = DataManager(Time(), initialValue)
  private val reactions: HashMap[Int, Rx[A]] = HashMap()
  @volatile private var task: Task[Long] = Task()


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

  def :=(newValue: => A): Boolean = {

    // enqueue new value
    val event = Event(Time(), Computation(() => newValue))
    eventManager.emit(event) 
    process(newValue)

  }

  private def process(newEvent: => A): Boolean = {
    // enqueueing value is independent from processing, unless processing is much slower then enqueueing
    // in such cases, enqueueing must wait for a processing
    if(task.isRunning){

      true
    }else{
     //   println("restart")
      //  println("eventManager.isEmpty = " + eventManager.isEmpty)
      val executable = eventManager.process{(time: Time, value: A) => { 
          Timer().elapsedTime{

            val written = dataManager.write(time, value)

            if(written){
              
              reactions.foreach{rx => rx._2.recalc}
              subscriptionManager.run(value)

            }
            
            value
          }
          
        }
      }
      
      
      task = manager.exec(Exec(executable))
      
      true
    }
  }
}

object Var{
 
  def apply[A](value: => A, queueSize: Int = 100)(implicit manager: StateManager): Var[A] = {
    
    val variable: Var[A] = new Var[A](manager, queueSize, value){}
  

    variable
    
  }

}

