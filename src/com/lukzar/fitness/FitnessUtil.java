package com.lukzar.fitness;

import com.lukzar.config.Configuration;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.PolygonUtils;
import com.lukzar.utils.RayCasting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
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
        boolean[][] ray = RayCasting.cast(svg);
        Predicate<Point> upperHalf = p -> p.getY() < halfHeight;
        Predicate<Point> lowerHalf = p -> p.getY() >= halfHeight;
        Predicate<Point> quarterHalf = p -> p.getY() > quarterHeight && p.getY() < (halfHeight + quarterHeight);

        double a = area(ray, upperHalf);
        double b = area(ray, lowerHalf);
        double c = area(ray, quarterHalf);
        double d = area(ray, p -> true);

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
                String.format("Width: %.3f", width),
                String.format("Symmetric: %s", !piece.isAsymmetric()),
                String.format("Average degree: %.3f", getAverageDegree(piece))
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
        return (svg.isAsymmetric() ? 1.0 : 2.0)
                * svg.getParts().stream()
                .filter(p -> p instanceof Line)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();
    }

    /**
     * area in percent (0..1)
     */
    public static double area(boolean[][] ray, Predicate<Point> predicate) {

        int height = ray.length;
        int width = ray[0].length;
        int counter = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (ray[row][col] && predicate.test(Point.of(col, row))) {
                    counter++;
                }
            }
        }

        return counter;
    }

    /**
     * calculates lenght of arcs
     */
    public static double arcLength(Piece svg) {
        return (svg.isAsymmetric() ? 1.0 : 2.0)
                * svg.getParts().stream()
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
        return (svg.isAsymmetric() ? 1.0 : 2.0)
                * svg.getParts().stream()
                .filter(p -> p instanceof DoubleArc)
                .map(Part::convertToLines)
                .flatMap(Collection::stream)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();

    }

    public static double area(Piece piece) {
        return area(piece, 0, Configuration.Piece.HEIGHT);
    }

    /**
     * Calculates area of piece (only right half of piece) in between lines: <ul> <li>(0, min_y) -
     * (WIDTH, min_y)</li> <li>(0, max_y) - (WIDTH, max_y)</li> </ul> <p> For example: min_y = 0,
     * max_y = HEIGHT will calculate whole piece area
     */
    public static double area(Piece piece, double min_y, double max_y) {
        List<Point> trimmed = PolygonUtils.trim(piece, min_y, max_y);
        return (piece.isAsymmetric() ? 1.0 : 2.0)
                * PolygonUtils.calculateArea(trimmed);
    }

    public static double getMinDegree(Piece piece) {
        return getArcs(piece).stream().mapToDouble(a -> a).min().orElse(0.0);
    }

    public static double getMaxDegree(Piece piece) {
        return getArcs(piece).stream().mapToDouble(a -> a).max().orElse(0.0);
    }

    public static double getAverageDegree(Piece piece) {
        List<Double> arcs = getArcs(piece);
        return arcs.stream().mapToDouble(a -> a).average().getAsDouble();
    }

    /**
     * Returns list of arcs in degree for piece Note that arc are converted to lines so its very
     * likely that most of the arc will be close to 180 degrees
     */
    public static List<Double> getArcs(Piece piece) {

        Point a = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);
        Point b;
        Point c;

        List<Double> result = new ArrayList<>();

        for (Part part : piece.getParts()) {
            List<Line> lines = part.convertToLines();
            Line firstLine = lines.get(0);
            Line lastLine = lines.get(lines.size() - 1);

            b = firstLine.getStartPos();
            c = firstLine.getEndPos();
            result.add(PolygonUtils.calculateArc(a, b, c));

            a = lastLine.getStartPos();
        }

        return result;

    }
}
