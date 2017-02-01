package utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutureUtils {

  implicit class FutureObjectOps(fo: Future.type) {
    def switchOFToFO[A](of: Option[Future[A]]): Future[Option[A]] = {
      of match {
        case Some(f) => f.map(Some(_))
        case None => Future.successful(None)
      }
    }

    def flattenOFOToFO[A](ofo: Option[Future[Option[A]]]): Future[Option[A]] = {
      ofo.getOrElse(Future.successful(None))
    }

    def flattenFOFToFO[A](fof: Future[Option[Future[A]]]): Future[Option[A]] = {
      fof.flatMap(i => switchOFToFO(i))
    }

    def flattenFOFOToFO[A](fofo: Future[Option[Future[Option[A]]]]): Future[Option[A]] = {
      flattenFOFToFO(fofo).map(_.flatten)
    }
  }

}
