package edu.nyu.cs.hps.evasion.game;

import java.awt.*;

public class PositionAndVelocity {
  public Point pos;
  public Point vel;

  public PositionAndVelocity(PositionAndVelocity positionAndVelocity){
    pos = new Point(positionAndVelocity.pos);
    vel = new Point(positionAndVelocity.vel);
  }

  public PositionAndVelocity(Point pos, Point vel){
    this.pos = new Point(pos);
    this.vel = new Point(vel);
  }
}