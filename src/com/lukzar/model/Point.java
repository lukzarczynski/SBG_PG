package com.lukzar.model;

import java.util.Locale;

public class Point {
    private final int x;
    private final int y;

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(double x, double y) {
        return new Point((int) x, (int) y);
    }

    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    public String toSvg() {
        return String.format(Locale.US, "%d,%d", x, y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return Integer.compare(this.x, point.x) == 0
                && Integer.compare(this.y, point.y) == 0;
    }

    @Override
    public int hashCode() {

        int result = this.x;
        result = 31 * result + this.y;
        return result;
    }

    @Override
    public String toString() {
        return "(" + toSvg() + ")";
    }
}
