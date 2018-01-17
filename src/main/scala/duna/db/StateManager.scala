
package architect
package duna
package db

import java.util.concurrent.{Executors, ExecutorService}
import java.util.LinkedList
import scala.collection.mutable.Queue
import duna.processing.{ Executor, Worker, Task }
import StateManager.{Exec, Suspend, Stop, Message, EmptyMessage}

case class StateManager(poolSize: Int = Runtime.getRuntime().availableProcessors()) {self =>  

 // private val registrator: LinkedList[Var[Any]] = new LinkedList() // Contains all Vars in the project

  private val executor: Executor = Executor(poolSize)

  private def tasks[A]: Queue[Task[A]] = new Queue()
/**
  def registrate[A](node: Var[A]): Boolean = {

      self.registrator.add(node.asInstanceOf[Var[Any]])
      true

  } 

  def size: Int = {
    registrator.size
  }
*/
  private def matcher[A](msg: Message[A]): Task[A] = {
   // TODO: If executor is shutdown, an error throws. Need to make a state machine
      msg match{
              case Exec(function: (() => A)) => {
                    executor.submit(function)
                  }   
              case Suspend() => {
                    executor.submit(() => waiting()) // pure workaround to stay typesafe. Can I do better???
                  }
              case Stop() => {
                    executor.submit(() => close())
                  }
              case EmptyMessage() =>{
                executor.submit(() => doNothing())
              }
                  
            }
    
  }

  def exec[A](msg: Message[A]): Task[A] = {
    
    val future = matcher(msg)

    tasks.enqueue(future)

    future
 
  }

  def doNothing(): Boolean = {
    
    true
   
  }


  def suspend(): Boolean = {
    
    exec(Suspend())
    
    true
   
  }

  private def waiting(): Boolean = {
  
    while(!tasks.isEmpty){
      tasks.dequeue.waiting
    } 
    
    true

  }

  private def close(): Boolean = {
    if(!executor.isShutdown) {

     suspend()
     executor.close()
   }else{
     false
   }
  }

  def stop() = {

    val task = self.exec(Stop())
    task.waiting
    true

  }

}

object StateManager{

  sealed trait Message[@specialized(Int) A]

  case class EmptyMessage() extends Message[Boolean]
  case class Exec[@specialized(Int) A](f: () => A) extends Message[A]{

    def exec: A = {
      f()
      
    }
      

  }
  case class Suspend() extends Message[Boolean]
  case class Stop() extends Message[Boolean]


}
