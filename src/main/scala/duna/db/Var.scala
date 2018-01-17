package architect
package duna
package db

import java.util.UUID 

import scala.collection.mutable.ListBuffer
import duna.processing.{ ITask, NoTask, Task}


sealed class Var[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](manager: StateManager, private val processor: Processor[A]){ self =>

 // private val triggers: ListBuffer[Obs[A]] = ListBuffer[Obs[A]]() // TODO: Find a suitable data structure                                            
  private val uuid: UUID = UUID.randomUUID() // the unique identificator for persistence layer

/**  private def playTriggers(newValue: A, triggerList: ListBuffer[Obs[A]]): Boolean = {
    if(!triggerList.isEmpty){
      triggerList.foreach(_.cb(newValue))
      true
    }else{
      false
    }
  }

  private def removeTrigger(obsIndex: Int): Boolean = {
    if(!triggers.isEmpty){
      triggers.remove(obsIndex)
      true
    }else{
      false
    }
  }*/

/**
  def stopTrigger(obsIndex: Int): Boolean = {

    if(isBusyWriting){
      
      newValuesQueue.add(Exec(() => { removeTrigger(obsIndex); read()}))

      false
    }
    else {
      
      removeTrigger(obsIndex)
      true  
    }
  }*/


  
  // if msg in taskQueue is needed type then process on the current thread else switch threads via StateManager


  def async(cb: A => Unit): Unit = {

    processor.onComplete(cb)

  }

  def onComplete(cb: A => Unit): Unit = {

    processor.onComplete(cb)

  }

  def apply(): Option[A] = {
  
    processor.read
    
  }
  
 // def connectedTo[B, C](destination: Var[B])(relation: Relation[C]): Edge[A, B, C] = Edge(self, destination, relation)

  def :=(newValue: => A): Boolean = {

    processor.write(newValue)

  }
}

object Var{
  
  def apply[A](value: => A, queueSize: Int = 100)(implicit manager: StateManager): Var[A] = {
    
    val processor: Processor[A] = Processor[A](manager, value, queueSize)

    val variable: Var[A] = new Var[A](manager, processor){}

    //manager.registrate(variable)
    variable
    
  }

}
