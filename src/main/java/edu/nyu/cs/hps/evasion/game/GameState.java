package edu.nyu.cs.hps.evasion.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameState {

  public long ticknum;
  public int maxWalls;
  public int wallPlacementDelay;
  public Point boardSize;
  public int wallTimer;
  public PositionAndVelocity hunterPosAndVel;
  public Point preyPos;
  public List<Wall> walls;

  public GameState(int maxWalls, int wallPlacementDelay){
    this.walls = new ArrayList<>();
    this.maxWalls = maxWalls;
    this.wallPlacementDelay = wallPlacementDelay;
    this.wallTimer = 0;
    this.hunterPosAndVel = new PositionAndVelocity(new Point(0, 0), new Point(1,1));
    this.preyPos = new Point(230, 200);
    this.ticknum = 0;
    this.boardSize = new Point(300,300);
  }

  public String toString(){
    StringBuilder stringBuilder = new StringBuilder()
      .append(ticknum).append(" ")
      .append(maxWalls).append(" ")
      .append(wallPlacementDelay).append(" ")
      .append(boardSize.x).append(" ")
      .append(boardSize.y).append(" ")
      .append(wallTimer).append(" ")
      .append(hunterPosAndVel.pos.x).append(" ")
      .append(hunterPosAndVel.pos.y).append(" ")
      .append(hunterPosAndVel.vel.x).append(" ")
      .append(hunterPosAndVel.vel.y).append(" ")
      .append(preyPos.x).append(" ")
      .append(preyPos.y).append(" ")
      .append(walls.size());
    for(Wall wall : walls){
      stringBuilder.append(" ").append(wall.toString());
    }
    return stringBuilder.toString();
  }
}
