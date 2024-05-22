package lectures.concurrency

import scala.collection.mutable
import scala.util.Random

object JVMThreadCommunication {
  def main(args: Array[String]): Unit = {
    //    ProdConsV1.start()
    //    ProdConsV2.start()
    //    ProdConsV3.start(2)
    ProdConsV4.start(3, 1, 4)
  }
}


//example:  producer-consumer problem
class SimpleContainer {
  private var value: Int = 0

  def isEmpty: Boolean = value == 0

  def set(newValue: Int): Unit = value = newValue

  def get(): Int = {
    val result = value
    value = 0
    result
  }
}

// producer part 1: one producer & one consumer (busy wait)

object ProdConsV1 {
  def start(): Unit = {
    val container = new SimpleContainer

    // check whether the container is empty
    val consumer = new Thread(() => {
      println(s"[consumer] reading")
      // busy waiting
      while (container.isEmpty) {
        println("[consumer] waiting for a value")
      }

      println(s"[consumer] I have consumed a value: ${container.get()}")
    })

    val producer = new Thread(() => {
      println(s"[producer] computing")
      Thread.sleep(200)
      val value = 42
      println(s"[producer] I'm producing after LONG time value $value")
      container.set(value)
    })

    consumer.start()
    producer.start()
  }
}

// wait and notify
object ProdConsV2 {
  def start(): Unit = {
    val container = new SimpleContainer

    // check whether the container is empty
    val consumer = new Thread(() => {
      println(s"[consumer] reading")

      container.synchronized { // will block other thread trying to "lock" this object
        // thread safe code
        if (container.isEmpty)
          container.wait() // release the lock + suspend the thread
        // reacquire the lock
        // continue execution
        println(s"[consumer] I have consumed a value: ${container.get()}")
      }

    })

    val producer = new Thread(() => {
      println(s"[producer] computing")
      Thread.sleep(200)
      val value = 42

      container.synchronized {
        println(s"[producer] I'm producing after LONG time value $value")
        container.set(value)
        container.notify() // awaken one suspended thread on this object
      } // release the lock
    })

    consumer.start()
    producer.start()

  }
}

// inset a large container
// producer --> [ _ _ _ ] --> consumer
object ProdConsV3 {
  def start(containerCapacity: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]()

    val consumer = new Thread(() => {
      val random = new Random(System.nanoTime())

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println(s"[consumer] buffer is empty, waiting ...")
            buffer.wait()
          }

          // buffer must not be empty
          val x = buffer.dequeue()
          println(s"[consumer] I've just consumed $x")

          // it kinda means:  producer, give me more elements
          buffer.notify() // wake up the producer (if it is asleep)
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random(System.nanoTime())

      var counter = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == containerCapacity) {
            println(s"[producer] buffer is full, waiting...")
            buffer.wait() // waiting until consumer consumes some value
          }
          //buffer is not empty
          val newElement = counter
          counter += 1
          println(s"[producer] I'm producing new element $newElement")
          buffer.enqueue(newElement)

          // it kinda means: consumer don't be lazy
          buffer.notify() // wake up the consumer (if it is asleep)
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }
}

// multiple consumers & producers
// producer1 --> [ _ _ _ ] --> consumer1
// producer2 ----^         --> consumer2
object ProdConsV4 {
  private class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random(System.nanoTime())
      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer is empty, waiting")
            buffer.wait()
          }
          //buffer is non- empty
          val newValue = buffer.dequeue()
          println(s"[consumer $id] consumed $newValue")

          // notify a producer

          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  private class Producers(id: Int, buffer: mutable.Queue[Int], containerCapacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random(System.nanoTime())
      var currentCount = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == containerCapacity) {
            // buffer is full, we need to lock
            println(s"[producer $id] buffer is full, waiting..")
            buffer.wait()
          }

          // there is space int he buffer
          println(s"[produces $id] producing $currentCount")
          buffer.enqueue(currentCount)
          // awake consumer
          buffer.notifyAll()

          currentCount += 1

        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def start(nProducers: Int, nConsumers: Int, containerCapacity: Int) = {
    // start producers & consumers
    val buffer = new mutable.Queue[Int]()

    val producers = (1 to nProducers).map(id =>
      new Producers(id, buffer, containerCapacity)
    )

    val consumers = (1 to nConsumers).map(id =>
      new Consumer(id, buffer)
    )

    producers.foreach(_.start())
    consumers.foreach(_.start())
  }
}
