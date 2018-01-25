package architect
package duna
package api

import duna.kernel.Value
import scala.util.{Try, Success, Failure}

class DataManager[Index, A](index: Index, value: A){self =>

  @volatile private var availableValues: Value[Index, A] = Value(index, value)

  def read: A = {

    availableValues.value
    
  }

  def write(index: Index, value: => A): Try[A] = {

    val calc = Try{value}
    calc match {

      case Success(value) => {
        val newValue = Value(index, value)

        availableValues = newValue
      }
      case Failure(e) => e
    }
    

    calc
  }

}

object DataManager{

  def apply[Index, A](index: Index, value: A) = new DataManager[Index, A](index, value){}

}


