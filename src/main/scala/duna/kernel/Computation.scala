package architect
package duna
package kernel

case class Computation[A](blob: () => A){

  def exec: A = {
    blob()
  }

}