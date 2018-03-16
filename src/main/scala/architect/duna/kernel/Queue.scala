// ### Non blocking single producer multiple consumers queue.

  package architect
  package duna
  package kernel

  import java.lang.Throwable
  import scala.util.{ Either, Left, Right }
  import scala.runtime.ScalaRunTime._
  import scala.reflect.ClassTag 
  import java.util.concurrent.ConcurrentLinkedQueue
  import scala.collection.immutable.SortedMap
  
  trait QueueIssue{
    val message: String
  }

  case class CantDequeueEmptyQueue() extends QueueIssue{
    override val message: String = "Can't dequeue for an empty queue."
  }

  // The queue is actually a circular buffer, including two pointers, which point to the next written element and the next readable element.

  case class Queue[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A: ClassTag](private val size: Int){self =>


    // Both pointers start from the zero element. They have [@volatile](http://tutorials.jenkov.com/java-concurrency/volatile.html) annotation, because we need every thread to have an access to the newest value of the variable.
    // Here is the write pointer.
    @volatile private var writePointer: Int = 0
    // And the read pointer.
    @volatile private var readPointer: Int = 0

    // We calculate an array size available on the machine.
    private val availableSize = {
      val runtime = Runtime.getRuntime()
      // Hard coded values are: 32bits is Int size, 4 - memory share
      // TODO: change integer zite to A size
      (runtime.freeMemory/4/32).toInt // 32bits is Int size, 4 - memory share
    }


    // We check the input size. If it is less than one, we make 100000 array (because I want so).

    val actualSize = size match {
      case number if(number < 1) => 100000
      case number if(number > availableSize) => availableSize
      case number => number
    }
    // Then we calculate physical location of the pointers in the array buffer. It should be from 0 to actual array size. So we need to find a reminder of current pointer position and actual array size.
    
    private def physicalReadPointer: Int = {
      readPointer  % actualSize
    }

    private def physicalWritePointer: Int = {
      writePointer  % actualSize
    }
    // Here is a buffer array, where we keep all the data.
    private val store: Array[A] = new Array[A](actualSize)
    // And the next one is for backpressure. I'll write about it below...
    private val tmpStore: ConcurrentLinkedQueue[A] = new ConcurrentLinkedQueue[A]()

    override def toString: String = {
      
      stringOf(store)

    }

    def toArray: Array[A] = {
      store
    }


    def map(function: A => Unit): Seq[Unit] = {

        store.map(function) 

    }

    // Whenever a write pointer is bigger than array size, we put next elements into the tmpStore. It is a backpressure strategy. If a producer is faster than consumer, then the default array is not enought. We start using tmpStore, which help us under heavy load. But it can cause an OutOfMemoryException exeption. Type of the tmpStore is ConcurrentLinkedQueue, so it is not limited and can be dynamically resized. Why we didn't do it before?
    // Because any linked list based data structure with unknown length at runtime replaces itself with a new allocated structure when the capacity is exceeded. A new structure is allocated and a previous one is collected multiple times. This process can generate a lot of garbage and lead to memory leak.
    def enqueue(value: => A): Either[A, QueueIssue] = {
      // The queue is full, can't rewrite an element which hasn't been ridden
      if(writePointer >= actualSize){

        tmpStore.add(value)

      }else{
        // Enqueue a new element
        store.update(phisicalWritePointer, value) // enqueue a new element

      }

      val newWritePointer = writePointer + 1 // move writePointer to the next slot
      writePointer = newWritePointer

      Left(value)

    }

    def read: Either[A, QueueIssue] = {
      if(isEmpty){ 
        
        Right{CantDequeueEmptyQueue()}

      }else{
          val res = if(readPointer < actualSize || tmpStore.isEmpty){

          store(phisicalReadPointer)

        }else{
          
            tmpStore.element

        
        }
        
          Left(res)
      }
    
    }

    def hasNext: Boolean = {
      if(isEmpty){ 
        
        false

      }else{
        val res = if(readPointer + 1 < writePointer){

            true
            
          }else{
              
              if(tmpStore.size > 2){

                true

              } else{

                false

              }
          }
          res
      }
    }
    // The next method extracts next value from the queue.
    def dequeue: Either[A, QueueIssue] = {

      if(isEmpty){ 
        
        Right{CantDequeueEmptyQueue()}

      }else{

        val res = if(readPointer < actualSize || tmpStore.isEmpty){

          val value = store(phisicalReadPointer)
          value
        }else{
          
          val dec = tmpStore.poll

          store(phisicalReadPointer) = dec
          dec

        }
        
        val newReadPointer = readPointer + 1 // The next pointer can point at an empty slot, will check on the next dequeue.
      
        readPointer = newReadPointer
        Left(res)
      }
    }

    // The queue is empty or the next pointer points to an empty slot.
    def isEmpty: Boolean = {

        readPointer == writePointer
    }


  }