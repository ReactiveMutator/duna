
# Kernel (duna/kernel)

Kernel contains the highest level items. They don't depend on any item from Duna. 

### Callback

The class was introduced to abstract away effectful callbacks.

```tut
    import architect.duna.kernel.Callback 
    
    val value = 4
    
    val callback = Callback[Int](a => a + 2) // Create callback
    
    callback.run(value) // Execute callback
```



### ComputedList

It is used in Var and Rx to store a list of Rx, which contains these Vars and Rxs. 

How it works:
```tut
    import architect.duna.api.{ Var, Rx, StateManager } 
    
    implicit val stateManager = StateManager()

    val a = Var(1)
    val b = Var(2)

    Rx[Int]{implicit rx => a() + b()} 
    

```
Both a and b have a computedList of type ComputedList[Rx, Int], which store a link to the Rx.

Use case:
```tut
    import architect.duna.kernel.ComputedList 
    import scala.util.Try

    val computedList: ComputedList[Option, Int] = ComputedList() // Empty ComputedList
    
    val value: Option[Int] = Some(3)

    computedList.add(value) // Add value to ComputedList

    computedList.signal(value => Try(value.get)) // Execute callback
```

### Events

Write is used to abstract away a callback, which change the Var's value.
Read is used in case we need to read the Var's value. 

```tut
    import architect.duna.kernel.{ Events, Write, Read }

    val write = Write[Int](() => 4) // Create write callback

    val read = Read[Int](a => a + 4) // Create read callback

    read.callback(write.blob()) // execute callbacks
```

### ProcessingTime

ProcessingTime is for storing results of Reactive's process function. It contains processing time and the result.

### Queue

Every EventManager contains a queue. The queue is a buffer, where all Var's and Rx's events are stored. The queue is non-blocking by default, but to manage backpressure, there exists tmpStore of type ConcurrentLinkedQueue[A]. So, in case the queue size is not enough, tmpStore will keep new events. 

The size is set equal to 100000 if you pass a number less then 1, and is set equal to (avaliable size)/4 if the passed number exceeds avaliable size. 

```tut
    import architect.duna.kernel.Queue 

    val array: Array[Int] = Array(1, 2, 3, 4, 5)
    val queue: Queue[Int] = Queue(5) // Create a queue of size 5

    array.foreach{i => queue.enqueue(i)} // Populate the queue with numbers from array

    queue.dequeue // Return the first number from the queue

```

### Task

Task is a wrapper over a java.util.concurrent.Future. It checks if the Future is not null. 

```tut
    import architect.duna.kernel.Task 
    import architect.duna.processing.Executor 

    val executor: Executor = Executor() // Create an executer
          
    val job = () => 6
    val future = executor.submit(job) // Return a future
    val task = Task(future) // Wrap the future

    task.get // Blocking operation, which returns the result of future

    executor.close() // Close the executor
```

### Timer

The Timer is used in Reactive to measure elapsed time of an operation. It measures the System.nanoTime().

```tut

    import architect.duna.kernel.Timer 

    Timer().elapsedTime{ 4 + 6 }

```
### Value

The Value is used by DataManager to store the current value.