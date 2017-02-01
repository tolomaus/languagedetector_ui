package utils

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{Duration, FiniteDuration}

object Utils {
  val cf: Config = ConfigFactory.load

  def getConfig(path: String): String = {
    cf.getString(path)
  }

  def getTimestamp: String = {
    new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime)
  }

  def getDuration(startDate: Date, stopDate: Date): FiniteDuration = {
    Duration.fromNanos(TimeUnit.NANOSECONDS.convert(stopDate.getTime, TimeUnit.MILLISECONDS) - TimeUnit.NANOSECONDS.convert(startDate.getTime, TimeUnit.MILLISECONDS))
  }

  def getDuration(seconds: Int): FiniteDuration = {
    Duration.fromNanos(TimeUnit.NANOSECONDS.convert(seconds, TimeUnit.SECONDS))
  }

  def formatDuration(duration: FiniteDuration): String = {
    val elapsedInHours = duration.toHours
    val elapsedInMinutes = duration.toMinutes % 60
    val elapsedInSeconds = duration.toSeconds % 60
    "%01dh%02dm%02ds".format(elapsedInHours, elapsedInMinutes, elapsedInSeconds)
  }

  def parseDuration(duration: String): Int = {
    val values = duration.split("h|m|s")
    if (values.length == 3)
      values(0).toInt * 60 * 60 + values(1).toInt * 60 + values(2).toInt
    else
      0
  }

  def roundDouble(double: Double, precision: Int = 3): Double = {
    val dimension = Math.pow(10, precision)
    Math.round(double * dimension) / dimension
  }
}
