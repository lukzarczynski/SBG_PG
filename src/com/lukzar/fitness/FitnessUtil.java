package com.lukzar.fitness;

import com.lukzar.config.Configuration;
import com.lukzar.config.Feature;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.PolygonUtils;
import com.lukzar.utils.RayCasting;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.lukzar.fitness.FitnessAttribute.*;
import static com.lukzar.utils.PolygonUtils.distance;
import static java.util.Objects.nonNull;

/**
 * Created by lukasz on 16.07.17.
 */
public class FitnessUtil {

    private static double fullHeight = Configuration.Piece.HEIGHT;
    private static double fullWidth = Configuration.Piece.WIDTH;
    private static double fullArea = fullWidth * fullHeight;

    private static double halfWidth = fullWidth / 2;
    private static double quarterWidth = fullWidth / 4;
    private static Predicate<Point> middleXHalf = p -> p.getX() > quarterWidth && p.getX() < (halfWidth + quarterWidth);
    private static Predicate<Point> triangle = p -> {
        Point A = Point.of(0, Configuration.Piece.HEIGHT);
        Point B = Point.of(Configuration.Piece.WIDTH / 2.0, 0);
        Point C = Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT);
        return isInTriangle(A, B, C, p);
    };

    public static LinkedHashMap<FitnessAttribute, Object> getAttributes(Piece piece) {
        final List<Line> pieceAsLines = piece.getAsLines();
        final LinkedList<Part> pieceAllParts = piece.getAllParts();
        boolean[][] ray = RayCasting.castLines(pieceAsLines);
        boolean asymmetric = piece.isAsymmetric();
        final List<Double> arcs = getArcs(pieceAllParts);
        final DoubleSummaryStatistics doubleSummaryStatistics = getDoubleSummaryStatistics(pieceAsLines);

        double height = figureHeight(pieceAsLines);
        double width = figureWidth(pieceAsLines);

        double doubleArcLength = doubleArcLength(piece);
        double arcLength = arcLength(piece);
        double linesLength = linesLength(piece);
        double boxLength = (2 * height) + (2 * width);
        double lengthSum = doubleArcLength + arcLength + linesLength;


        double area = area(ray, p -> true);
        double upperHalfArea = area(ray, p -> p.getY() < Configuration.Piece.HEIGHT - height + 0.33 * height);
        double lowerHalfArea = area(ray, p -> p.getY() > Configuration.Piece.HEIGHT - 0.33 * height);
        double middleHalfArea = area - upperHalfArea - lowerHalfArea;
        double middleXHalfArea = area(ray, middleXHalf);
        double triangleArea = area(ray, triangle);
        final Point startPos = pieceAsLines.get(0).getStartPos();
        final Point endPos = pieceAsLines.get(pieceAsLines.size() - 1).getEndPos();
        final Point of = Point.of(Configuration.Piece.WIDTH / 2.0, 200 - height);
        double triangularity = area(ray, p -> isInTriangle(endPos, of, startPos, p));
        double innerHalfXArea = area(ray, p -> {
            double min = doubleSummaryStatistics.getMin();
            double max = doubleSummaryStatistics.getMax();
            double mid = (max + min) / 2;
            return p.getX() > mid - (width / 4) && p.getX() < mid + (width / 4);
        });
        double symmetryArea = getSymmetryArea(ray, piece);

        double baseWidth = area(ray, p -> p.getY() == 199);

        double minDegree = getMinDegree(arcs);
        double averageDegree = getAverageDegree(arcs);


        final List<Double> arcsMapped = arcs.stream().map(d -> d > 180 ? 360 - d : d).collect(Collectors.toList());
        double numberOfArcs = arcsMapped.size();
        double numberOfGentleArcs = arcsMapped.stream().filter(d -> d >= 120).count();
        double numberOfSharpArcs = arcsMapped.stream().filter(d -> d <= 60).count();
        double numberOfMediumArcs = numberOfArcs - numberOfSharpArcs - numberOfGentleArcs;


        LinkedHashMap<FitnessAttribute, Object> result = new LinkedHashMap<>();
        result.put(SHAPE_LENGTH, lengthSum);
        result.put(DOUBLE_ARC_LENGTH, doubleArcLength);
        result.put(ARC_LENGTH, arcLength);
        result.put(LINE_LENGTH, linesLength);
        result.put(BOX_LENGTH, boxLength);
        result.put(BASE_WIDTH, baseWidth);
        result.put(AREA, area);
        result.put(TOP_HALF_AREA, upperHalfArea);
        result.put(MID_Y_AREA, middleHalfArea);
        result.put(INNER_HALF_X_AREA, innerHalfXArea);
        result.put(BOTTOM_HALF_AREA, lowerHalfArea);
        result.put(MID_X_AREA, middleXHalfArea);
        result.put(TRIANGLE_BASE_AREA, triangleArea);
        result.put(TRIANGLE_PIECE_AREA, triangularity);
        result.put(MIN_DEGREE, minDegree);
        result.put(AVERAGE_DEGREE, averageDegree);
        result.put(HEIGHT, height);
        result.put(WIDTH, width);
        result.put(CENTROID, centroid(piece));
        result.put(SYMMETRIC, !asymmetric);
        result.put(SYMMETRY, symmetryArea);
        result.put(NUMBER_OF_ANGLES, numberOfArcs);
        result.put(NUMBER_OF_GENTLE_ANGLES, numberOfGentleArcs);
        result.put(NUMBER_OF_MEDIUM_ANGLES, numberOfMediumArcs);
        result.put(NUMBER_OF_SHARP_ANGLES, numberOfSharpArcs);

        return result;
    }

    public static double calculateFitness(Piece svg) {

        final Map<FitnessAttribute, Object> attributes = getAttributes(svg);

        double pieceHeight = (Double) attributes.get(HEIGHT);
        double pieceWidth = (Double) attributes.get(WIDTH);
        double pieceArea = (Double) attributes.get(AREA);
        double boxArea = pieceWidth * pieceHeight;
        double boxPerimeter = (Double) attributes.get(BOX_LENGTH);
        double topPieceArea = (Double) attributes.get(TOP_HALF_AREA);
        double bottomPieceArea = (Double) attributes.get(BOTTOM_HALF_AREA);
        double middlePieceArea = (Double) attributes.get(MID_Y_AREA);
        double innerhalfXArea = (Double) attributes.get(INNER_HALF_X_AREA);
        double piecePerimeter = (Double) attributes.get(SHAPE_LENGTH);
        double baseTriangleArea = (Double) attributes.get(TRIANGLE_BASE_AREA);
        double piecelikeTriangleArea = (Double) attributes.get(TRIANGLE_PIECE_AREA);
        double symmetry = (Double) attributes.get(SYMMETRY);
        double straightLineLength = (Double) attributes.get(LINE_LENGTH);
        double doublearcLineLength = (Double) attributes.get(DOUBLE_ARC_LENGTH);
        double arcLineLength = (Double) attributes.get(ARC_LENGTH);
        double numberofAngles = (Double) attributes.get(NUMBER_OF_ANGLES);
        double sharpAngles = (Double) attributes.get(NUMBER_OF_SHARP_ANGLES);
        double gentleAngles = (Double) attributes.get(NUMBER_OF_GENTLE_ANGLES);
        double averageDegree = (Double) attributes.get(AVERAGE_DEGREE);
        double minDegree = (Double) attributes.get(MIN_DEGREE);

        // Measures
        double widthRatio = pieceWidth / fullWidth;
        double heightRatio = pieceHeight / fullHeight;
        double areaRatio = pieceArea / fullArea;
        double topRatio = topPieceArea / pieceArea;
        double middleRatio = middlePieceArea / pieceArea;
        double symmetryRatio = symmetry / pieceArea;
        double innerhalfXRatio = innerhalfXArea / pieceArea;
        double baseTriangleAreaRatio = baseTriangleArea / pieceArea;
        double piecelikeTriangleAreaRatio = piecelikeTriangleArea / pieceArea;
        double perimeterRatio = piecePerimeter / (boxPerimeter * Configuration.PERIMETER_RATIO_MULTIPLIER);
        double straightLineRatio = straightLineLength / piecePerimeter;
        double curveLineRatio = 1 - straightLineRatio;
        double sharpAnglesRatio = sharpAngles / numberofAngles;
        double gentleAnglesRatio = gentleAngles / numberofAngles;
        Map<Feature, Double> measures = new HashMap<>();
        // comment this 'puts' to remove measure from fitness function
        measures.put(Feature.widthRatio, widthRatio);
        measures.put(Feature.heightRatio, heightRatio);
        measures.put(Feature.areaRatio, areaRatio);
        measures.put(Feature.topRatio, topRatio);
        measures.put(Feature.middleRatio, middleRatio);
        measures.put(Feature.symmetryRatio, symmetryRatio);
        measures.put(Feature.innerhalfXRatio,            innerhalfXRatio);
        measures.put(Feature.baseTriangleAreaRatio, baseTriangleAreaRatio);
        measures.put(Feature.piecelikeTriangleAreaRatio, piecelikeTriangleAreaRatio);
        measures.put(Feature.perimeterRatio,             perimeterRatio);
        measures.put(Feature.straightLineRatio, straightLineRatio);
        measures.put(Feature.curveLineRatio,             curveLineRatio);
        measures.put(Feature.sharpAnglesRatio, sharpAnglesRatio);
        measures.put(Feature.gentleAnglesRatio, gentleAnglesRatio);

        // Calculating fitness
        Map<Feature, Double> target = Configuration.getTargetFeatureValues().get(Configuration.TARGET_PIECE);
        double result = 0;
        for (Feature key : measures.keySet()) {
            result += 1 - Math.abs(measures.get(key) - target.get(key)); // todo - some better function here?
        }

        return result;
    }

    public static boolean isInTriangle(Point A, Point B, Point C, Point p) {

        double d1 = (p.getX() - A.getX()) * (B.getY() - A.getY()) -
                (p.getY() - A.getY()) * (B.getX() - A.getX());
        double d2 = (p.getX() - B.getX()) * (C.getY() - B.getY()) -
                (p.getY() - B.getY()) * (C.getX() - B.getX());

        return d1 <= 0 && d2 <= 0;
    }

    public static double normalize(double x) {

        double v = 1 / (1 + Math.exp(-5 * (x - 1)));
        if (Double.isNaN(v)) {
            return 0.0;
        }
        return v;
    }

    public static List<String> getAttributesDescription(Piece piece) {

        final LinkedHashMap<FitnessAttribute, Object> attributes = getAttributes(piece);
        final List<String> result = new ArrayList<>();
        attributes.forEach((k, v) -> result.add(k.getDescription(v, attributes)));

        return result;
    }

    private static double getSymmetryArea(boolean[][] ray, Piece svg) {
        double area = area(ray, p -> true);

        if (!svg.isAsymmetric()) {
            return area;
        }

        int counter = 0;

        for (boolean[] row : ray) {
            int left = 0;
            int right = ray.length - 1;
            while (left <= right) {
                if (row[left] && row[right]) {
                    counter += 2;
                }
                left++;
                right--;
            }
        }
        return counter;
    }

    private static Point centroid(Piece svg) {
        List<Line> converted = svg.getAsLines();
        List<Point> points = converted.stream().map(Part::getEndPos)
                .collect(Collectors.toList());

        points.add(0, converted.get(0).getStartPos());

        return Point.of(
                points.stream().mapToDouble(Point::getX).average().getAsDouble(),
                points.stream().mapToDouble(Point::getY).average().getAsDouble()
        );
    }

    private static double figureHeight(List<Line> lines) {
        return Configuration.Piece.HEIGHT -
                DoubleStream.concat(
                        lines.stream().mapToDouble(p -> p.getEndPos().getY()),
                        lines.stream().mapToDouble(p -> p.getStartPos().getY())
                )
                        .min()
                        .orElse(Configuration.Piece.HEIGHT);
    }

    private static double figureWidth(List<Line> lines) {
        final DoubleSummaryStatistics stats = getDoubleSummaryStatistics(lines);
        return stats.getMax() - stats.getMin();
    }

    private static DoubleSummaryStatistics getDoubleSummaryStatistics(List<Line> lines) {
        return DoubleStream.concat(
                lines.stream().mapToDouble(p -> p.getEndPos().getX()),
                lines.stream().mapToDouble(p -> p.getStartPos().getX())
        ).summaryStatistics();
    }

    private static double linesLength(Piece svg) {
        return (svg.isAsymmetric() ? 1.0 : 2.0)
                * svg.getParts().stream()
                .filter(p -> p instanceof Line)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();
    }

    private static double area(boolean[][] ray, Predicate<Point> predicate) {
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

    private static double arcLength(Piece svg) {
        return (svg.isAsymmetric() ? 1.0 : 2.0)
                * svg.getParts().stream()
                .filter(p -> p instanceof Arc)
                .map(Part::convertToLines)
                .flatMap(Collection::stream)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();

    }

    private static double doubleArcLength(Piece svg) {
        return (svg.isAsymmetric() ? 1.0 : 2.0)
                * svg.getParts().stream()
                .filter(p -> p instanceof DoubleArc)
                .map(Part::convertToLines)
                .flatMap(Collection::stream)
                .mapToDouble(p -> distance(p.getStartPos(), p.getEndPos()))
                .sum();

    }

    public static double getMinDegree(Piece piece) {
        return getMinDegree(getArcs(piece.getAllParts()));
    }

    private static double getMinDegree(List<Double> arcs) {
        return arcs.stream().mapToDouble(a -> a).min().orElse(0.0);
    }

    private static double getAverageDegree(List<Double> arcs) {
        return arcs.stream().mapToDouble(a -> a).average().orElse(0);
    }

    private static List<Double> getArcs(List<Part> allParts) {

        Point a = Point.of(0, Configuration.Piece.HEIGHT);
        Point b = null;
        Point c;

        List<Double> result = new ArrayList<>();

        for (Part part : allParts) {
            List<Line> lines = part.convertToLines();
            Line firstLine = lines.get(0);
            Line lastLine = lines.get(lines.size() - 1);

            b = firstLine.getStartPos();
            c = firstLine.getEndPos();
            double e = PolygonUtils.calculateArc(a, b, c);
            if (!Double.isNaN(e) && Double.isFinite(e)) {
                result.add(e);
            }

            a = lastLine.getStartPos();
            b = lastLine.getEndPos();
        }

        if (nonNull(b)) {
            double e = PolygonUtils.calculateArc(a, b, Point.of(Configuration.Piece.WIDTH, Configuration.Piece.HEIGHT));
            if (!Double.isNaN(e) && Double.isFinite(e)) {
                result.add(e);
            }
        }

        return result;

    }
}
