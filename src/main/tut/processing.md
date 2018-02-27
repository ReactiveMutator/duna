
# Processing (duna/processing)

### Executor

The Executor is a wrapper around the java ExecutorService, which contains a link to the current fixed thread pool. It is used by the StateManager for tasks execution.

```tut
    val executor: Executor = Executor() // Create an executor

    executor.close() // Shut down the executor

    executor.isShutdown // Returns true
```

### Worker

It extends a Callable and thus is used by an Executor to submit tasks to a thread pool.