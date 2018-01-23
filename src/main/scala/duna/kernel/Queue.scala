package architect
package duna
package kernel

import java.lang.Throwable
import scala.util.{ Either, Left, Right }
import scala.runtime.ScalaRunTime._
import scala.reflect.ClassTag 

trait QueueIssue{
  val message: String
}

case class CantDequeueEmptyQueue() extends QueueIssue{
  override val message: String = "Can't dequeue for an empty queue."
}

case class Queue[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A: ClassTag](private val size: Int){self =>

  @volatile private var writePointer: Int = 0
  @volatile private var readPointer: Int = 0

  private val availableSize = {
    val runtime = Runtime.getRuntime()

    (runtime.freeMemory/4/32).toInt // 32bits is Int size, 4 - memory share
  }

  val actualSize = size match {
    case number if(number < 1) => 100000
    case number if(number > availableSize) => availableSize
    case number => number
  }
  
  
  private def phisicalReadPointer: Int = {
    readPointer  % actualSize
  }

  private def phisicalWritePointer: Int = {
    writePointer  % actualSize
  }

  private val store: Array[A] = new Array[A](actualSize) 
  
  override def toString: String = {
    
    stringOf(store)

  }

  def toArray: Array[A] = {
    store
  }

  def foreach(function: A => Unit): Unit = {

      store.deep.foreach(function.asInstanceOf[Any => Unit]) 

  }

  // It can't rewrite elements, if there is no more room for them in the array.
  // So it will leave only k = size start elements.
  def enqueue(value: => A): Either[A, QueueIssue] = {

    store.update(phisicalWritePointer, value) // enqueue a new element
    
    val newWritePointer = writePointer + 1 // move writePointer to the next slot
    writePointer = newWritePointer
    
    if(writePointer - 1 >= actualSize){ // The queue is full, can't rewrite an element which hasn't been read
      
      dequeue

    }else{
      Left(value)
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

  // the queue is empty or the next pointer points at an empty slot
  def isEmpty: Boolean = {

      readPointer == writePointer
  }


}