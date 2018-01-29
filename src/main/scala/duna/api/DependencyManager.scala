package architect
package duna
package api

import java.util.concurrent.ConcurrentHashMap
import scala.collection.immutable.Map
import duna.kernel.{ QueueIssue, Queue, CantDequeueEmptyQueue }
import scala.reflect.ClassTag 

case class DependencyManager[Index, A](bufferSize: Int = 5){self =>

   @volatile private var buffer:  Map[Index, Queue[Option[A]]] = Map[Index, Queue[Option[A]]]()

  def show:  Map[Index, Queue[Option[A]]] = {
    
    buffer

  }

  def put(hash: Index, value: A): Either[Option[A], QueueIssue] = {
    
      buffer.get(hash) match{
        case Some(queue) => queue.enqueue(Some(value)) 
        case None => {
           val newQueue: Queue[Option[A]] = Queue[Option[A]](bufferSize)
            newQueue.enqueue(Some(value))
            update(hash, newQueue)
            Left(Some(value))
        }
      }
  }

  private def remove(hash: Index):  Map[Index, Queue[Option[A]]] = {
    if(buffer.contains(hash)){
      val newBuffer = buffer - hash // remove
      buffer = newBuffer
    }
    buffer      

  }

  def read(hash: Index): Option[A] = {

      buffer.get(hash) match{
        case Some(queue) => queue.read match{
                case Left(value) => value
                case Right(error) => 
                {
                  println("buffer.get(hash).read")
                  None
                }
              }
         case None => None
         }
  }

  def hasNext(hash: Index): Boolean = {
  
       buffer.get(hash) match{
        case Some(queue) => queue.hasNext
        case None => false
      }

  }

  def get(hash: Index): Option[A] = {

      buffer.get(hash) match{
        case Some(queue) => queue.dequeue match{
            case Left(value) => value
            case Right(error) => None
          }
        case None => None
      }
      
  }

  private  def update(hash: Index, queue: Queue[Option[A]]):  Map[Index, Queue[Option[A]]] = {
    
    remove(hash)
    val newBuffer = buffer + (hash -> queue)      
    buffer = newBuffer 
    buffer
  }
 }

