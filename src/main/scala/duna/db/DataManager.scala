package architect
package duna
package db


case class DataManager[Index, A](){self =>

  @volatile private var availableValues: IValue[Index, A] = NoValue()

  def read: Option[A] = {

    availableValues match{
      case Value(processedTime, value) => Some(value)
      case NoValue() => None
    }
    
  }

  def write(index: Index, value: A): Boolean = {

    val newValue = Value(index, value)

    availableValues = newValue
    println("write " + value)
    true
  }

}

trait IValue[Index, A] 
case class Value[Index, A](index: Index, value: A) extends IValue[Index, A]
case class NoValue[Index, A]() extends IValue[Index, A]