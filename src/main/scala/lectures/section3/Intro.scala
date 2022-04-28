package lectures.section3

import java.util.concurrent.Executors

object Intro extends App {


  /**
   * Normal Java Thread takes a trait with Runnable
   */
  val aThread = new Thread(new Runnable:
    override def run(): Unit = println("Running in parallel")
  )

  // Gives signal to JVM to start a JVM thread (not an OS thread, wraps OS thread)
  // Thread instances are not JVM threads
  aThread.start()

  // Blocks until a thread finishes running
  aThread.join()

  val runnable = new Runnable:
    override def run(): Unit = println("Running in parallel")

  // Just demo that results vary due to scheduling
  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
  threadHello.start()
  threadGoodbye.start()

  val pool = Executors.newFixedThreadPool(10)

  pool.execute(() => println("something in the thread pool"))
  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  // Let currently running
//  pool.shutdown()

  // Interrupt currently running threads, not necessarily kill (yay JVM)
  pool.shutdownNow()
//  pool.execute(() => println("should not appear"))

  println(pool.isShutdown)
}
