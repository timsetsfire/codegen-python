package com.github.timsetsfire.gateway

import com.datarobot.prediction.TimeSeriesScore

object Implicits {
implicit def toTSRegressionPrediction(ts: TimeSeriesScore[java.lang.Double]) = {
    val pred = (ts.getForecastPoint,
            ts.getForecastTimestamp,
            ts.getForecastDistance,
            ts.getSeriesId,
            ts.getScore)
    (TSRegressionPrediction.apply _) tupled pred
}
}

trait Prediction {
  def toCSV(): String
}




case class TSRegressionPrediction(
    forecastPoint: String,
    forecastTimeStamp: String,
    forecastDistance: Int,
    seriesId: String,
    prediction: java.lang.Double
) extends Prediction {
  def toCSV(): String = {
    s"$forecastPoint,$forecastTimeStamp,$forecastDistance,$seriesId,$prediction"
  }
}
object TSRegressionPrediciton {
  val headers = Array(
    "Forecast Point",
    "Forecast Timestamp",
    "Forecast Distance",
    "Series ID",
    "Prediction"
  )
}
case class RegressionPrediction(prediction: java.lang.Double) extends Prediction {
  val headers = Array("Predictions")
  def toCSV(): String = prediction.toString
}

case class ClassificationPrediction(
    prediction: java.util.Map[String, java.lang.Double],
    classLabels: Array[String]
) extends Prediction {
  def toCSV(): String = {
    classLabels.map { l => prediction.get(l) }.mkString(",")
  }
}
