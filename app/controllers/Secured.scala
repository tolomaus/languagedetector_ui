package controllers

import javax.inject._

import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import repositories.UserAccountRepo.UserAccount

@Singleton
trait Secured {
  protected val logger: Logger = LoggerFactory.getLogger(this.getClass)

  object Authorized extends AuthenticatedBuilder(req => getUserFromRequest(req), req => Results.Unauthorized)

  private def getUserFromRequest(request: RequestHeader): Option[UserAccount] = {
    for {
      userName <- request.session.get("user.name")
      role <- request.session.get("user.role")
    } yield UserAccount(userName, role)
  }
}
