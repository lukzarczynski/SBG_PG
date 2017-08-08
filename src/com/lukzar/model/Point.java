package com.lukzar.model;

import lombok.Data;

@Data
public class Point {

    private int x;
    private int y;

    public static Point of(int x, int y) {
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        return point;
    }

    public String toSvg() {
        return String.format("%d,%d", x, y);
    }

}
