
// # Callback
// ### Wrapper of a function with effects
package duna
package kernel

// Sometimes we need wrappers to emphasize importance of a type. 
// Here is a class, which is used in methods with effects. That is why it returns Unit.
// Use it when you need to exec a function, which does not return any meaningful value. 
// Otherwise you may use Rx.

case class Callback[A](run: A => Unit)