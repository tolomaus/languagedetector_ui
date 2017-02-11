package test

import play.api.test.FakeRequest

object TestUtils {
  def addCredentials[A](request: FakeRequest[A]): FakeRequest[A] = request.withSession("user.name" -> "bob", "user.role" -> "admin")
}
