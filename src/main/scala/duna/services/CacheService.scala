package architect
package duna
package services 


/**
  Is a wrapper for a data structure, which contains Var history. Its values are of type Event[A], because
  we need to know time of a value occurence. Can't be empty.
*/

case class CacheService[Index, A](){self =>

  @volatile private var availableValues: IValue[Index, A] = NoValue()

  def get: Option[A] = {

    availableValues match{
      case Value(processedTime, value) => Some(value)
      case NoValue() => None
    }
    
  }

  def update(index: Index, value: A): Boolean = {

    val newValue = Value(index, value)

    availableValues = newValue

    true
  }

}

trait IValue[Index, A] 
case class Value[Index, A](index: Index, value: A) extends IValue[Index, A]
case class NoValue[Index, A]() extends IValue[Index, A]