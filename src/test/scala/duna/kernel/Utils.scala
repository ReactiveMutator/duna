package architect
package duna

import duna.kernel.{Queue, QueueIssue, CantDequeueEmptyQueue }
import scala.util.{ Either, Left, Right }


object Utils{
  
  def calculateActualSize(size: Int): Int = {
    
    val availableSize = {
      val runtime = Runtime.getRuntime()

        (runtime.freeMemory/4/32).toInt // 32 bits is Int size, 4 - memory share
    }

    if(size < 1 ){
      100000
      
    }else if(availableSize < size){
      availableSize 
    }else{
      size
    }
  }

  def populateViaEnqueue(array: Array[Int], size: Int): Queue[Int] = {

    val actualSize = calculateActualSize(size)

    val queue: Queue[Int] = Queue(actualSize)
    array.foreach{i => queue.enqueue(i)}
    queue
  }


}