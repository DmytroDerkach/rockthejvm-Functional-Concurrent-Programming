package lectures.concurrency

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

object Futures {

  def calculate(): Int = {
    // simulate long compute
    Thread.sleep(1000)
    12
  }

  // threadpool java-specific
  val executor = Executors.newFixedThreadPool(4)
  // thread poll (scala specific)
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executor)

  // a Future is async computation that will finish at some point
  val aFuture: Future[Int] = Future(calculate())

  private val futureInstantResult: Option[Try[Int]] = aFuture.value

  def demoPromises() = {
    /*
  Promises
   */
    val promise = Promise[Int]()
    val futureInside: Future[Int] = promise.future

    //thread 1 - "consumer": monitor thread for completion
    futureInside.onComplete {
      case Success(value) => println(s"I've just completed with value $value")
      case Failure(exception) => println(s"failed with ${exception}")
    }

    // thread 2 - "producer"
    val producerThread = new Thread(() => {
      println("crunching numbers ...")
      Thread.sleep(1000)

      promise.success(42)
      println(s"I'm done")
    })

    producerThread.start()
  }


  def main(args: Array[String]): Unit = {
//    println(futureInstantResult) // inspec a value of the future
    demoPromises()
    Thread.sleep(2000)
    executor.shutdown()
  }

}
