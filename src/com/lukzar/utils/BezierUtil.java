/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.utils;

import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BezierUtil {

    private static List<Line> convertToLine(List<Point> bezier) {
        int parts = 20;

        double step = 1 / (double) parts;

        List<Point> points = new ArrayList<>();

        for (double i = 1; i <= parts; i++) {
            points.add(compute(i * step, bezier));
        }

        final List<Line> result = points.stream().map(Line::new).collect(Collectors.toList());
        Point s = bezier.get(0);
        for (Part p : result) {
            p.setStartPos(s);
            s = p.getEndPos();
        }
        return result;
    }

    public static List<Line> convertToLine(DoubleArc arc) {
        return convertToLine(Arrays.asList(arc.getStartPos(), arc.getQ1(), arc.getQ2(), arc.getEndPos()));
    }

    public static List<Line> convertToLine(Arc arc) {
        return convertToLine(Arrays.asList(arc.getStartPos(), arc.getQ(), arc.getEndPos()));
    }

    private static Point compute(double t, List<Point> bezier) {
        int n = bezier.size();
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = bezier.get(i).getX();
            y[i] = bezier.get(i).getY();
        }
        return Point.of(compute(t, x), compute(t, y)
        );

    }

    /**
     * Casteljau's algorithm
     */
    private static double compute(double t, double[] bezier) {
        int n = bezier.length;
        double[][] b = new double[n][n];
        System.arraycopy(bezier, 0, b[0], 0, n);

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                b[j][i] = b[j - 1][i] * (1 - t) + b[j - 1][i + 1] * t;
            }
        }
        return b[n - 1][0];
    }
}
