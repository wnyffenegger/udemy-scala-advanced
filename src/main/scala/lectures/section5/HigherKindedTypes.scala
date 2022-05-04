package lectures.section5

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global

object HigherKindedTypes extends App {


  trait AHigherKindedType[F[_]]

  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyOption[T] {
    def flatMap[B](f: T => B): MyOption[B]
  }

  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyFuture[B]
  }

//  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
//    for {
//      a <- listA
//      b <- listB
//    } yield (a, b)
//
//  def multiply[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] =
//    for {
//      a <- optionA
//      b <- optionB
//    } yield (a, b)
//
//  def multiply[A, B](futureA: Future[A], futureB: Future[B]): Future[(A, B)] =
//    for {
//      a <- futureA
//      b <- futureB
//    } yield (a, b)

  /**
   * Write a generic multiply method for anything implementing a Monad trait.
   *
   * Need map and flatMap to work
   *
   * List, Future, and Option are higher kinded types
   */

  trait Monad[F[_], A] {
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]
  }

  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }

  implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)

    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] = {
    for {
      a <- ma
      b <- mb
    } yield (a, b)
  }



  println(multiply(new MonadList(List(1, 2)), new MonadList(List("a", "b"))))
  println(multiply(new MonadOption(Some(10)), new MonadOption(Some("a"))))

  // Adding implicit to the class declarations and to the parameters for multiply
  // takes this to the next level
  println(multiply(List(1, 2), List("a", "b")))
  println(multiply(Some(10), Some("a")))

}
