package architect
package duna

import org.scalatest._
import prop._
import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }
import duna.eventSourcing.{ Event, EventManager}
import scala.util.{ Either, Left, Right }
import duna.kernel.Computation

class EventManagerFlatSpec extends FlatSpec with Matchers{

  "EventManager receives 4 values and" should "transmit them into an array of 5." in {
    
    val eventManager = EventManager[Int, Int](5)

    eventManager.emit(Event(0,  5))
    eventManager.emit(Event(1,  6))
    eventManager.emit(Event(2,  7))
    eventManager.emit(Event(3,  8))


    eventManager.toArray(0).index should be (0)
    eventManager.toArray(1).index should be (1)
    eventManager.toArray(2).index should be (2)
    eventManager.toArray(3).index should be (3)

  }

  "EventManager receives 4 values and" should "transmit them into an array of 3." in {

    val eventManager = EventManager[Int, Int](3)

    eventManager.emit(Event(0,  5))
    eventManager.emit(Event(1,  6))
    eventManager.emit(Event(2,  7))
    eventManager.emit(Event(3,  8))


    eventManager.toArray(0).index should be (0)
    eventManager.toArray(1).index should be (1)
    eventManager.toArray(2).index should be (2)

  }

  "EventManager consume" should "return the first emitted element." in {
    
    val eventManager = EventManager[Int, Int](5)

    eventManager.emit(Event(0,  5))
    eventManager.emit(Event(1,  6))
    eventManager.emit(Event(2,  7))
    eventManager.emit(Event(3,  8))


    eventManager.consume match{
      case Left(event) => event.index should be (0)
      case Right(error) => fail()

    }

  }

  "EventManager consume" should "return the third emitted element." in {
    
    val eventManager = EventManager[Int, Int](3)

    eventManager.emit(Event(0,  5))
    eventManager.emit(Event(1,  6))
    eventManager.emit(Event(2,  7))
    eventManager.emit(Event(3,  8))


    eventManager.consume match{
      case Left(event) => event.index should be (0)
      case Right(error) => fail()

    }

  }

  
  "ToString" should "return internal array" in {

    val eventManager = EventManager[Int, Int](3)

    eventManager.emit(Event(0, 5))
    eventManager.emit(Event(1, 6))
    eventManager.emit(Event(2, 7))
      
    eventManager.toString should be ( "Array(Event(0,5), Event(1,6), Event(2,7))")

  }

  "IsEmpty" should "return true if nothing was emitted" in {

    val eventManager = EventManager[Int, Int](3)
 
    eventManager.isEmpty should be (true)

  }


}