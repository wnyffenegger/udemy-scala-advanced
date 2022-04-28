package lectures.section3

object JVMConcurrencyProblems extends App {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()

    Thread.sleep(100)
    println(x)
  }

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.amount = bankAccount.amount -  price
  }

  // Synchronized still works in scala
  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.synchronized {
      bankAccount.amount -= price
    }
  }

  // Both try to subtract from 50K at the same time instead of one after the other
  // One writes 46000 and another writes 47000,
  def demoBankingProblem(): Unit = {
    (1 to 20000).foreach { _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buy(account, "shoes", 3000))
      val thread2 = new Thread(() => buy(account, "iPhone", 4000))

      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000) println(s"AHA! I've just broken the bank: ${account.amount}")
    }
  }
  demoBankingProblem()

  // How to avoid

  /**
   * Exercises
   * 1 - create "inception threads"
   *  thread1
   *    thread2
   *      thread3
   * each thread prints hello from thread i
   * print all messages in reverse order
   *
   * 2 - what's the max/min value of x
   * 3 - "sleep fallacy"
   */

  // 1
  def createThreadsNested(maxThreads: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = createThreadsNested(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })
  }

  val test = createThreadsNested(100)
  test.start()
  test.join()

  // min 1, max 100
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
  }

  // No idea could be either depending on scheduling
  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is awesome"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1000)
    println(message)
  }

  demoSleepFallacy()
}
