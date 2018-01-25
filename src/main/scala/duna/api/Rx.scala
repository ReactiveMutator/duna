package architect
package duna
package api

import duna.kernel.{Task, Callback, Computation}
import scala.collection.mutable.HashMap
import scala.collection.Map
import scala.util.{Try, Success, Failure}
import duna.eventSourcing.{EventManager, Event}
import duna.api.StateManager.{ Exec }

class Rx[A](calculation: Rx[A] => A)(manager: StateManager){ self =>
  
  private val eventManager: EventManager[Time, Task[Seq[Try[Unit]]]] = EventManager(100)

  @volatile private var value: A = calculation(self)
    
  @volatile private var completeon: Callback[A] = Callback(a => ())

  private val reactions: HashMap[Int, Rx[A]] = HashMap()

  def addTask(task: Task[Seq[Try[Unit]]]):  Seq[Try[Unit]] = {
    
    val event = Event(Time(), Computation(() => task))

    eventManager.emit(event)
    
    recalc

  }

  def waitForTasks = {

     while(!eventManager.isEmpty){

      eventManager.consume match {

        case Left(event: Event[Time, Task[Seq[Try[Unit]]]]) => {

            event.computation.exec.waiting
            
        }
        
        case Right(error) => {

          () //TODO: do something here

        }
      }
    } 
  }

  def recalc: Seq[Try[Unit]] = {

    waitForTasks
    
    val computed = Try{ calculation(self) } 

    computed match{
        case Success(good) => {

          val reactionsRes:  Seq[Try[Unit]] = 
            reactions.map{rx => rx._2.recalc.filter{_.isFailure}}.flatten.toSeq
          
          val callbackRes = Try(completeon.run(good))
          
          val finalRes: Seq[Try[Unit]] = if(callbackRes.isFailure){

            Seq(callbackRes) ++ reactionsRes

          }else if(!reactionsRes.isEmpty){

            reactionsRes
            
          }else{
            Seq()
          }

          if(finalRes.isEmpty){
            
            value = good
          }
          finalRes
      }
      case Failure(e) => Seq(Failure(e)) 
    }
      
  }

  def onChange(cb: A => Unit): Boolean = {

    completeon = Callback(cb)
    true

  }

  def apply()(implicit rx: Rx[A]): A = {

    if(reactions.get(rx.hashCode) == None){
      reactions += rx.hashCode -> rx
    } 

    value 

  }

  def now = {

      value 
  }

}
object Rx{

  def apply[A](calculation: Rx[A] => A)(implicit manager: StateManager): Rx[A] = new Rx(calculation)(manager)

}