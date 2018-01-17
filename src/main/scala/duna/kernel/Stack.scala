package architect
package duna
package kernel

case class Stack[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](){

  private val tasks: IList[A] = Empty()
  
  def isEmpty: Boolean = {

    tasks match{
      case Empty() => true 
      case List(head, tail) => false
    }

  }

  def push(value: A) = {

    tasks ++ value

  }

  def pop: A = {

    tasks match{

      case Empty() => throw new NoSuchElementException()
      case List(head, tail) => head
    }

  }

}