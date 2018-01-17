
package architect
package duna
package processing

import java.util.concurrent.Future
import scala.util.{ Try, Success, Failure }

trait ITask[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]
case class NoTask[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]() extends ITask[A]
case class Task[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](private val future: Future[A]) extends ITask[A]{self =>

  def cancel: Boolean = {

    future.cancel(true)

  }

  def get: Try[A] = {
    
    try{
      Success(future.get)
    }catch{
      case e: Throwable => Failure(e)
    }
      

  }

  def waiting: Unit = {
    // Sometimes we need to block
    while(!isComplete) {}

  }

  def isComplete: Boolean = 
    future.isDone

  def isCancelled: Boolean = 
    future.isCancelled

  
}
