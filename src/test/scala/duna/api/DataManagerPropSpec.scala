

package duna

import org.scalatest._
import prop._
import Utils._
import duna.api.{DataManager }

class DataManagerPropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers{

  property("DataManager should write and read the same element.") {
    forAll("index", "value") { (index: Int, value: Int) =>

      val dataManager: DataManager[Int, Int] = DataManager(0, 0)
      
      dataManager.write(index, value)
      dataManager.read should be (value)
    
    }

  }

}