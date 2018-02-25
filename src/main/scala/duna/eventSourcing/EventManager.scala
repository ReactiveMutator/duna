package architect
package duna
package eventSourcing

import duna.kernel.{Queue, QueueIssue, Computation, Task, Callback}
import duna.kernel.Timer._ 
import scala.util.{Try, Success, Failure}

case class EventManager[Index, A](queueSize: Int){

  private val eventQueue: Queue[Event[Index, A]] = new Queue[Event[Index, A]](queueSize)

  override def toString: String = {

    eventQueue.toString

  }

  def emit(event: Event[Index, A]): Either[Event[Index, A], QueueIssue] = {

    eventQueue.enqueue(event)
  }

  def consume: Either[Event[Index, A], QueueIssue] = {

      eventQueue.dequeue 
    
  }

  def read: Either[Event[Index, A], QueueIssue] = {
    
      eventQueue.read 
  }

  def isEmpty: Boolean = {

    eventQueue.isEmpty 

  }

  def cacheSize: Int = {

    eventQueue.actualSize

  }

  def map(function: Event[Index, A] => Unit): Seq[Unit] = {

    eventQueue.map(function)
  }

  def toArray: Array[Event[Index, A]] = {

    eventQueue.toArray

  }
  // one thread at a time
  def process[B](function: () => Either[Event[Index, A], QueueIssue])(work: (Index, A) =>  Seq[Failure[B]]): () =>  Seq[Failure[B]] = () => {

    var result:   Seq[Failure[B]] = Seq()
      
      while(!isEmpty){
  
        val newResult = function() match {
          case Left(event: Event[Index, A]) => {

            work(event.index, event.computation)
    
          } 
          case Right(error) => { 
 \
            Seq(Failure(new Throwable(error.toString)))
          }
        }
        result = result ++ newResult
      } 

    result

    }
}