package loyaltea

implicit class ListUtilsOps[A](list: List[A]) {
  def filterOpt[B](option: Option[B])(filter: (A, B) => Boolean): List[A] = {
    option.fold(list)(b => list.filter(filter(_, b)))
  }

  def takeOpt(opt: Option[Int]): List[A] =
    opt match {
      case Some(o) => list.take(o)
      case None => list
    }

  def dropOpt(opt: Option[Int]): List[A] =
    opt match {
      case Some(o) => list.drop(o)
      case None => list
    }

  def add(a: A): List[A] = a :: list

  def delete(cond: A => Boolean): List[A] = list.filterNot(cond)

  def update(cond: A => Boolean, f: A => A): List[A] = list.map(i => if (cond(i)) f(i) else i)
}

object Utils