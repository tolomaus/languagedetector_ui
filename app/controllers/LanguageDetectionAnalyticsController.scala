package controllers

import javax.inject.{Inject, Singleton}

import io.eels.Row
import io.eels.component.parquet.ParquetSource
import model.{SentenceCountByLanguage, WrongDetectionByLanguage}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LanguageDetectionAnalyticsController @Inject() extends Controller with Secured {
  private implicit val datasetJson = Json.writes[Dataset]

  def getDataset: Action[AnyContent] =
    Authorized.async { request =>
      Future {
        val path = Utils.getConfig("languagedetector.dir") + "/data/parquet"
        logger.info(s"Loading the sentence counts from file $path...")
        val sentenceCounts = readParquet(s"$path/SentenceCountsByLanguage/*", convertToSentenceCount).sortBy(_.detectedLanguage)

        logger.info(s"Loading the wrong detections from file $path...")
        val nonZeroWrongDetections = readParquet(s"$path/WrongDetectionsByLanguage/*", convertToWrongDetection)
        val wrongDetections = sentenceCounts.map { sc =>
          nonZeroWrongDetections
            .find(_.detectedLanguage == sc.detectedLanguage)
            .map(_.count)
            .getOrElse(0.toLong)
        }

        val labels = sentenceCounts.map(_.detectedLanguage)
        val series = Array("sentence counts", "wrong detections")
        val data = Array(sentenceCounts.map(_.count), wrongDetections)

        Ok(Json.toJson(Dataset(labels, series, data))).as(JSON)
      }
    }

  private def readParquet[T](path: String, rowConverter: Row => T) = {
    implicit val configuration = new Configuration()
    implicit val fs = FileSystem.get(configuration)

    ParquetSource(new Path(path))
      .toFrame
      .collect
      .map(convertToWrongDetection)
      .toArray
  }

  private def convertToSentenceCount(row: Row): SentenceCountByLanguage = {
    val language = row.values(0).asInstanceOf[String]
    val count = row.values(1).asInstanceOf[Long]

    SentenceCountByLanguage(language, count)
  }

  private def convertToWrongDetection(row: Row): WrongDetectionByLanguage = {
    val language = row.values(0).asInstanceOf[String]
    val count = row.values(1).asInstanceOf[Long]

    WrongDetectionByLanguage(language, count)
  }
}

case class Dataset(labels: Array[String], series: Array[String], data: Array[Array[Long]])