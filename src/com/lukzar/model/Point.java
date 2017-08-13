package com.lukzar.model;

public class Point {

    private double x;
    private double y;

    public Point() {
    }

    public static Point of(double x, double y) {
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        return point;
    }

    public String toSvg() {
        return String.format("%f,%f", x, y);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        return Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        return "com.lukzar.model.Point(x=" + this.getX() + ", y=" + this.getY() + ")";
    }
}
