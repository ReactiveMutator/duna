package architect
package duna
package kernel

case class Callback[A](run: A => Unit)