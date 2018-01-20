package architect
package duna
package processing

import java.util.concurrent.{Future, Executors, ExecutorService} 

case class Executor(poolSize: Int = Runtime.getRuntime().availableProcessors()){ self =>

  private val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

  def isShutdown = 
    pool.isShutdown

  def submit[A](function: () => A):  Future[A] = {
      
    val worker = Worker[A](function)

    pool.submit(worker)
      
 
  }

  def close(): Boolean = {
    if(!pool.isShutdown) {

     pool.shutdown

     true
    }else{
     false
    }
  }


}
