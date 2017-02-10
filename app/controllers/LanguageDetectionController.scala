package controllers

import javax.inject.{Inject, Singleton}

import model.Sentence
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import services.language.LanguageDetector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LanguageDetectionController @Inject()(languageDetector: LanguageDetector) extends Controller with Secured {
  private implicit val sentenceJson = Json.writes[Sentence]

  def detectLanguage(text: String): Action[AnyContent] =
    Authorized.async { request =>
      Future {
        val sentences = text.split(".")
          .map(_.trim)
          .map(sentence => Sentence(sentence, languageDetector.detectLanguage(sentence)))

        Ok(Json.toJson(sentences)).as(JSON)
      }
    }
}
