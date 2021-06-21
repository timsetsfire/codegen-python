package com.github.timsetsfire.gateway

import py4j.GatewayServer;
import java.net.InetAddress;
import com.datarobot.prediction.Predictors;
import com.datarobot.prediction.IClassificationPredictor;
import com.datarobot.prediction.IRegressionPredictor;
import com.datarobot.prediction.IClassificationPredictor;
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

class CodegenGateway(val modelId: String) {
  val model: IPredictorInfo = Predictors.getPredictor(modelId)
  val isRegression = model.isInstanceOf[IRegressionPredictor]
  val headers = isRegression match { 
      case true => Array("Predictions")
      case false => model.asInstanceOf[IClassificationPredictor].getClassLabels
  }
  
  def scoreFileCSV(csv: String) = {
    val data: StringReader = new StringReader(csv)
    val csvFormat = CSVFormat.DEFAULT.withHeader();
    val records = csvFormat.parse(data).getRecords.asScala.map { _.toMap }
    records.map { record =>
      isRegression match {
        case true =>
          Array(model.asInstanceOf[IRegressionPredictor].score(record))
        case false =>
          val score = model.asInstanceOf[IClassificationPredictor].score(record)
          headers.map{ h => score.get(h)}
      }
    }
  }

  def score(csv: String) = {
    
    val csvPrinter: CSVPrinter = new CSVPrinter(new StringWriter(), CSVFormat.DEFAULT.withHeader(headers:_*));
    val predictions = scoreFileCSV(csv)
    predictions.foreach{ value => csvPrinter.printRecord(value.map{_.asInstanceOf[Object]}: _*) }
    csvPrinter.flush();
    val outStream: StringWriter = csvPrinter.getOut().asInstanceOf[StringWriter];
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
    // val model: IPredictorInfo = Predictors.getPredictor(modelId)

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

