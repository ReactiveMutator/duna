package architect
package duna
package api

import java.util.concurrent.ConcurrentHashMap
import scala.collection.immutable.Map
import duna.kernel.{ QueueIssue, Queue, CantDequeueEmptyQueue }
import scala.reflect.ClassTag 
import scala.util.{Try, Success, Failure}

case class DependencyManager[Index, A](bufferSize: Int = 5){self =>

   @volatile private var buffer:  Map[Index, Queue[Option[A]]] = Map[Index, Queue[Option[A]]]()

  def show:  Map[Index, Queue[Option[A]]] = {
    
    buffer

  }

  def put(hash: Index, value: A): Try[A] = {
    
      buffer.get(hash) match{
        case Some(queue) => {
          queue.enqueue(Some(value)) match{
            case Left(Some(res)) => Success(res)
            case Left(None) => {println("Put Didn't read anything from the hash = " + hash); Failure(new Throwable("Didn't read anything from the hash = " + hash))}
            case Right(error) => {println(error.toString); Failure(new Throwable(error.toString))}
          }
        
        }
        case None => {
            val newQueue: Queue[Option[A]] = Queue[Option[A]](bufferSize)
            newQueue.enqueue(Some(value))
            update(hash, newQueue)
            Success(value)
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

  def read(hash: Index): Try[A] = {

      buffer.get(hash) match{
        case Some(queue) => queue.read match{
                case Left(Some(value)) => Success(value)
                case Left(None) => {println("Read Didn't read anything from the hash = " + hash); 
                Failure(new Throwable("Read Didn't read anything from the hash = " + hash))}
                case Right(error) =>  {println(error.toString); Failure(new Throwable(error.toString))}
                
              }
         case None => {println("Read Didn't find the hash = " + hash); Failure(new Throwable("Didn't find the hash = " + hash))}
         }
  }

  def hasNext(hash: Index): Boolean = {
  
       buffer.get(hash) match{
        case Some(queue) => queue.hasNext
        case None => false
      }

  }

  def get(hash: Index): Try[A] = {

      buffer.get(hash) match{
        case Some(queue) => queue.dequeue match{
            case Left(Some(value)) => Success(value)
                case Left(None) => {println("Get Didn't read anything from the hash = " + hash);
                Failure(new Throwable("Get Didn't read anything from the hash = " + hash))}
                case Right(error) =>  Failure(new Throwable(error.toString))
                
              }
         case None => Failure(new Throwable("Didn't find the hash = " + hash))
         }
      
  }

  private  def update(hash: Index, queue: Queue[Option[A]]):  Map[Index, Queue[Option[A]]] = {
    
    remove(hash)
    val newBuffer = buffer + (hash -> queue)      
    buffer = newBuffer 
    buffer
  }
 }

