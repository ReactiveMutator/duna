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
  def process(work: (Index, A) =>  Seq[Try[Unit]]): () =>  Seq[Try[Unit]] = () => {

    var result:   Seq[Try[Unit]] = Seq(Failure(new Throwable("error")))
    
    while(!isEmpty){
 
      result = consume match {
        case Left(event: Event[Index, A]) => {
          
          
            val computation = event.computation.exec
            work(event.index, computation)
          
            
        }
        
        case Right(error) => {
         Seq(Failure(new Throwable(error.toString)))
        }
      }
    } 

    result

    }
  

}