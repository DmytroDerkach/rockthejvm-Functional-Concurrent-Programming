package lectures.part2advancedfunctionalprogramming

object PartialsFunctions {

  val aFunction: Int => Int = x => x + 1

  val aFussyFunction = (x: Int) =>
    if (x == 0) 123 else if (x == 10) 999 else
      throw new RuntimeException("error")

  //partial functio
  val aPartialFunction: PartialFunction[Int, Int] = { // x => x match {...}
    case 1 => 123
    case 2 => 223
    case 3 => 523
    case 4 => 23
  }

  val canCallPartialFunction = aPartialFunction.isDefinedAt(37)
  private val listedPf: Int => Option[Int] = aPartialFunction.lift

  val anotherPF: PartialFunction[Int, Int] = { // x => x match {...}
    case 10 => 1230
    case 20 => 2230
    case 30 => 5230
  }

  val pfChain = aPartialFunction.orElse[Int, Int](anotherPF)

  // HOF accept PFs as args
  val aList = List(1,2,3,4)
  val aChangedList = aList.map(aPartialFunction)
  def main(args: Array[String]): Unit = {
    println(aPartialFunction(2)) // 223
    println(canCallPartialFunction) // false
    println(listedPf(3)) // Some(523)
    println(listedPf(0)) // None

    println(pfChain(10))
    println(aChangedList)
  }

}
