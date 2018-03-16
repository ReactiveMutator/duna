package architect
package duna
package api

import duna.kernel.{Queue, QueueIssue, Task, Callback, Value}
import duna.kernel.Timer._ 
import scala.util.{Try, Success, Failure}

/** A person who uses our application.
 *
 *  @constructor create a new person with a name and age.
 *  @param name the person's name
 *  @param age the person's age in years
 */
case class EventManager[Index, A](queueSize: Int){

  private val eventQueue: Queue[Value[Index, A]] = new Queue[Value[Index, A]](queueSize)

  override def toString: String = {

    eventQueue.toString

  }

  def emit(event: Value[Index, A]): Either[Value[Index, A], QueueIssue] = {

    eventQueue.enqueue(event)
  }

  def consume: Either[Value[Index, A], QueueIssue] = {

      eventQueue.dequeue 
    
  }

  def read: Either[Value[Index, A], QueueIssue] = {
    
      eventQueue.read 
  }

  def isEmpty: Boolean = {

    eventQueue.isEmpty 

  }

  def cacheSize: Int = {

    eventQueue.actualSize

  }

  def map(function: Value[Index, A] => Unit): Seq[Unit] = {

    eventQueue.map(function)
  }

  def toArray: Array[Value[Index, A]] = {

    eventQueue.toArray

  }
  // one thread at a time
  def process[B](function: () => Either[Value[Index, A], QueueIssue])(work: (Index, A) =>  Seq[Failure[B]]): () =>  Seq[Failure[B]] = () => {

    var result:   Seq[Failure[B]] = Seq()
      
      while(!isEmpty){
  
        val newResult = function() match {
          case Left(event: Value[Index, A]) => {

            work(event.index, event.value)
    
          } 
          case Right(error) => {
            
            Seq(Failure(new Throwable(error.toString)))
          }
        }
        result = result ++ newResult
      } 

    result

    }
}