package architect
package duna
package db

import java.util.UUID 

import duna.kernel.Callback
import duna.kernel.{ Computation, Task }
import duna.eventSourcing.{Event, EventManager}
import duna.db.StateManager.Exec
import duna.kernel.Timer

sealed class Var[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](manager: StateManager, private val queueSize: Int = 100){ self =>
                                           
  private val uuid: UUID = UUID.randomUUID() // the unique identificator for persistence layer
  private val subscriptionManager: SubscriptionManager[A] = SubscriptionManager()
  private val eventManager: EventManager[Time, A] = EventManager(queueSize)
  private val dataManager: DataManager[Time, A] = DataManager()
  @volatile private var task: Task[(A, Float)] = Task()

  override def toString: String = {

    eventManager.toString

  }

  def deleteTriggers: Boolean = {

    subscriptionManager.deleteAll

  }

  def onChange(cb: A => Unit): Obs[A] = {

    subscriptionManager.add(Callback(cb))

  }
  
  def onComplete(cb: A => Unit): Boolean = {

    subscriptionManager.setCompleteon(Callback(cb))
    true

  }

  // Blocking method
  def apply(): Option[A] = {
    
    task.waiting
    dataManager.read
    
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
