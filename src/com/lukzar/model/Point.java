package com.lukzar.model;

import lombok.Data;

@Data
public class Point {

    private double x;
    private double y;

    public static Point of(double x, double y) {
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        return point;
    }

    public String toSvg() {
        return String.format("%f,%f", x, y);
    }

}
