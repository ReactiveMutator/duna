
package duna
package kernel

import scala.util.{ Try, Success, Failure }
import java.util.concurrent.Future

case class Task[A](future: Future[A] = null ){self =>

  def isRunning: Boolean = {

    if(future.isInstanceOf[Future[A]]){
      !future.isDone
        
    }else{
      false
    }
  }

  def cancel: Boolean = {

    if(future.isInstanceOf[Future[A]]){
      future.cancel(true)
    
    }else{
      false
    }  
        
  }

  def waiting: Unit = {
    // Sometimes we need to block
    while(isRunning) {}

  }

  def isComplete: Boolean = {
  
    if(future.isInstanceOf[Future[A]]){
      future.isDone
    
    }else{
      true
    }
  } 

  def get: Try[A] = {

    if(future.isInstanceOf[Future[A]]){
      Success(future.get)
    }else{
      Failure(new Throwable("Can't retreive a value from null future."))
    }

  }
}