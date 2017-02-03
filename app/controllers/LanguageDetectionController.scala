package controllers

import javax.inject.{Inject, Singleton}

import model.Sentence
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import services.pos.{LanguageDetector, SentenceExtractor}

import scala.concurrent.Future

@Singleton
class LanguageDetectionController @Inject()(sentenceExtractor: SentenceExtractor, languageDetector: LanguageDetector) extends Controller with Secured {
  private implicit val sentenceJson = Json.writes[Sentence]

  def detectLanguage(text: String): Action[AnyContent] =
    Authorized.async { request =>
      val sentences = sentenceExtractor
        .convertTextToSentences(text)
        .map(sentence => Sentence(sentence, languageDetector.detectLanguage(sentence)))

      Future.successful(Ok(Json.toJson(sentences)).as(JSON))
    }
}
