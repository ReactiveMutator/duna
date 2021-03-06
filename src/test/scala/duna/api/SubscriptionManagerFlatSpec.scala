
package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{ SubscriptionManager }
import duna.kernel.Callback

class SubscriptionManagerFlatSpec extends FlatSpec with Matchers{

  "SubscriptionManager" should "run all its triggers." in {
    
      val subscriptionManager: SubscriptionManager[Int] = SubscriptionManager()
      var count1: Int = 0
      var count2: Int = 0
      val callback1 = (a: Int) => {count1 = a + 3 }
      val callback2 = (a: Int) => {count2 = a + 4 }

      subscriptionManager.trigger(Callback(callback1)) 
      subscriptionManager.trigger(Callback(callback2))
      subscriptionManager.run(1)   
      count1 should be (4)
      count2 should be (5)

  }

  "SubscriptionManager" should "remove trigger." in {
    
      val subscriptionManager: SubscriptionManager[Int] = SubscriptionManager()
      var count1: Int = 0
      var count2: Int = 0
      val callback1 = (a: Int) => {count1 = a + 3 }
      val callback2 = (a: Int) => {count2 = a + 4 }

      val obs = subscriptionManager.trigger(Callback(callback1)) 
      subscriptionManager.trigger(Callback(callback2))
      subscriptionManager.remove(obs)
      subscriptionManager.run(1)   
      count1 should be (0)
      count2 should be (5)

  }

  "Trigger" should "delete itself from SubscriptionManager." in {
    
      val subscriptionManager: SubscriptionManager[Int] = SubscriptionManager()
      var count1: Int = 0
      var count2: Int = 0
      val callback1 = (a: Int) => {count1 = a + 3 }
      val callback2 = (a: Int) => {count2 = a + 4 }

      val obs = subscriptionManager.trigger(Callback(callback1)) 
      subscriptionManager.trigger(Callback(callback2))
      obs.delete
      subscriptionManager.run(1)   
      count1 should be (0)
      count2 should be (5)

  }

  "SubscriptionManager hasTriggers" should "return true." in {
    
      val subscriptionManager: SubscriptionManager[Int] = SubscriptionManager()
      var count1: Int = 0
      var count2: Int = 0
      val callback1 = (a: Int) => {count1 = a + 3 }
      val callback2 = (a: Int) => {count2 = a + 4 }

      subscriptionManager.trigger(Callback(callback1)) 
      subscriptionManager.trigger(Callback(callback2))
      subscriptionManager.hasTriggers should be (true)

  }

  "SubscriptionManager" should "delete all." in {
    
      val subscriptionManager: SubscriptionManager[Int] = SubscriptionManager()
      var count1: Int = 0
      var count2: Int = 0
      val callback1 = (a: Int) => {count1 = a + 3 }
      val callback2 = (a: Int) => {count2 = a + 4 }

      subscriptionManager.trigger(Callback(callback1)) 
      subscriptionManager.trigger(Callback(callback2))
      subscriptionManager.deleteAll 
      subscriptionManager.hasTriggers should be (false)

  }

}