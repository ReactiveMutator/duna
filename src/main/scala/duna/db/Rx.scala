package architect
package duna
package db

import java.util.concurrent.ConcurrentHashMap
/**
class Rx[A](calculation: Rx[A] => A){ self =>

  def now: A = {

    calculation(self)
  }

}
object Rx{

  def apply[A](calculation: implicit Rx[A] => A): Rx[A] = new Rx(calculation)

}*/