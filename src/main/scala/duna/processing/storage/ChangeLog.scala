package architect
package duna
package processing
package storage

import duna.processing.time.Time

/**
  Is a wrapper for a data structure, which contains Var history. Its values are of type Event[A], because
  we need to know time of a value occurence. Can't be empty.
*/

case class ChangeLog[A](){self =>

  @volatile private var availableValues: IValue[A] = NoValue()

  def get: Option[A] = {

    availableValues match{
      case Value(processedTime, value) => Some(value)
      case NoValue() => None
    }
    
  }

  def update(value: A): Boolean = {

    val newValue = Value(Time(), value)

    availableValues = newValue

    true
  }

}

trait IValue[A] 
case class Value[A](processedTime: Time, value: A) extends IValue[A]
case class NoValue[A]() extends IValue[A]