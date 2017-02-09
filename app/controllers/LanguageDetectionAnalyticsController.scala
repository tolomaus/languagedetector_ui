package controllers

import javax.inject.{Inject, Singleton}

import io.eels.Row
import io.eels.component.parquet.ParquetSource
import model.SentenceCountByLanguage
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
        implicit val configuration = new Configuration()
        implicit val fs = FileSystem.get(configuration)

        val path = Utils.getConfig("spark.content") + "/parquet/SentenceCountsByLanguage/*"
        logger.info(s"Loading the sentence counts from file $path...")

        val sentenceCountsByLanguage = ParquetSource(new Path(path))
          .toFrame
          .collect
          .map(convertRow)
          .toArray

        val labels = sentenceCountsByLanguage.map(_.language)
        val series = Array("sentence counts by language")
        val data = Array(sentenceCountsByLanguage.map(_.count))


        Ok(Json.toJson(Dataset(labels, series, data))).as(JSON)
      }
    }

  private def convertRow(row: Row): SentenceCountByLanguage = {
    val language = row.values(0).asInstanceOf[String]
    val count = row.values(1).asInstanceOf[Long]

    SentenceCountByLanguage(language, count)
  }
}

case class Dataset(labels: Array[String], series: Array[String], data: Array[Array[Long]])