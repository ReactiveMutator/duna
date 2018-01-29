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

    // It can't rewrite elements, if there is no more room for them in the array.
    // So it will leave only k = size start elements.
    def enqueue(value: => A): Either[A, QueueIssue] = {
      
      if(writePointer >= actualSize){ // The queue is full, can't rewrite an element which hasn't been read

        tmpStore.add(value)

      }else{

        store.update(phisicalWritePointer, value) // enqueue a new element

      }

      val newWritePointer = writePointer + 1 // move writePointer to the next slot
      writePointer = newWritePointer

      Left(value)

    }
    // do not take an element from the queue
    def read: Either[A, QueueIssue] = {
      if(isEmpty){ 
        
        Right{CantDequeueEmptyQueue()}

      }else{
          val res = if(readPointer < actualSize || tmpStore.isEmpty){

           store(phisicalReadPointer) // read value
          
        }else{
          
            tmpStore.element

         
        }
        
          Left(res)
      }
    
    }
    // do not take an element from the queue
    def hasNext: Boolean = {
      if(isEmpty){ 
        
        false

      }else{
        val res = if(readPointer + 1 < actualSize){

            true // read value
            
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
    
    def dequeue: Either[A, QueueIssue] = {

      if(isEmpty){ 
        
        Right{CantDequeueEmptyQueue()}

      }else{

        val res = if(readPointer < actualSize || tmpStore.isEmpty){

          val value = store(phisicalReadPointer) // read value
          value
        }else{
          
          val dec = tmpStore.poll

          store(phisicalReadPointer) = dec
          dec

        }
        
        val newReadPointer = readPointer + 1 // the next pointer can point at an empty slot, will check on the next dequeue
      
        readPointer = newReadPointer
        Left(res)
      }
    }

    // the queue is empty or the next pointer points at an empty slot
    def isEmpty: Boolean = {

        readPointer == writePointer
    }


  }