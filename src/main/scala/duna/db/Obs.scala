package architect
package duna
package db


import duna.kernel.{ Index, Callback }

case class Obs[A](callback: Callback[A], index: Index[Int], link: SubscriptionManager[A]){

  def delete: Boolean = {

    link.remove(index)
    true
  }   

  def run(value: A): Boolean = {

    callback.run(value)
    true
  } 
                                                                                            

}

