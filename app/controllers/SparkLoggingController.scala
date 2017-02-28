package controllers

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import javax.inject.Singleton

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import utils.Utils

import scala.concurrent.Future
import scala.io.Source
import scala.reflect.io.Path._
import scala.util.{Failure, Success, Try}

@Singleton
class SparkLoggingController extends Controller with Secured {
  private implicit val logLineMessageJson = Json.writes[LogLineMessage]
  private implicit val logLineDataWriteJson = Json.writes[LogLineDataWrite]
  private implicit val logLineDataReadJson = Json.writes[LogLineDataRead]
  private implicit val logLineTransactionJson = Json.writes[LogLineTransaction]
  private implicit val logLineTransactionCategoryJson = Json.writes[LogLineTransactionCategory]
  private implicit val logLineJobJson = Json.writes[LogLineJob]
  private implicit val logLineCalcJson = Json.writes[LogLineCalc]
  private implicit val logLineWorkflowJson = Json.writes[LogLineWorkflow]

  def getBusinessLogs(mostRecent: Int = 10, includeAllDetails: Boolean = false): Action[AnyContent] =
    Authorized.async { request =>
      Future.successful(Ok(Json.toJson(getBusinessLogsImpl(mostRecent, includeAllDetails))).as(JSON))
    }

  private def getBusinessLogsImpl(mostRecent: Int = 10, includeAllDetails: Boolean = false): Array[LogLineWorkflow] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val logFilePath = Utils.getConfig("languagedetector.dir") + "/logs"
    val regex = """businessLog_2.*\.log""".r

    logFilePath.toDirectory.files
      .map(_.name)
      .flatMap { case n@regex() => Some(n) case _ => None }
      .map(path => (path, Source.fromFile(logFilePath + "/" + path).getLines.mkString("\n")))
      .filter { case (path, content) => content.nonEmpty }
      .map { case (path, content) =>
        val pathDateFormat = new SimpleDateFormat("yyyyMMddHHmm")
        val fileDate = pathDateFormat.parse(path.split("_").last.replace(".log", ""))
        (path, fileDate, content)
      }
      .toArray
      .sortBy(_._1).reverse
      .zipWithIndex
      .filter { case ((path, fileDate, content), index) => index <= mostRecent }
      .map { case ((path, fileDate, content), index) =>
        content
          .split("\n")
          .flatMap { line =>
            Try {
              val values = line.split("\t", -1)
              //don't remove empty values
              val date = dateFormat.parse(values(0))
              val module = values(1).replace("biz.meetmatch.modules.", "").replace("$", "")
              val subject = values(2)
              (date, module, subject, values.drop(3))
            } match {
              case Failure(t) =>
                logger.error(s"Error parsing log file line '$line' in log file $path", t)
                None
              case Success(t) =>
                Some(t)
            }
          }
          .filter { case (_, module, _, _) => module != "java.lang.Class.save" }
          .groupBy { case (_, module, _, _) => module }
          .flatMap { case (module, lines) =>
            Try {
              val calcLines = lines
                .filter { case (_, _, subject, _) => subject == "CALC" }
                .map { case (date, _, _, values) =>
                  val event = values(0)
                  (date, event, values.drop(1))
                }

              calcLines
                .find { case (_, event, _) => event == "START" }
                .map { case (startDate, _, values) =>
                  val options = values(0)
                    .split(",")
                    .filterNot(_.isEmpty)
                    .map { option =>
                      val options = option.split("=")
                      Array(options.head, options.drop(1).mkString("="))
                    }

                  val sparkAppId = values(1)

                  val (stopDate, state) = calcLines
                    .find { case (_, event, _) => event == "STOP" }
                    .map { case (date, _, values) =>
                      val state = values(0)

                      (date, state)
                    }
                    .getOrElse((Calendar.getInstance.getTime, "BUSY"))

                  val duration = Utils.formatDuration(Utils.getDuration(startDate, stopDate))

                  val jobs = lines
                    .filter { case (_, _, subject, _) => subject == "JOB" }
                    .map { case (date, _, _, values) =>
                      val jobId = values(0).toInt
                      val event = values(1)
                      (date, jobId, event, values.drop(2))
                    }
                    .groupBy { case (date, jobId, event, values) => jobId }
                    .map { case (jobId, linesForJobId) =>
                      val (startDate, jobDescription, stageCount, executionId) = linesForJobId
                        .find { case (_, _, event, _) => event == "START" }
                        .map { case (date, _, _, values) =>
                          val jobDescription = values(0)
                          val stageCount = values(1).toInt
                          val executionId = if (values(2) != "") values(2).toInt else -1

                          (date, jobDescription, stageCount, executionId)
                        }
                        .get

                      val (stopDate, state) = linesForJobId
                        .find { case (_, _, event, _) => event == "STOP" }
                        .map { case (date, _, _, values) =>
                          val state = values.head

                          (date, state)
                        }
                        .getOrElse((Calendar.getInstance.getTime, "BUSY"))

                      val duration = Utils.formatDuration(Utils.getDuration(startDate, stopDate))

                      LogLineJob(jobId, dateFormat.format(startDate), duration, state, jobDescription, stageCount, executionId)
                    }
                    .toArray
                    .sortBy(_.id)

                  val categoriesWithTransactions = lines
                    .filter { case (_, _, subject, _) => subject == "TRANSACTION" }
                    .map { case (date, _, _, values) =>
                      val category = values(0)
                      val id = values(1)
                      val event = values(2)
                      (date, category, id, event, values.drop(3))
                    }
                    .groupBy { case (_, category, id, _, _) => (category, id) }
                    .map { case ((category, id), linesForTransaction) =>
                      val (startDate, stageId, partitionId, taskId, message) = linesForTransaction
                        .find { case (_, _, _, event, _) => event == "START" }
                        .map { case (date, _, _, _, values) =>
                          val stageId = values(0).toInt
                          val partitionId = values(1).toInt
                          val taskId = values(2).toLong
                          val message = values(3)
                          (date, stageId, partitionId, taskId, message)
                        }
                        .get

                      val stopDateO = linesForTransaction
                        .find { case (_, _, _, event, _) => event == "STOP" }
                        .map { case (date, _, _, _, _) => date }

                      val state = stopDateO.map(_ => "FINISHED").getOrElse("BUSY")
                      val duration = Utils.formatDuration(Utils.getDuration(startDate, stopDateO.getOrElse(Calendar.getInstance.getTime)))

                      LogLineTransaction(category, id, stageId, partitionId, taskId, message, dateFormat.format(startDate), duration, state)
                    }
                    .groupBy(_.category)
                    .mapValues(_.toArray)

                  val transactionCategories = categoriesWithTransactions
                    .map { case (category, transactions) =>
                      val selectedTransactions =
                        if (includeAllDetails || transactions.length < 1000)
                          transactions.sortBy(_.startDate)
                        else
                          (transactions.takeRight(100) ++ transactions.filter(_.state == "BUSY")).sortBy(_.startDate)

                      val numberOfTransactions = transactions.length
                      val finishedTransactions = transactions.filter(_.state == "FINISHED")
                      val averageFinishedTransactionDuration =
                        if (finishedTransactions.length > 0)
                          Utils.formatDuration(Utils.getDuration(finishedTransactions.map(tr => Utils.parseDuration(tr.duration)).sum / finishedTransactions.length))
                        else
                          "n/a"

                      LogLineTransactionCategory(category, selectedTransactions, numberOfTransactions, transactions.head.startDate, averageFinishedTransactionDuration)
                    }
                    .toArray
                    .sortBy(_.startDate)

                  val dataHandlings = lines
                    .filter { case (_, _, subject, _) => subject == "DATA" }
                    .map { case (date, _, _, values) =>
                      val storage = values(0)
                      val rw = values(1)
                      val tableName = values(2)
                      (date, storage, rw, tableName, values.drop(3))
                    }

                  val dataReads = dataHandlings
                    .filter { case (_, _, rw, _, _) => rw == "READ" }
                    .map { case (date, storage, rw, tableName, values) =>

                      val count = if (values.length > 0) values(0).toInt else -1
                      LogLineDataRead(storage, tableName, count, dateFormat.format(date))
                    }
                    .sortBy(_.date)

                  val dataWrites = dataHandlings
                    .filter { case (_, _, rw, _, _) => rw == "WRITE" }
                    .map { case (date, storage, rw, tableName, values) =>
                      val countBefore = values(0).toInt
                      val countAfter = values(1).toInt
                      val countUpdated = if (values.length >= 3) values(2).toInt else -1

                      LogLineDataWrite(storage, tableName, countBefore, countAfter, countUpdated, dateFormat.format(date))
                    }
                    .sortBy(_.date)

                  val messages = lines
                    .filter { case (_, _, subject, _) => subject == "MESSAGE" }
                    .map { case (date, _, _, values) =>
                      val category = values(0)
                      val subject = values(1)
                      val message = values(2)
                      LogLineMessage(category, subject, message, dateFormat.format(date))
                    }
                    .sortBy(_.date)

                  LogLineCalc(module, dateFormat.format(startDate), stopDate, duration, state, options, sparkAppId, jobs, transactionCategories, dataReads, dataWrites, messages)
                }
            } match {
              case Failure(t) =>
                logger.error(s"Error parsing log file module $module in log file dated ${dateFormat.format(fileDate)}", t)
                None
              case Success(t) =>
                t
            }
          }
          .toArray
          .sortBy(_.startDate)
      }
      .filter(_.nonEmpty)
      .map { logLineCalcs =>
        val logLineCalcForWorkflowO = logLineCalcs.find(_.module.startsWith("biz.meetmatch.workflow."))
        val logLineFirstCalc = logLineCalcs(0)
        val logLineLastCalc = logLineCalcs.takeRight(1)(0)

        val startDateStr = logLineCalcForWorkflowO.map(_.startDate).getOrElse(logLineFirstCalc.startDate)
        val startDate = dateFormat.parse(startDateStr)
        val stopDate = logLineCalcForWorkflowO.map(_.stopDate).getOrElse(logLineLastCalc.stopDate)
        val duration = Utils.formatDuration(Utils.getDuration(startDate, stopDate))
        val state = logLineCalcForWorkflowO.map(_.state).getOrElse(logLineLastCalc.state)
        val sparkAppId = logLineCalcForWorkflowO.map(_.sparkAppId).getOrElse(logLineFirstCalc.sparkAppId)

        val options = logLineCalcForWorkflowO.map(_.options).getOrElse(logLineFirstCalc.options)
        val message = options.find(_.head == "message").map(_.reverse.head).getOrElse("no message")

        val filteredLogLineCalcs = logLineCalcs.filterNot(calc => calc.module.startsWith("biz.meetmatch.workflow.")).filterNot(calc => calc.module.startsWith("biz.meetmatch.modules.util.")).filterNot(calc => calc.module == "Class.load").filterNot(calc => calc.module == "Class.save")

        val warnings = filteredLogLineCalcs.map(_.messages.count(_.category == "WARN")).sum
        val errors = filteredLogLineCalcs.map(_.messages.count(_.category == "ERROR")).sum

        LogLineWorkflow(message, startDateStr, stopDate, duration, state, options.filterNot(_.head == "message"), sparkAppId, filteredLogLineCalcs, warnings, errors)
      }
      .filter(_.calcs.length > 0)
      .sortBy(_.startDate)
  }
}

case class LogLineWorkflow(message: String, startDate: String, stopDate: Date, duration: String, state: String, options: Array[Array[String]], sparkAppId: String, calcs: Array[LogLineCalc], warnings: Int, errors: Int)

case class LogLineCalc(module: String, startDate: String, stopDate: Date, duration: String, state: String, options: Array[Array[String]], sparkAppId: String, jobs: Array[LogLineJob], transactionCategories: Array[LogLineTransactionCategory], dataReads: Array[LogLineDataRead], dataWrites: Array[LogLineDataWrite], messages: Array[LogLineMessage])

case class LogLineJob(id: Int, startDate: String, duration: String, state: String, description: String, stageCount: Int, executionId: Int = -1)

case class LogLineTransactionCategory(category: String, transactions: Array[LogLineTransaction], numberOfTransactions: Int, startDate: String, averageFinishedTransactionDuration: String)

case class LogLineTransaction(category: String, id: String, stageId: Int, partitionId: Int, taskId: Long, message: String, startDate: String, duration: String, state: String)

case class LogLineDataRead(storage: String, tableName: String, count: Int, date: String)

case class LogLineDataWrite(storage: String, tableName: String, countBefore: Int, countAfter: Int, countUpdated: Int, date: String)

case class LogLineMessage(category: String, subject: String, message: String, date: String)
