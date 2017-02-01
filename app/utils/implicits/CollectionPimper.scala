package utils.implicits

import scala.collection.parallel.ParIterable

object CollectionPimper {
  implicit class TopPimper[A](iterable: Iterable[A]) {
    def top(top: Int)(orderBy: A => Double): List[A] = {
      val ord = Ordering.by[A, Double](orderBy)
      val queue = collection.mutable.PriorityQueue()(ord)
      val ordRev = ord.reverse

      iterable
        .foldLeft(queue) { (queue, item) =>
          if (queue.size < top) {
            queue.enqueue(item)
          } else if (ordRev.compare(item, queue.head) >= 0) {
            queue.dequeue
            queue.enqueue(item)
          }

          queue
        }
        .toList
        .sorted(ord)
    }
  }

  implicit class ParTopPimper[A](parIterable: ParIterable[A]) {
    def top(top: Int)(orderBy: A => Double): List[A] = {
      val ord = Ordering.by[A, Double](orderBy)
      val queue = collection.mutable.PriorityQueue()(ord)
      val ordRev = ord.reverse

      parIterable
        .foldLeft(queue) { (queue, item) =>
          if (queue.length < top) {
            queue.enqueue(item)
          } else if (ordRev.compare(item, queue.head) >= 0) {
            queue.dequeue
            queue.enqueue(item)
          }
          queue
        }
        .toList
        .sorted(ord)
    }
  }
}
