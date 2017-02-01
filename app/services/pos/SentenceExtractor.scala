package services.pos

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.Try

class SentenceExtractor {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val props = new Properties()
  props.put("annotators", "tokenize, ssplit, pos, lemma")

  private val pipeline = Try(new StanfordCoreNLP(props))
    .getOrElse {
      logger.warn("failed to create pipeline, retrying once...")
      new StanfordCoreNLP(props)
    }

  def convertTextToSentences(text: String): Array[String] = {
    val doc = new Annotation(text)
    pipeline.annotate(doc)

    doc.get(classOf[SentencesAnnotation]).asScala.toArray.map(_.toString)
  }
}


