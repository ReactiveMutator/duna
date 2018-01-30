package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.DependencyManager
import scala.util.{Try, Success, Failure}
import org.scalatest.TryValues._

class DependencyManagerPropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers{

  property("DependencyManager should write and read the same element.") {
    forAll("index", "value") { (index: Int, value: Int) =>

      val dependencyManager: DependencyManager[Int, Int] = DependencyManager()
      
      dependencyManager.put(index, value)
      dependencyManager.read(index).success.value should be (value)
      dependencyManager.get(index).success.value should be (value)
    
    }

  }

  property("DependencyManager can't read an empty element.") {
    forAll("index", "value") { (index: Int, value: Int) =>

      val dependencyManager: DependencyManager[Int, Int] = DependencyManager()
      
      dependencyManager.read(index).failure.exception.getMessage should be ("Didn't find the hash = " + index)

    
    }

  }

  property("DependencyManager can't get an empty element.") {
    forAll("index", "value") { (index: Int, value: Int) =>

      val dependencyManager: DependencyManager[Int, Int] = DependencyManager()
      
      dependencyManager.get(index).failure.exception.getMessage should be ("Didn't find the hash = " + index)

    
    }

  }

}