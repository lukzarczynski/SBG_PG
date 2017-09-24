package com.lukzar.fitness;

import com.lukzar.config.Configuration;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.PolygonUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.lukzar.utils.PolygonUtils.distance;

/**
 * Created by lukasz on 16.07.17.
 */
public class FitnessUtil {

    public static double calculateFitness(Piece svg) {

        //util
        double fullHeight = Configuration.Piece.HEIGHT;
        double halfHeight = fullHeight / 2;
        double quarterHeight = fullHeight / 4;

        // attributes
        double height = figureHeight(svg);
        double width = figureWidth(svg);
        double areaUpperHalf = area(svg, 0, halfHeight);
        double areaLowerHalf = area(svg, halfHeight, height);
        double lengthOfLines = linesLength(svg);
        double lengthOfArcs = arcLength(svg);
        double lengthOfDoubleArcs = doubleArcLength(svg);
        double areaMiddle = area(svg, quarterHeight, halfHeight + quarterHeight);
        double boxLength = (2 * height) + (2 * width);

        // fitness
        double result = 0.0;
        result += normalize((height / width));
        result += normalize(1 - (lengthOfLines / boxLength));
        result += normalize(lengthOfArcs / boxLength);
        result += normalize(lengthOfDoubleArcs / boxLength);
        result += normalize(areaLowerHalf / (areaUpperHalf + 0.001));
        result += normalize(areaMiddle / area(svg));
        return result;
    }

    public static double normalize(double x) {

        double v = 1 / (1 + Math.exp(-5 * (x - 1)));
        if (Double.isNaN(v)) {
            return 0.0;
        }
        return v;
    }

    public static List<String> getAttributes(Piece piece) {

        double fullHeight = Configuration.Piece.HEIGHT;
        double halfHeight = fullHeight / 2;
        double quarterHeight = fullHeight / 4;

        double height = figureHeight(piece);
        double width = figureWidth(piece);

        return Arrays.asList(
                String.format("FITNESS: %.3f", calculateFitness(piece)),
                String.format("Double Arc Length: %.3f", doubleArcLength(piece)),
                String.format("Arc Length: %.3f", arcLength(piece)),
                String.format("Line Length: %.3f", linesLength(piece)),
                String.format("Box Length: %.3f", (2 * height) + (2 * width)),
                String.format("Area: %.3f", area(piece)),
                String.format("Top Area: %.3f", area(piece, 0, halfHeight)),
                String.format("Bottom Area: %.3f", area(piece, halfHeight, fullHeight)),
                String.format("Middle Area: %.3f", area(piece, quarterHeight, halfHeight + quarterHeight)),
                String.format("Min Degree: %.3f", getMinDegree(piece)),
                String.format("Height: %.3f", height),
                String.format("Width: %.3f", width)
        );
    }


    public static double figureHeight(Piece svg) {
        return Configuration.Piece.HEIGHT -
                DoubleStream.concat(
                        svg.getConverted().stream()
                                .mapToDouble(p -> p.getEndPos().getY()),
                        svg.getConverted().stream()
                                .mapToDouble(p -> p.getStartPos().getY()))
                        .min()
                        .orElse(Configuration.Piece.HEIGHT);
    }

    public static double figureWidth(Piece svg) {
        return 2.0 * (DoubleStream.concat(
                svg.getConverted().stream()
                        .mapToDouble(p -> p.getEndPos().getX()),
                svg.getConverted().stream()
                        .mapToDouble(p -> p.getStartPos().getX()))
                .max()
                .orElse(0) - (Configuration.Piece.WIDTH / 2.0));
    }

    /**
     * calculates length of lines
     */
    public static double linesLength(Piece svg) {
        return svg.getParts().stream()
                .filter(p -> p instanceof Line)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();
    }

    /**
     * calculates lenght of arcs
     */
    public static double arcLength(Piece svg) {
        return svg.getParts().stream()
                .filter(p -> p instanceof Arc)
                .map(Part::convertToLines)
                .flatMap(Collection::stream)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();

    }

    /**
     * calculates lenght of double arcs
     */
    public static double doubleArcLength(Piece svg) {
        return svg.getParts().stream()
                .filter(p -> p instanceof DoubleArc)
                .map(Part::convertToLines)
                .flatMap(Collection::stream)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();

    }

    public static double area(Piece piece) {
        List<Point> collect = piece.getConverted().stream().map(Line::getEndPos)
                .collect(Collectors.toList());
        collect.add(0, piece.getStart());
        collect.add(Point.of(Configuration.Piece.WIDTH / 2, piece.getStart().getY()));

        return PolygonUtils.calculateArea(collect);
    }

    /**
     * Calculates area of piece (only right half of piece) in between lines: <ul> <li>(0, min_y) -
     * (WIDTH, min_y)</li> <li>(0, max_y) - (WIDTH, max_y)</li> </ul>
     * <p>
     * For example: min_y = 0, max_y = HEIGHT will calculate whole piece area
     */
    public static double area(Piece piece, double min_y, double max_y) {
        List<Point> trimmed = PolygonUtils.trim(piece, min_y, max_y);
        return PolygonUtils.calculateArea(trimmed);
    }

    public static double getMinDegree(Piece piece) {
        return getArcs(piece).stream().mapToDouble(a -> a).min().orElse(0.0);
    }

    public static double getMaxDegree(Piece piece) {
        return getArcs(piece).stream().mapToDouble(a -> a).max().orElse(0.0);
    }

    /**
     * Returns list of arcs in degree for piece Note that arc are converted to lines so its very
     * likely that most of the arc will be close to 180 degrees
     */
    public static List<Double> getArcs(Piece piece) {
        Point start = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);
        Point end = Point.of(Configuration.Piece.WIDTH / 2.0, 0);
        LinkedList<Point> points = new LinkedList<>();
        points.addAll(piece.getConverted().stream().map(Line::getEndPos).collect(Collectors.toList()));
        points.addFirst(piece.getStart());
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
