package com.lukzar.fitness;

import com.lukzar.config.Configuration;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.PolygonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lukzar.utils.PolygonUtils.distance;

/**
 * Created by lukasz on 16.07.17.
 */
public class FitnessUtil {

    public static double calculateFitness(Piece svg) {
        double result = 0.0;

        double height = figureHeight(svg);
        double width = figureWidth(svg);

        result += normalize(height / width);
        result += normalize(1 - (linesLength(svg) / ((2 * height) + (2 * width))));
        result += normalize(arcLength(svg) / ((2 * height) + (2 * width)));
        result += normalize(area(svg, 0, Configuration.Piece.HEIGHT / 2)
                / area(svg, Configuration.Piece.HEIGHT / 2, Configuration.Piece.HEIGHT));

        double middle = area(svg, Configuration.Piece.HEIGHT / 4,
                (Configuration.Piece.HEIGHT / 2) + (Configuration.Piece.HEIGHT / 4));
        result += normalize(middle / area(svg));

        return result;
    }

    public static double normalize(double x) {
        return 1 / (1 + Math.exp(-5 * (x - 1)));
    }

    public static double figureHeight(Piece svg) {
        return svg.getParts().stream()
                .mapToDouble(p -> p.getEndPos().getY())
                .max().orElse(0);
    }

    public static double figureWidth(Piece svg) {
        return svg.getParts().stream()
                .mapToDouble(p -> p.getEndPos().getX())
                .map(d -> d - (Configuration.Piece.WIDTH / 2.0))
                .max()
                .orElse(0);
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
     *
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

    public static List<String> getAttributes(Piece piece) {
        return Arrays.asList(
                String.format("FITNESS: %.3f", calculateFitness(piece)),
                String.format("Arc Length: %.3f", arcLength(piece)),
                String.format("Line Length: %.3f", linesLength(piece)),
                String.format("Area: %.3f", area(piece)),
                String.format("Top Area: %.3f", area(piece, 0, Configuration.Piece.HEIGHT / 2)),
                String.format("Bottom Area: %.3f", area(piece, Configuration.Piece.HEIGHT / 2, Configuration.Piece.HEIGHT)),
                String.format("Middle Area: %.3f", area(piece, Configuration.Piece.HEIGHT / 4,
                        (Configuration.Piece.HEIGHT / 2) + (Configuration.Piece.HEIGHT / 4))),
                String.format("Min Degree: %.3f", getMinDegree(piece)),
                String.format("Height: %.3f", figureHeight(piece)),
                String.format("Width: %.3f", figureWidth(piece))
        );
    }
}
