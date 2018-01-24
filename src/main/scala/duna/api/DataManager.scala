package architect
package duna
package api


class DataManager[Index, A](index: Index, value: A){self =>

  @volatile private var availableValues: Value[Index, A] = Value(index, value)

  def read: A = {

    availableValues.value
    
  }

  def write(index: Index, value: A): Boolean = {

    val newValue = Value(index, value)

    availableValues = newValue

    true
  }

}

object DataManager{

  def apply[Index, A](index: Index, value: A) = new DataManager[Index, A](index, value){}

}

case class Value[Index, A](index: Index, value: A) 
