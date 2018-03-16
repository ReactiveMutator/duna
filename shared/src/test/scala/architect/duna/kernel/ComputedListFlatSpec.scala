
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.kernel.ComputedList
import scala.collection.mutable.HashMap
import scala.util.{Try, Success, Failure}

class ComputedListFlatSpec extends FlatSpec with Matchers{

  "The ComputedList" should "add computed value." in {
  
    val computedList: ComputedList[Option, Int] = ComputedList()
    val value: Option[Int] = Some(3)
    computedList.add(value) should be (HashMap(value.hashCode -> value))

  }

  "The ComputedList" should "add hash and value." in {
  
    val computedList: ComputedList[Option, Int] = ComputedList()
    val value: Option[Int] = Some(3)
    
    computedList.add(value) should be (HashMap(value.hashCode -> value))
    computedList.add(value) should be (HashMap(value.hashCode -> value))

  }

  "The ComputedList" should "signal to every element of the list." in {
  
    val computedList: ComputedList[Option, Int] = ComputedList()
    val value: Option[Int] = Some(3)
    computedList.add(value)
    computedList.signal(value => Try(value.get)) should be (Seq(Try(value.get)))

  }
}