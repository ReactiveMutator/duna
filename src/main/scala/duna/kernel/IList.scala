package architect
package duna
package kernel

trait IList[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]{self =>

  def ++(value: A): List[A] = {
    List(value, self)
  }

  def filter(f: A => Boolean): IList[A] = {

    def rec(list: IList[A])(f: A => Boolean)(cont: IList[A] => IList[A]): IList[A] = {
      list match {
        case List(head, tail) => rec(tail)(f)(next => if (f(head))(next ++ head)else(next))
        case Empty() => cont(Empty())
      }

    }
    rec(self)(f)(identity)
  }

  def map(f: A => A): IList[A] = {

    def rec(list: IList[A])(f: A => A)(cont: IList[A] => IList[A]): IList[A] = {
      list match {
        case List(head, tail) => rec(tail)(f)(next => next ++ f(head))
        case Empty() => cont(Empty())
      }

    }
    rec(self)(f)(identity)
  }

}
case class Empty[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A]() extends IList[A]
case class List[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](head: A, tail: IList[A]) extends IList[A]
