package lectures.part3concurrency

import scala.collection.parallel.CollectionConverters.ImmutableSeqIsParallelizable
import scala.collection.parallel.ParSeq
import scala.collection.parallel.immutable.ParVector

object ParallelCollections {
  /* val aList = (1 to 1000000).toList
   val incrementedList = aList.map(_ + 1)
   val parList: ParSeq[Int] = aList.par
   val parIncrementedList = parList.map(_ + 1)*/

  /*
  Applicable for
  - Seq
  - Vector
  - Arrays
  - Maps
  - Sets

   */

  val parVector = ParVector[Int](1, 2, 3, 4)

  private def measure[A](expression: => A): Long = {
    val time = System.currentTimeMillis()
    expression // forcing evaluation
    System.currentTimeMillis() - time
  }

  def compareListTransformation() = {
    val list = (1 to 30000000).toList
    println("list is done")
    val serialTime = measure(list.map(_ + 1))
    println(s"serialTime : $serialTime")

    val parallelTime = measure(list.par.map(_ + 1))

    println(s"parallelTime : $parallelTime")

  }

  def demoUndefinedOrder() = {
    val aList = (1 to 1000).toSeq
    val reduction = aList.reduce(_ - _) // [1,2,3].reduce( _ - _ ) = 1-2-3 = -4

    val parallelReduction = aList.par.reduce(_ - _)

    println(s" seq reduction: $reduction") // seq reduction: -500498
    println(s" par reduction: $parallelReduction") // par reduction: -160088
  }


  // for associative operations, result is deterministic
  def demoDefinedOrder() = {
    val strings = "I love parallel collections".split(" ").toList
    val concatenation = strings.reduce(_ + "|" + _)
    val parConcatenation = strings.par.reduce(_ + "|" + _)

    println(s" seq concatenation: $concatenation") // seq reduction: -500498
    println(s" par parConcatenation: $parConcatenation") // par reduction: -160088
  }

  def demoRaceConditions() = {
    var sum = 0
    (1 to 1000).par.foreach(ele => sum += ele)
    println(s"sum: $sum")
  }

  def main(args: Array[String]): Unit = {
    //    compareListTransformation()
    //    demoUndefinedOrder()
    //    demoDefinedOrder()
    demoRaceConditions()
  }

}
