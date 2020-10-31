package com.github.timsetsfire.gateway;

import py4j.GatewayServer;
import java.net.InetAddress;

public class Main {

  public static void main(String[] args) {
    // AdditionApplication app = new AdditionApplication();
    // app is now the gateway.entry_point
    
    Integer port = Integer.parseInt(args[0]);
    Integer pythonPort = Integer.parseInt(args[1]);
    java.net.InetAddress address = null ;
    java.net.InetAddress pythonAddress = null;
    Integer connectTimeout = Integer.parseInt(args[2]);
    Integer readTimeout = Integer.parseInt(args[3]);
    
    try { 
      address = InetAddress.getLoopbackAddress();
      pythonAddress = InetAddress.getLoopbackAddress();
    } catch(Exception e) {
      System.out.println("address failure");
    }
    

    try {
      Class.forName("com.datarobot.prediction.Predictors");
      System.out.println("success");
    } catch(Exception e) {
      System.out.println("failure");
    }

    System.out.println(port);
    GatewayServer.turnAllLoggingOn();

    GatewayServer server = new GatewayServer(null, port, pythonPort,address,pythonAddress, connectTimeout, readTimeout, null);
    System.out.println("java port:" + port.toString());
    System.out.println("python port:" + pythonPort.toString());
    System.out.println("address:" + address.toString());
    System.out.println("pythonAddress:" + pythonAddress.toString());
    System.out.println("starting Gateway");
    server.start();
  }
}


