package architect
package duna
package kernel

import scala.util.{Try, Success, Failure}

sealed trait Events[A]
case class Write[A](blob: () => A) extends Events[A]
case class Read[A](callback: A => Unit) extends Events[A]
