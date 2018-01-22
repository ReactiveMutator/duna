package architect
package duna
package eventSourcing

import duna.kernel.{Queue, QueueIssue, Computation, Task, Callback}
import duna.kernel.Timer._ 

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

  def foreach(function: Event[Index, A] => Unit) = {

    eventQueue.foreach(function)
  }
  
  def toArray: Array[Event[Index, A]] = {

    eventQueue.toArray

  }

def process(newEvent: A)(work: (Index, A) => (A, Float))(cb: => Callback[A]): () => (A, Float) = () => {

    var result = (newEvent, 0.toFloat)
    
    while(!isEmpty){
 
      result = consume match {
        case Left(event: Event[Index, A]) => {
          
          val computation = event.computation.exec
          
          work(event.index, computation)

        }
        case Right(error) => {
          (newEvent, 0)
        }
      }
    } 
 
    cb.run(result._1)
    result

    }
  
  

}