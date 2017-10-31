package com.lukzar.model;

import java.util.Locale;

public class Point {
    private static final int PRECISION = 10;
    private final double x;
    private final double y;

    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(double x, double y) {
        return new Point(x, y);
    }

    public String toSvg() {
        return String.format(Locale.US, "%.3f,%.3f", x, y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        int ix = (int) (this.x * PRECISION);
        int iy = (int) (this.x * PRECISION);
        int ix1 = (int) (point.x * PRECISION);
        int iy1 = (int) (point.x * PRECISION);

        return Integer.compare(ix, ix1) == 0
                && Integer.compare(iy, iy1) == 0;
    }

    @Override
    public int hashCode() {

        int ix = (int) (this.x * PRECISION);
        int iy = (int) (this.x * PRECISION);

        int result = ix;
        result = 31 * result + iy;
        return result;
    }

    @Override
    public String toString() {
        return toSvg();
    }
}
