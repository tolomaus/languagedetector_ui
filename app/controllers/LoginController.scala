package controllers

import javax.inject._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import repositories.UserAccountRepo
import repositories.UserAccountRepo.UserAccount

import scala.concurrent.Future

@Singleton
class LoginController extends Controller with Secured {

  def login(): Action[AnyContent] = {
    Action.async { request =>
      Future {
        request.body.asJson
          .map { paramsJson =>
            val userName = (paramsJson \ "userName").as[String]
            val password = (paramsJson \ "password").as[String]

            getUserAccountO(userName)
              .map { userAccount =>
                if (userAccount.password == password) {
                  Ok
                    .withSession("user.name" -> userAccount.userName, "user.role" -> userAccount.role)
                    .withCookies(Cookie("role", userAccount.role, httpOnly = false))
                } else
                  Forbidden
              }
              .getOrElse(NotFound)
          }
          .getOrElse(BadRequest)
      }
    }
  }

  def logout(): Action[AnyContent] = {
    Action { request =>
      Ok.withNewSession.discardingCookies(DiscardingCookie("role"))
    }
  }

  private def getUserAccountO[A, B, C](userName: String): Option[UserAccount] = {
    new UserAccountRepo().findByUserNameO(userName)
  }
}
