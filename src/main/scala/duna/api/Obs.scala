
package duna
package api


import duna.kernel.Callback
import scala.util.{Try, Success, Failure}

case class Obs[A](callback: Callback[A], link: SubscriptionManager[A]){self =>

  def delete: Boolean = {

    link.remove(self)
    true
  }   

  def run(value: A): Try[A] = {

    Try(callback.run(value)) match{
      case Success(smth) => Success(value)
      case Failure(error) => Failure(error)
    }
    
  } 


}

