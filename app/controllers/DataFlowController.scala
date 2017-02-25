package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, AnyContent, Controller}
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

@Singleton
class DataFlowController @Inject() extends Controller with Secured {
  def getDataset: Action[AnyContent] =
    Authorized.async { request =>
      Future {
        val path = Utils.getConfig("languagedetector.dir") + "/data/text/data-flow.json"

        if (new File(path).exists)
          Ok(Source.fromFile(path).getLines.mkString).as(JSON)
        else
          InternalServerError
      }
    }
}
