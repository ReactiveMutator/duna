
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }

class QueueFlatSpec extends FlatSpec with Matchers{

  "The queue" should "contain all elements from the array, if it's size is more then the array's length." in {
         
   
        val array: Array[Int] = Array(1, 2, 3, 4, 5)
        val queue: Queue[Int] = populateViaEnqueue(array, 9)

        (queue.toArray: Seq[Int]) should be (Array(1, 2, 3, 4, 5, 0, 0, 0, 0): Seq[Int])
        queue.dequeue should be (Left(1))    
         
          

  }

  "The queue" should "contain array.length last elements from the array, if it's size is less then array's length." in {
    
        val array: Array[Int] = Array(1, 2, 3, 4, 5)
        val queue: Queue[Int] = populateViaEnqueue(array, 3)
        
        (queue.toArray: Seq[Int]) should be (Array(1, 2, 3): Seq[Int])
        queue.dequeue should be (Left(1))
        

  }

  "The queue" should "be empty after dequeue of all elements" in {

          val array: Array[Int] = Array(1, 2, 3)
          val queue: Queue[Int] = populateViaEnqueue(array, 5)
          
          queue.dequeue should be (Left(array(0)))
          queue.dequeue should be (Left(array(1)))
          queue.dequeue should be (Left(array(2)))
          queue.isEmpty should be (true)


  }

  "Dequeue" should "return CantDequeueEmptyQueue if the queue was empty" in {

          val array: Array[Int] = Array()
          val queue: Queue[Int] = populateViaEnqueue(array, 5)
          
          queue.dequeue should be (Right(CantDequeueEmptyQueue()))

  }

  "Dequeue" should "return CantDequeueEmptyQueue if the queue size was less then zero" in {

          val array: Array[Int] = Array()
          val queue: Queue[Int] = populateViaEnqueue(array, -5)
          
          queue.dequeue should be (Right(CantDequeueEmptyQueue()))

  }

  "Dequeue" should "contain all elements from the array, if the queue size is more then available." in {

          val array: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7)
          val queue: Queue[Int] = populateViaEnqueue(array, 999999999)
          
          queue.dequeue should be (Left(array(0)))
          queue.dequeue should be (Left(array(1)))
          queue.dequeue should be (Left(array(2)))
          queue.dequeue should be (Left(array(3)))
          queue.dequeue should be (Left(array(4)))
          queue.dequeue should be (Left(array(5)))
          queue.dequeue should be (Left(array(6)))

  }

  "ToString" should "return internal array" in {

          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaEnqueue(array, 5)
          
          queue.toString should be ("Array(1, 2, 3, 4, 5)")

  }

  "ForEach" should "iterate over all elements" in {

          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaEnqueue(array, 5)
          var i = 0
          queue.foreach{element => element should be (array(i)); i += 1}  

  }


}