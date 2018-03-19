
package duna
package processing

import java.util.concurrent.{Callable, CompletableFuture}

case class Worker[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](job: () => A) extends Callable[A]{self =>

  def call(): A = {
      job()

  }

}
