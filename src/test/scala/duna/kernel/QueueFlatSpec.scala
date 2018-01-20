
package architect
package duna

import org.scalatest.FlatSpec
import Utils._
import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }

class QueueFlatSpec extends FlatSpec {

  "The queue" should "contain all elements from the array, if it's size is more then the array's length." in {
          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaEnqueue(array, 9)
          
          assert((queue.toArray: Seq[Int]) == (Array(1, 2, 3, 4, 5, 0, 0, 0, 0): Seq[Int]))
          assert(queue.dequeue == Left(1))

  }

  "The queue" should "contain array.length last elements from the array, if it's size is less then array's length." in {
    
          val array: Array[Int] = Array(1, 2, 3, 4, 5)
          val queue: Queue[Int] = populateViaEnqueue(array, 3)
          
          assert((queue.toArray: Seq[Int]) == (Array(4, 5, 3): Seq[Int]))
          assert(queue.dequeue == Left(3))


  }

  "The queue" should "be empty after dequeue of all elements" in {

          val array: Array[Int] = Array(1, 2, 3)
          val queue: Queue[Int] = populateViaEnqueue(array, 5)
          
          assert(queue.dequeue == Left(array(0)))
          assert(queue.dequeue == Left(array(1)))
          assert(queue.dequeue == Left(array(2)))
          assert(queue.isEmpty == true)


  }

}