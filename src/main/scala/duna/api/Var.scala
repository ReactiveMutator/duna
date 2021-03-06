
package duna
package api

import java.util.UUID 
import duna.kernel.{ Value, Events, Read, Write, Task, Callback, Timer, ProcessingTime, ComputedList }
import scala.util.{Try, Success, Failure}
import java.util.concurrent.CompletableFuture

sealed class Var[A]
  (manager: StateManager, private val queueSize: Int = 100, initialValue: => A) extends Reactive[A](manager, queueSize){ self =>

  private val eventManager: EventManager[Time, Events[A]] = EventManager(queueSize) // queued events of the new Var's values
  private val dataManager: DataManager[Time, A] = DataManager(Time(), initialValue) // contains the current value
  private val computed: ComputedList[Rx, A] = ComputedList() // all rx, where Var is a dependency
  

  override def toString: String = {

    eventManager.toString

  }

  
  def pull(callback: A => Unit) = {

    val time = Time()
    
    val event: Value[Time, Events[A]] = Value(time, Read(callback))
    
    eventManager.emit(event) 

    process(createExecutable)
    
  }

  def now = {

      task.waiting
      dataManager.read 

  }

  def apply()(implicit rx: Rx[A]): A = {

    computed.add(rx) // memorize dependency from rx
    // find a current value
    rx.dependency(self.hashCode, dataManager.read)

  }
  
// def connectedTo[B, C](destination: Var[B])(relation: Relation[C]): Edge[A, B, C] = Edge(self, destination, relation)

  def :=(newValue: => A)  = {
    val time = Time()

    // send to all Rx hashCode of the Var and the task
    computed.signal(rx => Try{rx.addEvent(time, self.hashCode)})

    // enqueue new value
    val event: Value[Time, Events[A]] = Value(time, Write(() => newValue))
    
    eventManager.emit(event) 

    process(createExecutable)

  }

  private def createExecutable = {

    eventManager.process(() => eventManager.consume){(time: Time, value: Events[A]) => { 
      
      value match {
        
        case Write(function) => {
          
          val written = dataManager.write(time, function())

          val res = written match {
            
            case Success(inside) => {
                  // asyncronouos send a signal that Var has changed  
                  val computedRes = computed.signal{rx => 
                      Try{rx.newValue(self.hashCode, inside)}
                  }.filter{_.isFailure}.asInstanceOf[Seq[Failure[Any]]]

                  val subscriptionRes =  subscriptionManager.run(inside).filter{_.isFailure}.asInstanceOf[Seq[Failure[Any]]]

                  computedRes ++ subscriptionRes 
        
            }
            case Failure(e) => {

              Seq(Failure(e))
            
            }
          }
          res
        }
        
        case Read(function) => Seq()

      }
    }
  }
}

}

object Var{

  def apply[A](value: => A, queueSize: Int = 100)(implicit manager: StateManager): Var[A] = {
    
    val variable: Var[A] = new Var[A](manager, queueSize, value){}
    
    variable
    
  }

}

