package lectures.concurrency

object JVMConcurrencyProblems {

  /**
   * Part 1
   */
  def runInParallel() = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    Thread.sleep(1000L)
    println(x)
  }

  /**
   * Part 2
   *
   * @param amount
   */
  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int) = {
    bankAccount.amount -= price
  }

  def buySafe(bankAccount: BankAccount, thing: String, price: Int) = {
    bankAccount.synchronized { // does not allow multiple thread to run critical section at the same time
      bankAccount.amount -= price // critical section
    }
  }

  def demoBankingProblem() = {
    (1 to 10000).foreach { _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => {
        buySafe(account, "shoes", 3000)
      })

      val thread2 = new Thread(() => {
        buySafe(account, "IPhone", 4000)
      })

      thread1.start()
      thread2.start()

      thread1.join()
      thread2.join()

      if (account.amount != 43000) println(s" != 43K [${account.amount}]")
    }
  }


  /**
   * Part 3 : Exercise 1
   */
  def exercise_inceptionThread(maxThreads: Int, i: Int = 1): Thread =
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = exercise_inceptionThread(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })


  def main(args: Array[String]): Unit = {
    //    demoBankingProblem()

    exercise_inceptionThread(50).start()
  }
}
