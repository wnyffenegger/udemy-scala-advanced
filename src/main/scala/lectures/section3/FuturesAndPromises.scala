package lectures.section3

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}

object FuturesAndPromises extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife
  } // add execution context implicitly by the compiler

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the future")

  aFuture.onComplete(t => t match {
    case Success(meaningoflife) => println(s"meaning of life is $meaningoflife")
    case Failure(e) => println(s"I failed with $e")
  })

  Thread.sleep(3000)

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(e) => e.printStackTrace()
  }

  Thread.sleep(1000)

  // Prettier
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFried = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
//  val zucksBestFriedRestricted = marksBestFried.filter(profile => profile.name.startsWith("Z"))

  // Much Prettier

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // Fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unkown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "forever alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unkown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))


  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the DB
      // create a transaction
      // wait for it to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions
    }
  }

  println(BankingApp.purchase("Daniel", "iPhone 12", "rock the jvm store", 3000))

  val promise = Promise[Int]()
  val future = promise.future

  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)

  /**
   * 1) fulfill future immediately with a value
   * 2) inSequence(fa, fb) run fb after fa
   * 3) first(fa, fb) => new future holding either a or b whichever finishes first
   * 4) last(fa, fb) => new future holding either b or a whichever finishes last
   * 5) retryUntil(action: () => Future[T], condition: T => Boolean): Future[T] repeat until a condition is met and return matching future as a result
   */

  // 1
  val now = Future {
    10
  }

  // 2
  def inSequence[T](fa: Future[T], fb: Future[T]): Future[T] =
    fa.flatMap(_ => fb)

  def first[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val promise = Promise[T]()

//    fa.onComplete({
//      case Success(t: T) => try { promise.success(t) } catch { case _ => }
//      case Failure(e) => try { promise.failure(e) } catch { case _ => }
//    })
//
//    fb.onComplete({
//      case Success(t: T) => try { promise.success(t) } catch { case _ => }
//      case Failure(e) => try { promise.failure(e) } catch { case _ => }
//    })

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  def last[T](fa: Future[T], fb: Future[T]): Future[T] = {
    val bothPromise = Promise[T]
    val lastPromise = Promise[T]

    fa.onComplete(result => {
      if (!bothPromise.tryComplete(result))
          lastPromise.tryComplete(result)
    })

    fb.onComplete(result => {
      if (!bothPromise.tryComplete(result))
        lastPromise.tryComplete(result)
    })

    lastPromise.future
  }

  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
//    val result = action()
//    result.andThen({
//      case Success(value) => if (condition(value)) result else retryUntil(action, condition)
//      case _ => retryUntil(action, condition)
//    })

    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action, condition)
      }
  }


}
