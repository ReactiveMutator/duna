package architect
package duna
package kernel

import java.lang.Throwable
import scala.util.{ Either, Left, Right }
import scala.runtime.ScalaRunTime._

trait QueueIssue{
  val message: String
}
case class CantEnqueueFullQueue() extends QueueIssue{
  override val message: String = "PhisicalWritePointer == phisicalReadPointer. The queue is full, can't rewrite an element which hasn't been read"
 } 
case class CantDequeueElementDoesntExist() extends QueueIssue{
  override val message: String = "Can't dequeue. An element doesn't exist."
 } 
case class CantDequeueEmptyQueue() extends QueueIssue{
  override val message: String = "Can't dequeue for an empty queue."
}

case class Queue[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A: ClassManifest](size: Int){self =>

  @volatile private var addPointer: Int = 0
  @volatile private var readPointer: Int = 0
    
  val availableSize = {
    val runtime = Runtime.getRuntime()

    (runtime.freeMemory/4/32).toInt // 32bits is Int size, 4 - memory share
  }

  val actualSize = {
    if(size < 1 ){
      100000
      
    }else if(availableSize < size){
      availableSize 
    }else{
      size
    }
  }
  
  private val store: Array[A] = new Array[A](actualSize)

  override def toString: String = {
    
    stringOf(store)

  }

  def foreach(function: Any => Unit): Unit = {

      store.deep.foreach(function) 

  }

  // It can't rewrite elements, if there is no more room for them in the array.
  // So it will leave only k = size start elements.
  def enqueue(value: => A): Either[A, QueueIssue] = {
    
    if(isFull){ // The queue is full, can't rewrite an element which hasn't been read
     
      Right(CantEnqueueFullQueue()) 

    }else{

      store.update(phisicalWritePointer, value) // enqueue a new element

      val newWritePointer = addPointer + 1 // move writePointer to the next slot
      addPointer = newWritePointer

      Left(value)
    }
    
    
  }
   // If enqueue fails, it means there is no available slot in the Queue.
  // We need to freed up some space, so you provide a callback strategy to rescue the method.
  // In short, if the queue is full, instead of waiting for a free slot, the method can dequeue the next element 
  // in a manner you characterize via a callback.
  def smartEnqueue(value: => A)(cb: () => Either[A, QueueIssue]): Either[A, QueueIssue] = {
      
    enqueue(value) match{
      case data: Left[A, QueueIssue]  => {
        
        data
      }
      case Right(issue) => { // the queue is full, dequeue some elements. 
        
        cb()
      }
    }   

  }

  def dequeue: Either[A, QueueIssue] = {
      
    if(isEmpty){ 
      
      Right{CantDequeueEmptyQueue()}

    }else{
      
      val value = store(phisicalReadPointer) // read value
      val newReadPointer = readPointer + 1 // the next pointer can point at an empty slot, will check on the next dequeue
      
      readPointer = newReadPointer

      Left(value)
    }
  }

  private def phisicalReadPointer: Int = {
    readPointer % actualSize
  }

  private def phisicalWritePointer: Int = {
    addPointer % actualSize
  }

  // the queue is empty or the next pointer points at an empty slot
  def isEmpty: Boolean = {
      //nonInitialized
      //addPointer == 0
      addPointer == readPointer
  }

  def isFull: Boolean = {
    if(addPointer == 0 && readPointer == 0) {

      false

    }else{

      (addPointer - readPointer) % actualSize == 0


    }
    

  }

}