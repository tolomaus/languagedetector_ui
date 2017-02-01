package test

import play.api.test.FakeRequest

object TestUtils {
  def addCredentials[A](request: FakeRequest[A]): FakeRequest[A] = request.withSession("user.role" -> "admin")
}
