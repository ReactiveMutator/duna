
package architect
package duna

import org.scalatest._
import prop._
import Utils._
import duna.db.{DataManager }

class DataManagerPropSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers{

  property("DataManager should write and read same element.") {
    forAll("index", "value") { (index: Int, value: Int) =>

      val dataManager: DataManager[Int, Int] = DataManager()
      
      dataManager.write(index, value)
      dataManager.read should be (Some(value))
    
    }

  }
}