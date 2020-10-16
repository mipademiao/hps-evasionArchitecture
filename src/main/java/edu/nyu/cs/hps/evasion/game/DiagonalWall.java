package edu.nyu.cs.hps.evasion.game;

import java.awt.*;

public class DiagonalWall implements Wall {

    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int builddirection;
    
    DiagonalWall(int x1, int x2, int y1, int y2, int builddirection) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.builddirection = builddirection;
    }
    
    public boolean occupies (Point point) {
        if (builddirection == 1) {
            int offset = point.x - this.x1;
            return (this.y1 + offset == point.y || this.y1 + offset + 1 == point.y) && point.x >= this.x1 && point.x <= this.x2 && point.y >= this.y1 && point.y <= this.y2;
        }
        else {
            int offset = point.y - this.y1;
            return (this.x1 + offset == point.x || this.x1 + offset + 1 == point.x) && point.x >= this.x1 && point.x <= this.x2 && point.y >= this.y1 && point.y <= this.y2;
        }
    }
    
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder()
        .append("2").append(" ")
        .append(x1).append(" ")
        .append(x2).append(" ")
        .append(y1).append(" ")
        .append(y2).append(" ")
        .append(builddirection);
        return stringBuilder.toString();
    }
}

