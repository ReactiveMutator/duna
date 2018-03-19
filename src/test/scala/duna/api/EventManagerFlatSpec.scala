
package duna

import org.scalatest._
import prop._
import duna.api.EventManager
import duna.kernel.Value
import scala.util.{Try, Success, Failure}

class EventManagerFlatSpec extends FlatSpec with Matchers{

  "EventManager receives 4 values and" should "transmit them into an array of 5." in {
    
    val eventManager = EventManager[Int, Int](5)

    eventManager.emit(Value(0,  5))
    eventManager.emit(Value(1,  6))
    eventManager.emit(Value(2,  7))
    eventManager.emit(Value(3,  8))


    eventManager.toArray(0).index should be (0)
    eventManager.toArray(1).index should be (1)
    eventManager.toArray(2).index should be (2)
    eventManager.toArray(3).index should be (3)

  }

  "EventManager receives 4 values and" should "transmit them into an array of 3." in {

    val eventManager = EventManager[Int, Int](3)

    eventManager.emit(Value(0,  5))
    eventManager.emit(Value(1,  6))
    eventManager.emit(Value(2,  7))
    eventManager.emit(Value(3,  8))


    eventManager.toArray(0).index should be (0)
    eventManager.toArray(1).index should be (1)
    eventManager.toArray(2).index should be (2)

  }

  "EventManager consume" should "return the first emitted element." in {
    
    val eventManager = EventManager[Int, Int](5)


    eventManager.emit(Value(0,  5))
    eventManager.emit(Value(1,  6))
    eventManager.emit(Value(2,  7))
    eventManager.emit(Value(3,  8))


    eventManager.consume match{
      case Left(event) => event.index should be (0)
      case Right(error) => fail()

    }

  }

  "EventManager consume" should "return the third emitted element." in {
    
    val eventManager = EventManager[Int, Int](3)


    eventManager.emit(Value(0,  5))
    eventManager.emit(Value(1,  6))
    eventManager.emit(Value(2,  7))
    eventManager.emit(Value(3,  8))


    eventManager.consume match{
      case Left(event) => event.index should be (0)
      case Right(error) => fail()

    }

  }

  
  "ToString" should "return internal array" in {

    val eventManager = EventManager[Int, Int](3)

    eventManager.emit(Value(0, 5))
    eventManager.emit(Value(1, 6))
    eventManager.emit(Value(2, 7))
      
    eventManager.toString should be ( "Array(Value(0,5), Value(1,6), Value(2,7))")

  }

  "IsEmpty" should "return true if nothing was emitted" in {

    val eventManager = EventManager[Int, Int](3)
 
    eventManager.isEmpty should be (true)

  }

  "Process" should "return tree failures" in {

    val eventManager = EventManager[Int, Int](3)

    eventManager.emit(Value(0, 5))
    eventManager.emit(Value(1, 6))
    eventManager.emit(Value(2, 7))

    val result = eventManager.process(() => eventManager.consume){(time, value) => 

      Seq(Try{value /0}).filter(_.isFailure).asInstanceOf[Seq[Failure[Any]]]

    }
    result().size should be (3)
  }


}