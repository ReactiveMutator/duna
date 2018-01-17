package architect
package duna
package processing

import scala.util.{ Try, Success, Failure }

case class TaskManager[A](){

  @volatile private var workingThread: ITask[A] = NoTask()

  def set(task: ITask[A]): Boolean = {

    workingThread = task 
    true

  }


  def isRunning: Boolean = {

    workingThread match{

      case task: Task[A] => {
        if(task.isComplete){
          false
        }else{
          true
        }
      }
      case NoTask() => false

    }

  }

  def cancel: Boolean = {

    workingThread match{

      case task: Task[A] => {
        task.cancel
      }
      case NoTask() => false

    }

  }

  def waiting: Boolean = {

    workingThread match{

      case task: Task[A] => {
        task.waiting
        true
      }
      case NoTask() => false

    }

  }

  def isComplete: Boolean = {
    
    workingThread match{

      case task: Task[A] => {
        if(task.isComplete){
          true
        }else{
          false
        }
      }
      case NoTask() => {
        false
      }
    }

  } 

  def noTask: Boolean = {
    
    workingThread match{

      case task: Task[A] => {
        false
      }
      case NoTask() => true

    }

  }

  def get: Try[A] = {
    
    workingThread match{

      case task: Task[A] => {
        task.get
      }
      case NoTask() => {
        Failure(new Throwable("Can't retreive a value from NoTask."))
      }
    }

  }


}