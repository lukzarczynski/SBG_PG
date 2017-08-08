package com.lukzar.fitness;

import com.lukzar.utils.IntersectionUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;

/**
 * Created by lukasz on 16.07.17.
 */
public class FitnessUtil {

    public static double calculateFitness(Piece svg) {
        double result = 1.0;


        result += linesLength(svg);
        result += arcLength(svg);

        result *= heightWidthRatio(svg);

        return result;
    }

    public static double heightWidthRatio(Piece svg) {
        return figureHeight(svg) / figureWidth(svg);
    }

    public static double figureHeight(Piece svg) {
        return svg.getParts().stream()
                .mapToInt(p -> p.getEndPos().getY())
                .max().orElse(0);
    }

    public static double figureWidth(Piece svg) {
        return svg.getParts().stream()
                .mapToInt(p -> p.getEndPos().getX())
                .max().orElse(0);
    }

    public static double linesLength(Piece svg) {
        return svg.getParts().stream()
                .filter(p -> p instanceof Line)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();
    }

    public static double arcLength(Piece svg) {
        return svg.getParts().stream()
                .filter(p -> p instanceof Arc)
                .map(p -> (Arc) p)
                .mapToDouble(IntersectionUtil::bezier_length)
                .sum();

    }

    public static double distance(Point a, Point b) {
        int x1 = a.getX();
        int x2 = b.getX();
        int y1 = a.getY();
        int y2 = b.getY();
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
