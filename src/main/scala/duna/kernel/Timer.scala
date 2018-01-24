package architect
package duna
package kernel

case class Timer(){

  def elapsedTime[R](block: => R): Long = {  
      val t0 = System.nanoTime()
      val result = block    
      val t1 = System.nanoTime()

       t1 - t0
      
  }

}

