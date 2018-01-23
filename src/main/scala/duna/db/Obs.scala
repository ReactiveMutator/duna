package architect
package duna
package db


import duna.kernel.Callback

case class Obs[A](callback: Callback[A], link: SubscriptionManager[A]){self =>

  def delete: Boolean = {

    link.remove(self)
    true
  }   

  def run(value: A): Boolean = {

    callback.run(value)
    true
  } 
                                                                                            

}

