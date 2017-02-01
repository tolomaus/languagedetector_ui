package utils.decorators

import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration

object WithStopwatch {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def apply[B](f: => B): B = {
    apply("")(f)
  }

  def apply[B](label: String)(f: => B): B = {
    val start = System.nanoTime

    val result = f

    val stop = System.nanoTime
    val elapsed = Duration.fromNanos(stop - start)
    val elapsedInHours = elapsed.toHours
    val elapsedInMinutes = elapsed.toMinutes % 60
    val elapsedInSeconds = elapsed.toSeconds % 60
    val elapsedInMilliSeconds = elapsed.toMillis % 60

    logger.info(s"Stopwatch: $label ${"%02d".format(elapsedInHours)}:${"%02d".format(elapsedInMinutes)}:${"%02d".format(elapsedInSeconds)},${"%03d".format(elapsedInMilliSeconds)}")

    result
  }
}
