package edu.nyu.cs.hps.evasion.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class IO {

  public static class Response {
    public String message;
    public Duration elapsed;
  }

  private static class SocketInfo {
    public int index;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    public ExecutorService pool;
    public PrintWriter in;
    public BufferedReader out;
    public String name;
  }

  private List<SocketInfo> infoList;

  public void start(List<Integer> ports) throws Exception {
    infoList = new ArrayList<>();
    for(Integer port : ports) {
      SocketInfo info = new SocketInfo();
      info.pool = Executors.newFixedThreadPool(1);
      info.name = "port_" + port;
      info.serverSocket = new ServerSocket(port);
      info.index = infoList.size();
      infoList.add(info);
    }
    List<Future<Socket>> futures = new ArrayList<>();
    for(SocketInfo info : infoList) {
      futures.add(info.pool.submit(() -> info.serverSocket.accept()));
    }
    for(SocketInfo info : infoList) {
      info.clientSocket = futures.get(info.index).get();
      info.clientSocket.setTcpNoDelay(true);

      info.in = new PrintWriter(info.clientSocket.getOutputStream(), true);
      info.out = new BufferedReader(new InputStreamReader(info.clientSocket.getInputStream()));

      info.name = getResponse(info.index, "sendname").get().message;

      System.out.println("Hello " + info.name + " (player " + (info.index+1) + ")");
    }
  }

  public void sendLine(int index, String string){
    SocketInfo info = infoList.get(index);
    info.in.print(string + "\n");
    info.in.flush();
  }

  public Future<Response> getResponse(int index, String string) throws Exception {
    sendLine(index, string);
    SocketInfo info = infoList.get(index);
    return info.pool.submit(() -> {
      Instant start = Instant.now();
      Response response = new Response();
      response.message = info.out.readLine();
      Instant now = Instant.now();
      response.elapsed = Duration.between(start, now);
      return response;
    });
  }

  public void destroy() {
    try {
      for(SocketInfo info : infoList) {
        info.clientSocket.close();
        info.serverSocket.close();
        info.pool.shutdown();
      }
    } catch (Exception e){
      System.out.println(e.getMessage());
    }
  }

  public String getName(int index){
    return infoList.get(index).name;
  }

}
