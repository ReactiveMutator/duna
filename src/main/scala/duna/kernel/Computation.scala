
// # Computation
// ### Wrapper of a lazy function

package duna
package kernel


// Just a wrapper of a lazy function, which returns a value. Use it when you don't want to execute the function right now.
// Probably it is not the best name, so you can offer something more appropriate.
case class Computation[A](blob: () => A){

  def exec: A = {
    blob()
  }

}