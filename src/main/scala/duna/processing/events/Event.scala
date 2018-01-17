package architect
package duna
package processing 
package events

import duna.processing.time.Time

trait IEvent[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]
case class NoEvent[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]() extends IEvent[A]
case class Event[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](time: Time, computation: () => A) extends IEvent[A]