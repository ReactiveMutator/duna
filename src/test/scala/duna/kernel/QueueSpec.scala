package architect
package duna

import org.scalatest._
import prop._
import duna.kernel.{Queue, QueueIssue, CantEnqueueFullQueue, CantDequeueEmptyQueue, CantDequeueElementDoesntExist }
import duna.processing.events.Event
import scala.util.{ Either, Left, Right }

class QueueSpec extends PropSpec with Matchers with GeneratorDrivenPropertyChecks with Checkers{
  

  def calculateActualSize(size: Int): Int = {
    
    val availableSize = {
      val runtime = Runtime.getRuntime()

        (runtime.freeMemory/4/32).toInt // 32bits is Int size, 4 - memory share
    }

    if(size < 1 ){
      100000
      
    }else if(availableSize < size){
      availableSize 
    }else{
      size
    }
  }

  def populateViaEnqueue(array: Array[Int], size: Int): Queue[Int] = {

    val actualSize = calculateActualSize(size)

    val queue: Queue[Int] = Queue(actualSize)
    array.foreach{i => queue.enqueue(i)}
    queue
  }

  def populateViaSmartEnqueue(array: Array[Int], size: Int): Queue[Int] = {

    //val actualArray = calculateActualArray(array)
    val actualSize = calculateActualSize(size)
     val queue: Queue[Int] = Queue(actualSize)

    array.foreach{i => 
  
      def recStrategy: () => Either[Int, QueueIssue] = () => {
                queue.dequeue match {
                  case  Left(event)  => {
                 
                    queue.smartEnqueue(i)(recStrategy) // try again
                  }
                  case error => {
                      
                    error
                  }
                }
              }
      
      queue.smartEnqueue(i)(recStrategy)
    }
      
      queue
  }

              
  property("After queue population via enqueue, dequeue method must return either CantDequeueEmptyQueue or the first element of an array.") {
    forAll("queue", "size") { (array: Array[Int], size: Int) =>{

          val queue: Queue[Int] = populateViaEnqueue(array, size)
          
          if(queue.isEmpty){
            queue.dequeue should be (Right(CantDequeueEmptyQueue()) )
          }else{
            queue.dequeue should be (Left(array(0)))
          }

        
      }
    }
  }
  // TODO: separate static Spec
  property("After queue population via enqueue, queue must contain all elements from the array, if its size is more then array.length.") {
    forAll("size") { (size: Int) =>{

          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaEnqueue(array, 9)
          
          queue.dequeue should be (Left(array(0)))
          queue.dequeue should be (Left(array(1)))
          queue.dequeue should be (Left(array(2)))
          queue.dequeue should be (Left(array(3)))
          queue.dequeue should be (Left(array(4)))

      }
    }
  }

  property("After queue population via enqueue, queue must contain array.length first elements from the array, if its size is less then array.length.") {
    forAll("size") { (size: Int) =>{

          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaEnqueue(array, 3)
          
          queue.dequeue should be (Left(array(0)))
          queue.dequeue should be (Left(array(1)))
          queue.dequeue should be (Left(array(2)))
          queue.dequeue should be (Right(CantDequeueEmptyQueue()))
          queue.dequeue should be (Right(CantDequeueEmptyQueue()))

      }
    }
  }

  property("After queue population via smartEnqueue, queue must contain all elements from the array, if its size is more then array.length.") {
    forAll("size") { (size: Int) =>{

          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaSmartEnqueue(array, 9)
          
          queue.dequeue should be (Left(array(0)))
          queue.dequeue should be (Left(array(1)))
          queue.dequeue should be (Left(array(2)))
          queue.dequeue should be (Left(array(3)))
          queue.dequeue should be (Left(array(4)))

      }
    }
  }

  property("After queue population via smartEnqueue, queue must contain array.length last elements from the array, if its size is less then array.length.") {
    forAll("size") { (size: Int) =>{

          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaSmartEnqueue(array, 3)
          
          queue.dequeue should be (Left(array(2)))
          queue.dequeue should be (Left(array(3)))
          queue.dequeue should be (Left(array(4)))
          queue.dequeue should be (Right(CantDequeueEmptyQueue()))
          queue.dequeue should be (Right(CantDequeueEmptyQueue()))

      }
    }
  }



  property("After queue population via smartEnqueue, dequeue method must return CantDequeueEmptyQueue / a first element of the array / element of the array at number (array.length % actualSize).") {
    //FIXME: How can I generate array of length > size which is more then ???
    forAll("array", "size") { (array: Array[Int], size: Int) =>{
          
          val actualSize = calculateActualSize(size)
          val queue: Queue[Int] = populateViaSmartEnqueue(array, actualSize)
          val dequeueElementPosition: Int = 
           if(actualSize < array.length) {
             array.length % actualSize
           }else{
             0
           }

          queue.dequeue match {
            case Left(event)  => {
              
              Left(event) should be (Left(array(dequeueElementPosition)))
            }
            case error => {
              
              error should be (Right(CantDequeueEmptyQueue()) )
            }

          }

      }
    }
  }


}