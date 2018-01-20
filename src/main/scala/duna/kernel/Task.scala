package architect
package duna
package kernel

import scala.util.{ Try, Success, Failure }
import java.util.concurrent.Future

case class Task[A](future: Option[Future[A]] = None){self =>

  def isRunning: Boolean = {

     future match {

      case Some(task) =>

        !task.isDone
        
      case None =>
        false

     }

  }

  def cancel: Boolean = {

    future match {

      case Some(task) =>

        task.cancel(true)
        
      case None =>

        true

     }

  }

  def waiting: Unit = {
    // Sometimes we need to block
    while(!isComplete) {}
    println("task is complete")
  }

  def isComplete: Boolean = {
    
    future match {

      case Some(task) =>

        task.isDone
        
      case None =>{
        println("no task")
        true
      }

        

     }

  } 

  def noTask: Boolean = {
    
    future match {

      case Some(task) =>

        false
        
      case None =>

        true

     }
  }

  def get: Try[A] = {
    
    future match {

      case Some(task) =>
        if(task != null){
          Success(task.get)
        }else{
          Failure(new Throwable("Can't retreive a value from null future."))
        }
        
        
      case None =>

        Failure(new Throwable("Can't retreive a value from NoTask."))

     }
  }
}