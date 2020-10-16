package edu.nyu.cs.hps.evasion.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BresenhamsAlgorithm {

  public static List<Point> pointsBetween(Point p0, Point p1){

    int x0 = p0.x;
    int x1 = p1.x;
    int y0 = p0.y;
    int y1 = p1.y;

    List<Point> points = new ArrayList<>();
    boolean steep = Math.abs(y1-y0) > Math.abs(x1-x0);

    if(steep){
      int tx0 = x0;
      x0 = y0;
      y0 = tx0;

      int tx1 = x1;
      x1 = y1;
      y1 = tx1;
    }
    if(x0 > x1){
      int tx0 = x0;
      x0 = x1;
      x1 = tx0;

      int ty0 = y0;
      y0 = y1;
      y1 = ty0;
    }

    int deltax = x1-x0;
    int deltay = Math.abs(y1-y0);
    int error = deltax / 2;
    int y = y0;
    int ystep;
    if(y0 < y1){
      ystep = 1;
    } else {
      ystep = -1;
    }

    for(int x = x0; x <= x1; x++){
      if(steep){
        points.add(new Point(y,x));
      } else {
        points.add(new Point(x,y));
      }
      error -= deltay;
      if(error < 0){
        y += ystep;
        error += deltax;
      }
    }

    return points;
  }

}
