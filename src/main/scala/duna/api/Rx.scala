package architect
package duna
package api

import duna.kernel.{Task, Callback, Computation, ComputedList, QueueIssue, ProcessingTime, Timer}
import scala.collection.mutable.HashMap
import scala.collection.Map
import scala.util.{Try, Success, Failure}
import duna.eventSourcing.{EventManager, Event}
import duna.api.StateManager.{ Exec }

class Rx[A](calculation: Rx[A] => A, private val bufferSize: Int, manager: StateManager) extends Reactive[A](manager, bufferSize){ self =>
  
  private val eventManager: EventManager[Time, Int] = EventManager(bufferSize)
  private val dependencyManager: DependencyManager[Int, A] = DependencyManager() // all dependent Vars
  private val dataManager: DataManager[Time, A] = DataManager(Time(), calculation(self))
  private val computed: ComputedList[Rx, A] = ComputedList()
  
  def newValue(hashVar: Int, value: A)  = synchronized{
    
    dependencyManager.put(hashVar, value) 
    println("put " + value)
    process(recalc)

  }
  
  def addEvent(time: Time, hashVar: Int) = {

    val event = Event(time,  hashVar)

    eventManager.emit(event)
    
  }

  def dependency(hashVar: Int, init: A): A = {

    dependencyManager.read(hashVar) match{
      case Some(value) => {
        println("dependency = " + hashVar + " " + value)
        value
      } 
      case None => {

        dependencyManager.put(hashVar, init)
        init

      }
    }
  }

  
  def calculateRx(rxValue:  A): Seq[Try[A]] = {
      
      val computedRes = computed.signal{rx => 
                rx.newValue(self.hashCode, rxValue).result.future.get
            }.flatten

      val subscriptionRes = subscriptionManager.run(rxValue)
           computedRes ++ subscriptionRes


      dataManager.write(Time(), rxValue)

      subscriptionRes ++ computedRes
    
          
  }

  def recalc:  () => Seq[Try[A]] = {
   // If there is an event
   eventManager.review{(time: Time, hashVar: Int) => { 
        // And if there are values 
      if(dependencyManager.hasNext(hashVar)){
        
        // We move the queue to get the next value
        dependencyManager.get(hashVar) 
            
            val calculated = Try{ calculation(self) } 

            val results = calculated match{
              case  Success(value) => {

                  println("consume")
                  eventManager.consume
                  calculateRx(value)

                }
              case Failure(error) => Seq(Failure(error))
            }
            results
        }else{
          Seq(Failure(new Throwable("Nothing was defined")))
        }
          
      }
    }
  }


  def apply()(implicit rx: Rx[A]): A = {
    
    computed.add(rx) // memorize dependency from rx
    // find a current value
    rx.dependency(self.hashCode, dataManager.read)

  }

  def now = {
      task.waiting
      dataManager.read 
  }

}
object Rx{

  def apply[A](calculation: Rx[A] => A, bufferSize: Int = 5)(implicit manager: StateManager): Rx[A] = new Rx(calculation, bufferSize, manager)

}