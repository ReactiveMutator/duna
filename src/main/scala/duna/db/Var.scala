package architect
package duna
package db

import java.util.UUID 

import scala.collection.mutable.HashMap
import duna.kernel.{ Callback, Computation, Task }
import duna.eventSourcing.{Event, EventManager}
import duna.db.StateManager.Exec
import duna.kernel.Timer

sealed class Var[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](manager: StateManager, private val queueSize: Int = 100){ self =>
                                           
  private val uuid: UUID = UUID.randomUUID() // the unique identificator for persistence layer
  private val subscriptionManager: SubscriptionManager[A] = SubscriptionManager()
  private val eventManager: EventManager[Time, A] = EventManager(queueSize)
  private val dataManager: DataManager[Time, A] = DataManager()
  private val reactions: HashMap[Int, Rx[A]] = HashMap()
  @volatile private var task: Task[(A, Float)] = Task()

  override def toString: String = {

    eventManager.toString

  }

  def deleteTriggers: Boolean = {

    subscriptionManager.deleteAll

  }

  def onChange(cb: A => Unit): Obs[A] = {

    subscriptionManager.trigger(Callback(cb))

  }
  
  def onComplete(cb: A => Unit): Boolean = {

    subscriptionManager.complete(Callback(cb))
    true

  }

  // Blocking method
  def apply()(implicit rx: Rx[A] = null): A = {

    if(rx != null){

      if(reactions.get(rx.hashCode) == None){
        reactions += rx.hashCode -> rx
      } 

    }else{
      task.waiting
    }

    dataManager.read.get

  }
  
 // def connectedTo[B, C](destination: Var[B])(relation: Relation[C]): Edge[A, B, C] = Edge(self, destination, relation)

  def :=(newValue: => A): Boolean = {

    // enqueue new value
    val event = Event(Time(), Computation(() => newValue))
    eventManager.emit(event) 

    process(newValue)
    
  }

  private def process(newEvent: A): Boolean = {
    // enqueueing value is independent from processing, unless processing is much slower then enqueueing
    // in such cases, enqueueing must wait for a processing
    if(task.isRunning){

      true
    }else{
      
      val executable = eventManager.process(newEvent){(time: Time, value: A) => { 
          Timer().elapsedTime{

            dataManager.write(time, value)
            subscriptionManager.run(value)
            reactions.foreach{rx => rx._2.recalc}
            value
          }
          
        }
      }
      {subscriptionManager.getCompleteon}

      task = manager.exec(Exec(executable))

      true
    }
  }
}

object Var{
 
  def apply[A](value: => A, queueSize: Int = 100)(implicit manager: StateManager): Var[A] = {
    
    val variable: Var[A] = new Var[A](manager, queueSize){}

    variable := value

    variable
    
  }

}
