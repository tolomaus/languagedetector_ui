package services.language

import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.CommonTextObjectFactories

import scala.collection.JavaConversions._

class LanguageDetector {
  private val languageProfiles = new LanguageProfileReader().readAllBuiltIn
  private val languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard)
    .withProfiles(languageProfiles)
    .build()
  private val textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText

  def detectLanguage(text: String): String = {
    languageDetector.getProbabilities(textObjectFactory.forText(text)).headOption.map(_.getLocale.getLanguage).getOrElse("xx")
  }
}
