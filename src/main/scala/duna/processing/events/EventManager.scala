package architect
package duna
package processing 
package events

import duna.processing.time.Time
import duna.kernel.{Queue, QueueIssue, CantEnqueueFullQueue}
import scala.util.{ Either, Left, Right }

case class EventManager[A](queueSize: Int){

  @volatile private var running: Boolean = false // indicate if writer can throw elements from the queue.
    // If running is true, the reader can consume future elements and the writer has to wait. Otherwise
    // the writer can safely get rid from elements.
  private val eventQueue: Queue[Event[A]] = new Queue[Event[A]](queueSize)
/**
  def enqueue(computation: => A): Either[Event[A], QueueIssue] = {

    eventQueue.enqueue(Event(Time(), computation))

  }
*/

  def isRunning: Boolean = {
    running
  }

  private def startRunning: Boolean = {
    running = true 
    true
  }

  private def stopRunning: Boolean = {
    running = false 
    true
  }

  def put(computation: => A): Either[Event[A], QueueIssue] = {
      
      val recoveryStrategy = () => {
        if(isRunning){}
        eventQueue.dequeue match {
            case Left(event) => {
              
              put(computation) // try again
            }
            case error => error
          }
      }
      
      eventQueue.smartEnqueue(Event(Time(), () => computation))(recoveryStrategy)
  
  }
/**
  def dequeue: Either[Event[A], QueueIssue] = {
    // if None then the Queue is empty
    eventQueue.dequeue

  }
*/
   // the Queue might contain more elements. We process them all
  def next(tasks: A => Unit): Boolean = {
    
    startRunning

    while(!eventQueue.isEmpty){

      eventQueue.dequeue match {
        case Left(event) => {
            val computation = event.computation()
            tasks(computation)
            Left(computation)
        }
        case Right(error) => Right(error) // the Queue is empty or the next pointer points at the empty slot
      }
    }

    stopRunning

  }

  def isEmpty: Boolean = {

    eventQueue.isEmpty 

  }

}