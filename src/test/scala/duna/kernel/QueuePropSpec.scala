

package duna

import org.scalatest._
import prop._
import Utils._
import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }

class QueuePropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers{

  property("The queue should contain last array.length elements from the array.") {
    forAll("array", "size") { (array: Array[Int], size: Int) =>

      val queue: Queue[Int] = populateViaEnqueue(array, size)

      for(i <- 0 to array.length - 1){

        queue.dequeue should be (Left(array(i)))

      }

    
    }

  }
}