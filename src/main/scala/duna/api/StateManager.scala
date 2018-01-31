
package architect
package duna
package api

import java.util.LinkedList
import scala.collection.immutable.Queue
import duna.processing.{ Executor, Worker }
import java.util.concurrent.Future
import duna.kernel.Task 
import java.util.concurrent.CompletableFuture

case class StateManager(poolSize: Int = Runtime.getRuntime().availableProcessors()) {self =>  

 // private val registrator: LinkedList[Var[Any]] = new LinkedList() // Contains all Vars in the project

  private val executor: Executor = Executor(poolSize)

  @volatile private var tasks: List[Task[Any]] = List()
/**
  def registrate[A](node: Var[A]): Boolean = {

      self.registrator.add(node.asInstanceOf[Var[Any]])
      true

  } 

  def size: Int = {
    registrator.size
  }
*/

  def exec[A](msg: () => A): Task[A] = {
    
    val task =  Task(executor.submit(msg))
    val newTasks = task :: tasks 
    tasks = newTasks.asInstanceOf[List[Task[Any]]]
    task
 
  }

  def suspend(): Boolean = {
    
    waiting() 
    
    true
   
  }

  private def waiting(): Boolean = {
    Thread.sleep(1000) // How to avoid this?
    tasks.foreach{ task => 
      task.waiting
    } 
    
    true

  }

  def stop() = {
     
    if(!executor.isShutdown) {
        
     waiting()
     executor.close()
     true
   }else{
     false
   }

  }

}
