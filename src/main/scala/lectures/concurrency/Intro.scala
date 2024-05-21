package lectures.concurrency

import java.util.concurrent.Executors

object Intro extends App {
  // JVM threads

  val aThread = new Thread(() => println("in the thread"))

  val runnable = new Runnable {
    override def run(): Unit = println("running in parallel")
  }
  val aThread2 = new Thread(runnable)

  aThread.start()
  aThread2.start()

  //---------------

  val threadHello = new Thread(() => (1 to 5).foreach(a => println(s"Hello $a")))
  val threadBye = new Thread(() => (1 to 5).foreach(a => println(s"Bye $a")))

  threadHello.start()
//  threadHello.join()
  threadBye.start()

  /**
   executors
   */
    val pool = Executors.newFixedThreadPool(10)
    pool.execute(() => {
      Thread.sleep(1000)
      println("done after 1 sec")
    })

  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 sec")
  })


  pool.shutdown()
}
