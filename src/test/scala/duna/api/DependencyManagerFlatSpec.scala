
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.DependencyManager
import scala.collection.immutable.Map
import scala.util.{Try, Success, Failure}

class DependencyManagerFlatSpec extends FlatSpec with Matchers{

  "DependencyManager" should "read dependency." in {
    
      val dependencyManager: DependencyManager[Int, Int] = DependencyManager()

      dependencyManager.put(1, 7) 
      dependencyManager.put(1, 8) 
      dependencyManager.put(2, 1)
      dependencyManager.put(1, 9)
      dependencyManager.put(2, 3)
      dependencyManager.put(1, 2)  
      dependencyManager.put(2, 4) 

      dependencyManager.read(1) should be (Success(7))
      dependencyManager.read(2) should be (Success(1))
      dependencyManager.read(1) should be (Success(7))
      dependencyManager.read(1) should be (Success(7))
      dependencyManager.get(1) should be (Success(7))
      dependencyManager.read(1) should be (Success(8))
      dependencyManager.get(2) should be (Success(1))
      dependencyManager.read(2) should be (Success(3))
  }

  "DependencyManager" should "save dependency to buffer." in {
    
      val dependencyManager: DependencyManager[Int, Int] = DependencyManager()

      dependencyManager.put(1, 7) 
      dependencyManager.put(1, 8) 
      dependencyManager.put(1, 9)
      dependencyManager.put(1, 2)  
      dependencyManager.put(2, 1)
      dependencyManager.put(2, 3)  

      dependencyManager.read(1) should be (Success(7))
  }

  "DependencyManager" should "update dependency from buffer." in {
    
      val dependencyManager: DependencyManager[Int, Int] = DependencyManager()

      dependencyManager.put(1, 7) 
      dependencyManager.put(1, 8) 
      dependencyManager.put(2, 1)
      dependencyManager.put(1, 9)
      dependencyManager.put(2, 3)
      dependencyManager.put(1, 2)  
      dependencyManager.put(2, 4) 

      dependencyManager.get(1) should be (Success(7))
      dependencyManager.get(2) should be (Success(1))
      dependencyManager.get(1) should be (Success(8))
      dependencyManager.get(1) should be (Success(9))
      dependencyManager.get(1) should be (Success(2))
      dependencyManager.get(2) should be (Success(3))
      dependencyManager.get(2) should be (Success(4))
  }


}