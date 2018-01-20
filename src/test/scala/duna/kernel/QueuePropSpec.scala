
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }
/**
class QueuePropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers{

  property("The queue should contain last array.length elements from the array.") {
    forAll("array", "size") { (array: Array[Int], size: Int) =>

      val queue: Queue[Int] = populateViaEnqueue(array, size)
      
      val res = if(array.length > 0){
        if(size > 1){
          Left(array(size))
        }else{
          Left(array(0))
        }
      }else{

          Right(CantDequeueEmptyQueue())
        
      }
      queue.dequeue should be (res)
    
    }

  }
}*/