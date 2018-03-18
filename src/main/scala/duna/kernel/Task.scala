// # Task
// ### Wrapper of a future, which is returned by a thread pool

package duna
package kernel

import scala.util.{ Try, Success, Failure }
import java.util.concurrent.Future

// Executor submit Task to a worker and receive a future back. It is not a scala future, but java, so it lacks some nice features.
case class Task[A](future: Future[A] = null ){self =>
  // We check if the future is still running.
  def isRunning: Boolean = {
    // Java's future can be null, so we need to check it. Otherwise you'll get an error. 
    if(future.isInstanceOf[Future[A]]){
      !future.isDone
        
    }else{
      false
    }
  }
  // Cancel the future.
  def cancel: Boolean = {

    if(future.isInstanceOf[Future[A]]){
      future.cancel(true)
    
    }else{
      false
    }  
        
  }
  // Blocking operation. But sometimes we need to block. Use it with caution.
  def waiting: Unit = {
    
    while(isRunning) {}

  }
  // Check if the future is finished.
  def isComplete: Boolean = {
  
    if(future.isInstanceOf[Future[A]]){
      future.isDone
    
    }else{
      true
    }
  } 
  // It is a blocking operation.
  // TODO: think if I can get rid of it.
  def get: Try[A] = {

    if(future.isInstanceOf[Future[A]]){
      Success(future.get)
    }else{
      Failure(new Throwable("Can't retreive a value from null future."))
    }

  }
}