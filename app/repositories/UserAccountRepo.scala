package repositories

import repositories.UserAccountRepo.UserAccount

import scala.concurrent.ExecutionContext

class UserAccountRepo {
  def findByUserNameO(userName: String)(implicit executor: ExecutionContext): Option[UserAccount] = {
    if (userName == "user")
      Some(UserAccount(userName, "user", userName))
    else
      None
  }
}

object UserAccountRepo {
  case class UserAccount(userName: String, role: String, password: String = "")
}
