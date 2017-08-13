package com.lukzar.fitness;

import com.lukzar.config.Configuration;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.PolygonUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.lukzar.utils.PolygonUtils.distance;

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


    public static List<String> getAttributes(Piece piece) {
        return Arrays.asList(
                String.format("Arc Length: %.3f", arcLength(piece)),
                String.format("Line Length: %.3f", linesLength(piece)),
                String.format("Area: %.3f", area(piece)),
                String.format("Max Degree: %.3f", getMaxDegree(piece)),
                String.format("Min Degree: %.3f", getMinDegree(piece)),
                String.format("Height: %.3f", figureHeight(piece)),
                String.format("Width: %.3f", figureWidth(piece))
        );
    }

    public static double area(Piece piece) {
        List<Point> collect = piece.getConverted().stream().map(Line::getEndPos)
                .collect(Collectors.toList());
        collect.add(0, Configuration.Piece.START);
        collect.add(Point.of(Configuration.Piece.WIDTH / 2, Configuration.Piece.HEIGHT));

        return PolygonUtils.calculateArea(collect);
    }

    public static double getMinDegree(Piece piece) {
        return arcs(piece).stream().mapToDouble(a -> a).min().getAsDouble();
    }

    public static double getMaxDegree(Piece piece) {
        return arcs(piece).stream().mapToDouble(a -> a).max().getAsDouble();
    }

    public static List<Double> arcs(Piece piece) {
        Point start = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);
        Point end = Point.of(Configuration.Piece.WIDTH / 2.0, 0);
        LinkedList<Point> points = new LinkedList<>();
        points.addAll(piece.getConverted().stream().map(Line::getEndPos).collect(Collectors.toList()));
        points.addFirst(Configuration.Piece.START);
        points.addFirst(start);
        points.addLast(end);

        List<Double> result = new ArrayList<>();

        for (int i = 0; i < points.size() - 2; i++) {
            Point a = points.get(i);
            Point b = points.get(i + 1);
            Point c = points.get(i + 2);
            result.add(PolygonUtils.calculateArc(a, b, c));
        }

        return result;

    }
}
