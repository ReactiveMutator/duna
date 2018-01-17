package architect
package duna
package processing

import java.util.concurrent.{Callable, CompletableFuture}

case class Worker[@specialized(Short, Char, Int, Float, Long, Double, AnyRef) A](job: () => A) extends Callable[A]{self =>

  //val future: CompletableFuture[A] = new CompletableFuture()

  def call(): A = {
      job()
    //future.complete(job())
    //future.get
  }

}
