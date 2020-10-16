package edu.nyu.cs.hps.evasion.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {

  private GameState state;

  public enum WallCreationType {
    NONE,
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
    COUNTERDIAGONAL
  }

  public Game(int maxWalls, int wallPlacementDelay){
    state = new GameState(maxWalls, wallPlacementDelay);
  }

  public boolean tick(WallCreationType hunterWallAction, List<Integer> hunterWallsToDelete, Point preyMovement){
    removeWalls(hunterWallsToDelete);
    Point prevHunterPos = new Point(state.hunterPosAndVel.pos);
    state.hunterPosAndVel = move(state.hunterPosAndVel);
    doBuildAction(prevHunterPos, hunterWallAction);
    if(canPreyMove()) {
      state.preyPos = move(new PositionAndVelocity(state.preyPos, preyMovement)).pos;
    }
    state.ticknum++;
    if(state.wallTimer > 0){
      state.wallTimer--;
    }
    return captured();
  }

  private boolean isOccupied(Point p) {
    if(p.x < 0 || p.x >= state.boardSize.x || p.y < 0 || p.y >= state.boardSize.y){
      return true;
    }
    for(Wall wall : state.walls){
      if(wall.occupies(p)){
        return true;
      }
    }
    return false;
  }

  private boolean addWall(Wall wall){
    if(state.walls.size() < state.maxWalls && state.wallTimer <= 0){
      state.walls.add(wall);
      state.wallTimer = state.wallPlacementDelay;
      return true;
    } else {
      return false;
    }
  }

  private void removeWalls(List<Integer> indexList){
    List<Wall> newWalls = new ArrayList<>();
    for(int i = 0; i < state.walls.size(); ++i){
      if(!indexList.contains(i)){
        newWalls.add(state.walls.get(i));
      }
    }
    state.walls = newWalls;
  }

  private boolean captured(){
    if(state.hunterPosAndVel.pos.distance(state.preyPos) <= 4.0){
      List<Point> pts = BresenhamsAlgorithm.pointsBetween(state.hunterPosAndVel.pos, state.preyPos);
      for(Point pt : pts){
        if(isOccupied(pt)){
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  private boolean canPreyMove(){
    return (state.ticknum % 2) != 0;
  }

  private boolean doBuildAction(Point pos, WallCreationType action){
    if(action == WallCreationType.HORIZONTAL){
      Point greater = new Point(pos);
      Point lesser = new Point(pos);
      while(!isOccupied(greater)){
        if(greater.equals(state.hunterPosAndVel.pos) || greater.equals(state.preyPos)){
          return false;
        }
        greater.x++;
      }
      while(!isOccupied(lesser)){
        if(lesser.equals(state.hunterPosAndVel.pos) || lesser.equals(state.preyPos)){
          return false;
        }
        lesser.x--;
      }
      HorizontalWall horizontalWall = new HorizontalWall(pos.y, lesser.x+1, greater.x-1);
      return addWall(horizontalWall);
    } else if(action == WallCreationType.VERTICAL){
      Point greater = new Point(pos);
      Point lesser = new Point(pos);
      while(!isOccupied(greater)){
        if(greater.equals(state.hunterPosAndVel.pos) || greater.equals(state.preyPos)){
          return false;
        }
        greater.y++;
      }
      while(!isOccupied(lesser)){
        if(lesser.equals(state.hunterPosAndVel.pos) || lesser.equals(state.preyPos)){
          return false;
        }
        lesser.y--;
      }
      VerticalWall verticalWall = new VerticalWall(pos.x, lesser.y+1, greater.y-1);
      return addWall(verticalWall);
    }
    else if (action == WallCreationType.DIAGONAL) {
        Point greater = new Point(pos);
        Point lesser = new Point(pos);
        int count = 0;
        int builddirection = 0;
        while(!isOccupied(greater)){
            if(greater.equals(state.hunterPosAndVel.pos) || greater.equals(state.preyPos)){
                return false;
            }
            if (count % 2 == 0) {
                greater.y++;
            }
            else {
                greater.x++;
            }
            count++;
        }
        if (count % 2 == 0) {
            greater.x--;
        }
        else {
            greater.y--;
        }
        count = 0;
        while(!isOccupied(lesser)){
            if(lesser.equals(state.hunterPosAndVel.pos) || lesser.equals(state.preyPos)){
                return false;
            }
            if (count % 2 == 0) {
                lesser.x--;
            }
            else {
                lesser.y--;
            }
            count++;
        }
        if (count % 2 == 0) {
            lesser.y++;
            builddirection = 0; //this means we start building by moving in the x direction
        }
        else {
            lesser.x++;
            builddirection = 1; //this means we start building by moving in the y direction
        }
        DiagonalWall diagonalWall = new DiagonalWall(lesser.x, greater.x, lesser.y, greater.y, builddirection);
        return addWall(diagonalWall);
    }
    else if (action == WallCreationType.COUNTERDIAGONAL) {
        Point greater = new Point(pos);
        Point lesser = new Point(pos);
        int count = 0;
        int builddirection = 0;
        while(!isOccupied(greater)){
            if(greater.equals(state.hunterPosAndVel.pos) || greater.equals(state.preyPos)){
                return false;
            }
            if (count % 2 == 0) {
                greater.y--;
            }
            else {
                greater.x++;
            }
            count++;
        }
        if (count % 2 == 0) {
            greater.x--;
        }
        else {
            greater.y++;
        }
        count = 0;
        while(!isOccupied(lesser)){
            if(lesser.equals(state.hunterPosAndVel.pos) || lesser.equals(state.preyPos)){
                return false;
            }
            if (count % 2 == 0) {
                lesser.x--;
            }
            else {
                lesser.y++;
            }
            count++;
        }
        if (count % 2 == 0) {
            lesser.y--;
            builddirection = 0; //this means we start building by moving in the x direction
        }
        else {
            lesser.x++;
            builddirection = 1; //this means we start building by moving in the y direction
        }
        CounterDiagonalWall counterDiagonalWall = new CounterDiagonalWall(lesser.x, greater.x, lesser.y, greater.y, builddirection);
        return addWall(counterDiagonalWall);
    }
    return false;
  }

  private PositionAndVelocity move(PositionAndVelocity posAndVel){
    PositionAndVelocity newPosAndVel = new PositionAndVelocity(posAndVel);
    newPosAndVel.vel.x = Math.min(Math.max(newPosAndVel.vel.x, -1), 1);
    newPosAndVel.vel.y = Math.min(Math.max(newPosAndVel.vel.y, -1), 1);
    Point target = add(newPosAndVel.pos, newPosAndVel.vel);
    if(!isOccupied(target)){
      newPosAndVel.pos = target;
    } else {
      if(newPosAndVel.vel.x == 0 || newPosAndVel.vel.y == 0){
        if(newPosAndVel.vel.x != 0){
          newPosAndVel.vel.x = -newPosAndVel.vel.x;
        } else {
          newPosAndVel.vel.y = -newPosAndVel.vel.y;
        }
      } else {
        boolean oneRight = isOccupied(add(newPosAndVel.pos, new Point(newPosAndVel.vel.x, 0)));
        boolean oneUp = isOccupied(add(newPosAndVel.pos, new Point(0, newPosAndVel.vel.y)));
        if (oneRight && oneUp) {
          newPosAndVel.vel.x = -newPosAndVel.vel.x;
          newPosAndVel.vel.y = -newPosAndVel.vel.y;
        } else if (oneRight) {
          newPosAndVel.vel.x = -newPosAndVel.vel.x;
          newPosAndVel.pos.y = target.y;
        } else if (oneUp) {
          newPosAndVel.vel.y = -newPosAndVel.vel.y;
          newPosAndVel.pos.x = target.x;
        } else {
          boolean twoUpOneRight = isOccupied(add(newPosAndVel.pos, new Point(newPosAndVel.vel.x, newPosAndVel.vel.y * 2)));
          boolean oneUpTwoRight = isOccupied(add(newPosAndVel.pos, new Point(newPosAndVel.vel.x * 2, newPosAndVel.vel.y)));
          if ((twoUpOneRight && oneUpTwoRight) || (!twoUpOneRight && !oneUpTwoRight)) {
            newPosAndVel.vel.x = -newPosAndVel.vel.x;
            newPosAndVel.vel.y = -newPosAndVel.vel.y;
          } else if (twoUpOneRight) {
            newPosAndVel.vel.x = -newPosAndVel.vel.x;
            newPosAndVel.pos.y = target.y;
          } else {
            newPosAndVel.vel.y = -newPosAndVel.vel.y;
            newPosAndVel.pos.x = target.x;
          }
        }
      }
    }
    return newPosAndVel;
  }

  private static Point add(Point a, Point b){
    return new Point(a.x + b.x, a.y + b.y);
  }

  public GameState getState() {
    return state;
  }
}
