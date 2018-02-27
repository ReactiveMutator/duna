package architect
package duna
package eventSourcing


trait IEvent[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]
case class NoEvent[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]() extends IEvent[A]
case class Event[Index, @specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](index: Index, computation: A) extends IEvent[A]