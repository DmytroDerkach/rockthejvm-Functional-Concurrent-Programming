package lectures.part2advancedfunctionalprogramming

import scala.annotation.tailrec

abstract class FSet[A] extends (A => Boolean) {
  // main API
  def contains(element: A): Boolean
  def apply(element: A): Boolean = contains(element)
  def +(element: A): FSet[A]
  def ++(anotherSet: FSet[A]): FSet[A]
  // "classics"
  def map[B](f: A => B): FSet[B]
  def flatMap[B](f: A => FSet[B]): FSet[B]
  def filter(predicate: A => Boolean): FSet[A]
  def foreach(f: A => Unit): Unit

  // methodws
  def -(element: A): FSet[A]
  def --(anotherSet: FSet[A]): FSet[A]
  def &(anotherSet: FSet[A]): FSet[A]
  def unary_! : FSet[A] = new PBSet[A](x => !contains(x))
}

// property based set
class PBSet[A](property: A => Boolean) extends FSet[A] {
  override def contains(element: A): Boolean = property(element)
  override def +(element: A): FSet[A] = new PBSet[A](x => x == element || property(x))
  override def ++(anotherSet: FSet[A]): FSet[A] = new PBSet[A](x => property(x) || anotherSet(x))
  override def map[B](f: A => B): FSet[B] = politelyFail()
  override def flatMap[B](f: A => FSet[B]): FSet[B] = politelyFail()
  override def filter(predicate: A => Boolean): FSet[A] = new PBSet[A](x => property(x) && predicate(x))
  override def foreach(f: A => Unit): Unit = politelyFail()
  override def -(element: A): FSet[A] = filter(x => x != element)
  override def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet)
  override def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)
  override def unary_! : FSet[A] = new PBSet[A](x => !contains(x))
  // ---
  private def politelyFail() = throw new RuntimeException("set is iterable")
}


case class Empty[A]() extends FSet[A] {
  override def contains(element: A): Boolean = false
  override def +(element: A): FSet[A] = new Cons[A](element, this)
  override def ++(anotherSet: FSet[A]): FSet[A] = anotherSet
  override def map[B](f: A => B): FSet[B] = Empty[B]
  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty[B]
  override def filter(predicate: A => Boolean): FSet[A] = this
  override def foreach(f: A => Unit): Unit = ()
  override def -(element: A): FSet[A] = this
  override def --(anotherSet: FSet[A]): FSet[A] = this
  override def &(anotherSet: FSet[A]): FSet[A] = this

}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {
  override def contains(element: A): Boolean = {
    element == head || tail.contains(element)
  }
  override def +(element: A): FSet[A] = if (contains(element)) this else Cons(element, this)
  override def ++(anotherSet: FSet[A]): FSet[A] = tail ++ anotherSet + head
  override def map[B](f: A => B): FSet[B] = tail.map(f) + f(head)
  override def flatMap[B](f: A => FSet[B]): FSet[B] = tail.flatMap(f) ++ f(head)
  override def filter(predicate: A => Boolean): FSet[A] = {
    val filteredTail = tail.filter(predicate)
    if (predicate(head)) filteredTail + head
    else filteredTail
  }
  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
  override def -(element: A): FSet[A] = {
    if (head == element) tail else tail - element + head
  }
  override def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet)
  override def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)

}

object FSet {
  def apply[A](values: A*): FSet[A] = {
    @tailrec
    def buildSet(valueSeq: Seq[A], acc: FSet[A]): FSet[A] = {
      if (valueSeq.isEmpty) acc
      else buildSet(valueSeq.tail, acc + valueSeq.head)
    }

    buildSet(values, Empty())
  }
}

object FunctionalSetPlaygound {
  val aSet = Set(1, 2, 3)
  val aSet2 = Set(1, 2, 2)

  def main(args: Array[String]): Unit = {
    println(aSet)
    println(aSet2)

    val numbers = FSet(1, 2, 3, 4, 5)
    println(numbers.contains(5))
    println(numbers(6))
    println((numbers + 10).contains(10))

    println((numbers ++ FSet(10, 11)).contains(11))

    println("///////")
    println(numbers.contains(4))
    println((numbers - 4).contains(4))
    println("///////")
    println(numbers.contains(2))
    println(numbers.contains(3))
    println((numbers & FSet(2, 3)).contains(4))
    println((numbers & FSet(2, 3)).contains(2))
    println((numbers & FSet(2, 3)).contains(3))

    println("///////")
    println(numbers.contains(2))
    println(numbers.contains(3))
    println((numbers -- FSet(2, 3)).contains(3))
    println((numbers -- FSet(2, 3)).contains(2))

    val naturals = new PBSet[Int](_ => true)
    println(naturals.contains(2)) // True
    println(!naturals.contains(2)) // false
    println((!naturals + 1 + 2 + 3).contains(3))
  }
}
