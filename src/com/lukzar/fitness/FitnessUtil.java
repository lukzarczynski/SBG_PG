package com.lukzar.fitness;

import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;

import java.util.Collection;

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
                .mapToDouble(p -> p.getEndPos().getY())
                .max().orElse(0);
    }

    public static double figureWidth(Piece svg) {
        return svg.getParts().stream()
                .mapToDouble(p -> p.getEndPos().getX())
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
                .map(Part::convertToLines)
                .flatMap(Collection::stream)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();

    }

    public static double distance(Point a, Point b) {
        double x1 = a.getX();
        double x2 = b.getX();
        double y1 = a.getY();
        double y2 = b.getY();
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
