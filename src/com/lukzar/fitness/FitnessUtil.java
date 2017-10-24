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
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.lukzar.utils.PolygonUtils.distance;

/**
 * Created by lukasz on 16.07.17.
 */
public class FitnessUtil {

    static double fullHeight = Configuration.Piece.HEIGHT;
    static double halfHeight = fullHeight / 2;
    static double quarterHeight = fullHeight / 4;
    static Predicate<Point> upperHalf = p -> p.getY() < halfHeight;
    static Predicate<Point> lowerHalf = p -> p.getY() >= halfHeight;
    static Predicate<Point> middleHalf = p ->
            p.getY() > quarterHeight && p.getY() < (halfHeight + quarterHeight);
    static Predicate<Point> middleXHalf =
            p -> p.getX() > quarterHeight && p.getX() < (halfHeight + quarterHeight);
    static Predicate<Point> triangle =
            p -> {
                Point A = Point.of(0, Configuration.Piece.HEIGHT);
                Point B = Point.of(Configuration.Piece.WIDTH / 2.0, 0);
                Point C = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);
                return isInTriangle(A, B, C, p);
            };

    public static boolean isInTriangle(Point A, Point B, Point C, Point p) {

        double d1 = (p.getX() - A.getX()) * (B.getY() - A.getY()) -
                (p.getY() - A.getY()) * (B.getX() - A.getX());
        double d2 = (p.getX() - B.getX()) * (C.getY() - B.getY()) -
                (p.getY() - B.getY()) * (C.getX() - B.getX());

        return d1 <= 0 && d2 <= 0;
    }

    public static double calculateFitness(Piece svg) {

        //util
//        boolean[][] ray = RayCasting.cast(svg);

//        double fullImageArea = area(ray, p -> true);
//        double upperHalfArea = area(ray, upperHalf);
//        double lowerHalfArea = area(ray, lowerHalf);
//        double middleHalfArea = area(ray, middleHalf);
//        double middleXHalfArea = area(ray, middleXHalf);
//        double triangleArea = area(ray, triangle);
//        double baseWidth = area(ray, p -> p.getY() == 199);

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
        double area = area(svg);

        // fitness
        double result = 0.0;
//        result += normalize((height / width));
//        result += normalize(1 - (lengthOfLines / boxLength));
//        result += normalize(lengthOfArcs / boxLength);
//        result += normalize(lengthOfDoubleArcs / boxLength);
//        result += normalize(areaLowerHalf / (areaUpperHalf + 0.001));
        result += areaUpperHalf / area;
        result += 1.5 - (areaLowerHalf / area);
        result += 0.5 * (area / (200*200));
//        result += normalize(areaMiddle / area);
//        result += normalize(1 - (areaLowerHalf / area));
//        result += normalize(1 - (lengthOfLines / boxLength));
//        result += normalize(lengthOfArcs / boxLength);

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
        boolean asymmetric = piece.isAsymmetric();

        boolean[][] ray = RayCasting.cast(piece);

        double fullHeight = Configuration.Piece.HEIGHT;
        double halfHeight = fullHeight / 2;
        double quarterHeight = fullHeight / 4;

        double height = figureHeight(piece);
        double width = figureWidth(piece);

        double doubleArcLength = doubleArcLength(piece);
        double arcLength = arcLength(piece);
        double linesLength = linesLength(piece);
        double boxLength = (2 * height) + (2 * width);

        double area = area(ray, p -> true);
        double upperHalfArea = area(ray, upperHalf);
        double lowerHalfArea = area(ray, lowerHalf);
        double middleHalfArea = area(ray, middleHalf);
        double middleXHalfArea = area(ray, middleXHalf);
        double triangleArea = area(ray, triangle);
        double triangularity = area(ray, p -> {
            List<Line> converted = piece.getAsLines();
            Point startPos = converted.get(0).getStartPos();
            Point endPos = converted.get(converted.size() - 1).getEndPos();
            return isInTriangle(
                    endPos,
                    Point.of(Configuration.Piece.WIDTH / 2.0, 200 - height),
                    startPos,
                    p
            );
        });
        double baseWidth = area(ray, p -> p.getY() == 199);

        double minDegree = getMinDegree(piece);
        double averageDegree = getAverageDegree(piece);

        double lengthSum = doubleArcLength + arcLength + linesLength;
        return Arrays.asList(
//                String.format("FITNESS: %.3f", calculateFitness(piece)),
                String.format("Shape Length: %.3f ( 100 %% )", lengthSum),
                String.format("Double Arc Length: %.3f ( %s )", doubleArcLength, percent(doubleArcLength, lengthSum)),
                String.format("Arc Length: %.3f ( %s )", arcLength, percent(arcLength, lengthSum)),
                String.format("Line Length: %.3f ( %s )", linesLength, percent(linesLength, lengthSum)),
                String.format("Box Length: %.3f", boxLength),
                String.format("Base width Length: %.3f", baseWidth),
                String.format("Area: %.3f ( 100%%, %s of total area )", area, percent(area, 200*200)),
                String.format("Upper Half Area: %.3f ( %s )", upperHalfArea, percent(upperHalfArea, area)),
                String.format("Lower Half Area: %.3f ( %s )", lowerHalfArea, percent(lowerHalfArea, area)),
                String.format("Middle Half over Y Area: %.3f ( %s )", middleHalfArea, percent(middleHalfArea, area)),
                String.format("Middle Half over X Area: %.3f ( %s )", middleXHalfArea, percent(middleXHalfArea, area)),
                String.format("Triangle Area (BASE): %.3f ( %s )", triangleArea, percent(triangleArea, area)),
                String.format("Triangle Area (PIECE): %.3f ( %s )", triangularity, percent(triangularity, area)),
                String.format("Min Degree: %.3f", minDegree),
                String.format("Height: %.3f ( %s )", height, percent(height, 200)),
                String.format("Width: %.3f ( %s )", width, percent(width, 200)),
                String.format("Centroid: ( %s )", centroid(piece).toSvg()),
                String.format("Symmetric: %s", !asymmetric),
                String.format("Average degree: %.3f", averageDegree)
        );
    }

    public static Point centroid(Piece svg) {
        List<Line> converted = svg.getAsLines();
        List<Point> points = converted.stream().map(Part::getEndPos)
                .collect(Collectors.toList());

        points.add(0, converted.get(0).getStartPos());
//
//        double vx = 0, vy = 0, A = 0;
//        for (int i = 0; i < points.size() - 2; i++) {
//            Point a = points.get(i);
//            Point b = points.get(i + 1);
//
//            double xi = a.getX();
//            double yi = a.getY();
//            double xi1 = b.getX();
//            double yi1 = b.getY();
//            vx += (xi + xi1) * ((xi * yi1) - (xi1 * yi));
//            vy += (yi + yi1) * ((xi * yi1) - (xi1 * yi));
//            A += (xi * yi1) - (xi1 * yi);
//        }
//        A = A / 2;

        return Point.of(
                points.stream().mapToDouble(Point::getX).average().getAsDouble(),
                points.stream().mapToDouble(Point::getY).average().getAsDouble()
        );
    }

    public static String percent(double v, double max) {
        return String.format("%.1f %%", (v / max * 100));
    }


    public static double figureHeight(Piece svg) {
        return Configuration.Piece.HEIGHT -
                DoubleStream.concat(
                        svg.getAsLines().stream()
                                .mapToDouble(p -> p.getEndPos().getY()),
                        svg.getAsLines().stream()
                                .mapToDouble(p -> p.getStartPos().getY()))
                        .min()
                        .orElse(Configuration.Piece.HEIGHT);
    }

    public static double figureWidth(Piece svg) {
        return 2.0 * (DoubleStream.concat(
                svg.getAsLines().stream()
                        .mapToDouble(p -> p.getEndPos().getX()),
                svg.getAsLines().stream()
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
