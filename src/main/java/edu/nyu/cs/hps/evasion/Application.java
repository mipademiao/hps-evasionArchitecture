package edu.nyu.cs.hps.evasion;

import edu.nyu.cs.hps.evasion.game.GameHost;

public class Application {
  public static void main(String[] args) {
    if(args.length < 4){
      System.err.println("Require args: [player 1 port] [player 2 port] [max walls] [wall placement delay] [display_host] [display_port]");
    } else {
      try {
        int p1port = Integer.parseInt(args[0]);
        int p2port = Integer.parseInt(args[1]);
        int maxWalls = Integer.parseInt(args[2]);
        int wallDelay = Integer.parseInt(args[3]);
        String displayHost = (args.length > 5) ? args[4] : null;
        int displayPort = (args.length > 5) ? Integer.parseInt(args[5]) : 0;
        GameHost.hostGame(p1port, p2port, maxWalls, wallDelay, displayHost, displayPort);
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }
}
