package com.github.timsetsfire.gateway

import py4j.GatewayServer;
import java.net.InetAddress;
import com.datarobot.prediction.Predictors;
import com.datarobot.prediction.IClassificationPredictor;
import com.datarobot.prediction.IRegressionPredictor;
import com.datarobot.prediction.ITimeSeriesRegressionPredictor;
import com.datarobot.prediction.IPredictorInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import collection.JavaConverters._

import com.github.timsetsfire.gateway.Implicits._

class CodegenGateway(val modelId: String) {
  val model: IPredictorInfo = Predictors.getPredictor(modelId)
  val isRegression = model.isInstanceOf[IRegressionPredictor]
  val modelType = model match {
    case m: ITimeSeriesRegressionPredictor => "ITimeSeriesRegressionPredictor"
    case m: IClassificationPredictor       => "IClassificationPredictor"
    case m: IRegressionPredictor           => "IRegressionPredictor"
  }
  val headers = modelType match {
    case "IRegressionPredictor" => Array("Predictions")
    case "IClassificationPredictor" => 
      model.asInstanceOf[IClassificationPredictor].getClassLabels
    case "ITimeSeriesRegressionPredictor" => TSRegressionPrediciton.headers
  }

  def csvToArrayListOfMap(csv: String): ArrayList[java.util.Map[String, _]] = {
    val data: StringReader = new StringReader(csv)
    val csvFormat = CSVFormat.DEFAULT.withHeader();
    val records = csvFormat.parse(data).getRecords.asScala.map { _.toMap }
    val arr = new ArrayList[java.util.Map[String, _]]
    records.foreach(r => arr.add(r))
    arr
  }
    

  def scoreTimeSeriesFileCSV(csv: String) = {
    val inferenceData = csvToArrayListOfMap(csv)
    modelType match {
      case "ITimeSeriesRegressionPredictor" => {
        model
          .asInstanceOf[ITimeSeriesRegressionPredictor]
          .score(inferenceData)
          .asScala.map(_.toCSV)
        }
      case _ => throw new Exception(s"scoring for $modelType not implemented")
    }
    }
  

  def scoreFileCSV(csv: String) = {
    val data: StringReader = new StringReader(csv)
    val csvFormat = CSVFormat.DEFAULT.withHeader();
    val records = csvFormat.parse(data).getRecords.asScala.map { _.toMap }
    records.map { record =>
      isRegression match {
        case true =>
          RegressionPrediction(model.asInstanceOf[IRegressionPredictor].score(record)).toCSV
        case false =>
          val score =
            model.asInstanceOf[IClassificationPredictor].score(record)
          ClassificationPrediction(score, headers).toCSV
      }
    }
  }

  def score(csv: String) = {

    val csvPrinter: CSVPrinter = new CSVPrinter(
      new StringWriter(),
      CSVFormat.DEFAULT.withHeader(headers: _*)
    );

    val predictions = modelType match { 
      case "ITimeSeriesRegressionPredictor" => scoreTimeSeriesFileCSV(csv)
      case _ => scoreFileCSV(csv)
    }

    predictions.foreach ( value =>
      csvPrinter.printRecord(value.split(",").map { _.asInstanceOf[Object] }: _*)
    )
    csvPrinter.flush();
    val outStream: StringWriter =
      csvPrinter.getOut().asInstanceOf[StringWriter];
    outStream.toString();
  }

}

object CodegenGateway {

  def main(args: Array[String]) = {
    println(args.mkString(","))
    var port = args(0).toInt
    val pythonPort = args(1).toInt
    val address = InetAddress.getLoopbackAddress()
    val pythonAddress = InetAddress.getLoopbackAddress()
    val connectTimeout = args(2).toInt
    val readTimeout = args(3).toInt;
    val modelId = args(4)

    GatewayServer.turnAllLoggingOn();

    val server: GatewayServer = new GatewayServer(
      new CodegenGateway(modelId),
      port,
      pythonPort,
      address,
      pythonAddress,
      connectTimeout,
      readTimeout,
      null
    );
    println("java port:" + port.toString());
    println("python port:" + pythonPort.toString());
    println("address:" + address.toString());
    println("pythonAddress:" + pythonAddress.toString());
    println("starting Gateway");
    server.start();
  }

}
