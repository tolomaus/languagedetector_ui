package controllers

import org.scalatestplus.play._
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.test.Helpers._
import play.api.test._
import test._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
class LanguageDetectionControllerSpec extends PlaySpec with OneAppPerTest {
  protected val logger = LoggerFactory.getLogger(this.getClass)

  "CompanySearchController" should {
    "find companies by search term" in {
      val result = route(app, TestUtils.addCredentials(FakeRequest(GET, "/api/language-detection/abc"))).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }
  }
}
