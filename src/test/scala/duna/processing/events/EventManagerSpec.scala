package architect
package duna

import org.scalatest._
import prop._
import duna.kernel.{Queue, QueueIssue, CantEnqueueFullQueue, CantDequeueEmptyQueue, CantDequeueElementDoesntExist }
import duna.processing.events.{ Event, EventManager}
import scala.util.{ Either, Left, Right }

class EventManagerSpec extends PropSpec with Matchers with GeneratorDrivenPropertyChecks with Checkers{

              
  property("EventManager put method must return event containing value or CantDequeueEmptyQueue.") {
    forAll("queue", "size") { (value: Int, size: Int) =>{

          val eventManager = EventManager[Int](size)
          

          eventManager.put(value) match{
            case Left(data) => data.computation() should be (value)
            case Right(data) => data should be (CantDequeueEmptyQueue())
          }

      }
    }
  }

  property("EventManager receives 4 values and transmits them into an array.") {
    forAll("size") { (size: Int) =>{

          val eventManager = EventManager[Int](5)
          val array: Array[Int] = new Array[Int](5)

          eventManager.put(5)
          eventManager.put(6)
          eventManager.put(7)
          eventManager.put(8)


          var i = 0
          eventManager.next(a => { array(i) = a; i += 1})


          array should be (Array(5, 6, 7, 8, 0))

      }
    }
  }

}