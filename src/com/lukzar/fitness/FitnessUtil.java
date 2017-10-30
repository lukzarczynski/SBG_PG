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
import com.lukzar.utils.Timer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.lukzar.fitness.FitnessAttribute.*;
import static com.lukzar.utils.PolygonUtils.distance;

/**
 * Created by lukasz on 16.07.17.
 */
public class FitnessUtil {

    private static double fullArea = Configuration.Piece.WIDTH*Configuration.Piece.HEIGHT;

    private static double fullHeight = Configuration.Piece.HEIGHT;
    private static double halfHeight = fullHeight / 2;
    private static double quarterHeight = fullHeight / 4;
    private static Predicate<Point> upperHalf = p -> p.getY() < halfHeight;
    private static Predicate<Point> lowerHalf = p -> p.getY() >= halfHeight;
    private static Predicate<Point> middleHalf = p -> p.getY() > quarterHeight && p.getY() < (halfHeight + quarterHeight);
    private static Predicate<Point> middleXHalf = p -> p.getX() > quarterHeight && p.getX() < (halfHeight + quarterHeight);
    private static Predicate<Point> triangle = p -> {
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

        final Map<FitnessAttribute, Object> attributes = getAttributes(svg);


        double doubleArcLength = (Double) attributes.get(DOUBLE_ARC_LENGTH);
        double arcLength = (Double) attributes.get(ARC_LENGTH);
        double lineLength = (Double) attributes.get(LINE_LENGTH);
        double boxLength = (Double) attributes.get(BOX_LENGTH);
        double area = (Double) attributes.get(AREA);
        double topArea = (Double) attributes.get(TOP_HALF_AREA);
        double bottomArea = (Double) attributes.get(BOTTOM_HALF_AREA);
        double middleArea = (Double) attributes.get(MID_Y_AREA);
        double MinDegree = (Double) attributes.get(MIN_DEGREE);
        double height = (Double) attributes.get(HEIGHT);
        double width = (Double) attributes.get(WIDTH);
        boolean symmetric = (Boolean) attributes.get(SYMMETRIC);
        double averageDegree = (Double) attributes.get(AVERAGE_DEGREE);
        double minDegree = (Double) attributes.get(MIN_DEGREE);

        double middleXArea = (Double) attributes.get(MID_X_AREA);


        double topRatio = topArea / area;
        double bottomRatio = bottomArea / area;
        double midRatio = middleArea / area;
        double perimeter = doubleArcLength + arcLength + lineLength;
        double perimeterRatio = perimeter / boxLength;
        double narrowness = height / width;
        double lineRatio = lineLength / perimeter;
        double doubleArcRatio = doubleArcLength / perimeter;
        double arcRatio = arcLength / perimeter;
        double areaRatio = area / (width * height);

        double heightRatio = height / fullHeight;
        double widthRatio = width / Configuration.Piece.WIDTH;
        double areaFullRatio = area / fullArea;
        double midXRatio = middleXArea / area;
        double triangleBaseRatio = (Double) attributes.get(TRIANGLE_BASE_AREA) / area;
        double trianglePieceRatio = (Double) attributes.get(TRIANGLE_PIECE_AREA) / area;

        // fitness
        //double[] weights = Configuration.getFitnessWeights().get("pawn");
        double[] weights = Configuration.getFitnessWeights().get("queen");
        //double[] weights = Configuration.getFitnessWeights().get("king");
        //double[] weights = Configuration.getFitnessWeights().get("bishop");
        //double[] weights = Configuration.getFitnessWeights().get("rook");
        //double[] weights = Configuration.getFitnessWeights().get("knight");

        double result = 0.0;

        result += arcRatio * weights[0];
        //result += area * weights[1];
        result += areaRatio * weights[2];
        result += averageDegree * weights[3];
        result += bottomRatio * weights[4];
        result += doubleArcRatio * weights[5];
        //result += height * weights[6];
        result += heightRatio * weights[6];
        result += lineRatio * weights[7];
        result += midRatio * weights[8];
        result += minDegree * weights[9];
        result += narrowness * weights[10];
        result += perimeter * weights[11];
        result += perimeterRatio * weights[12];



        // we try to target pawn
        /*
        double targetArcRatio = 1.00;
        double targetLineRatio = 0.00;
        double targetAreaFullRatio = 0.119;
        double targetTopRatio = 0.019;// ??????????????????????????????????????
        double targetMidYRatio = 0.469;
        double targetMidXRatio = 0.999;
        double targetTriangleBaseRatio = 1.00;
        double targetTrianglePieceRatio = 0.732;
        double targetHeightRatio = 0.525;
        double targetWidthRatio = 0.45;
        //double targetMinDegree = xx;
        //double targetAvgDegree = xx;
        //*/
        // we try to target rook
        /*
        double targetArcRatio = 0.7127131608;
        double targetLineRatio = 0.287;
        double targetAreaFullRatio = 0.19;
        double targetTopRatio = 0.373;
        double targetMidYRatio = 0.468;
        double targetMidXRatio = 0.987;
        double targetTriangleBaseRatio = 0.920;
        double targetTrianglePieceRatio = 0.496;
        double targetHeightRatio = 0.625;
        double targetWidthRatio = 0.539;
        double targetMinDegree = 28.504;
        double targetAvgDegree = 97.625;
        //*/
        // we try to target queen
        /*
        double targetArcRatio = 1.00;
        double targetLineRatio = 0.00;
        double targetAreaFullRatio = 0.204;
        double targetTopRatio = 0.335;
        double targetMidYRatio = 0.443;
        double targetMidXRatio = 0.994;
        double targetTriangleBaseRatio = 0.981;
        double targetTrianglePieceRatio = 0.809;
        double targetHeightRatio = 0.900;
        double targetWidthRatio = 0.523;
        //double targetMinDegree = xx;
        //double targetAvgDegree = xx;
        //*/

        /*
        result = 0;
        result += 1 - Math.abs(arcRatio - targetArcRatio);
        result += 1 - Math.abs(lineRatio - targetLineRatio);
        // double arc is 1-arcRatio-lineRatio
        result += 1 - Math.abs(areaFullRatio - targetAreaFullRatio);
        result += 1 - Math.abs(topRatio - targetTopRatio);
        // Lower half is 1-topRatio
        result += 1 - Math.abs(midRatio - targetMidYRatio);
        result += 1 - Math.abs(midXRatio - targetMidXRatio);
        result += 1 - Math.abs(triangleBaseRatio - targetTriangleBaseRatio);
        result += 1 - Math.abs(trianglePieceRatio - targetTrianglePieceRatio);
        result += 1 - Math.abs(heightRatio - targetHeightRatio);
        result += 1 - Math.abs(widthRatio - targetWidthRatio);
        //result += 1 - Math.abs(minDegree - targetMinDegree)/180;
        //result += 1 - Math.abs(averageDegree - targetAvgDegree)/180;
        */

        return result;
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

    public static LinkedHashMap<FitnessAttribute, Object> getAttributes(Piece piece) {
        Timer t = Timer.start();

        final List<Line> pieceAsLines = piece.getAsLines();
        final LinkedList<Part> pieceAllParts = piece.getAllParts();

        boolean[][] ray = RayCasting.castLines(pieceAsLines);
        boolean asymmetric = piece.isAsymmetric();


        double height = figureHeight(pieceAsLines);
        double width = figureWidth(pieceAsLines);

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
        final Point startPos = pieceAsLines.get(0).getStartPos();
        final Point endPos = pieceAsLines.get(pieceAsLines.size() - 1).getEndPos();
        final Point of = Point.of(Configuration.Piece.WIDTH / 2.0, 200 - height);
        double triangularity = area(ray, p -> isInTriangle(endPos, of, startPos, p));

        double baseWidth = area(ray, p -> p.getY() == 199);

        final List<Double> arcs = getArcs(pieceAllParts);
        double minDegree = getMinDegree(arcs);
        double averageDegree = getAverageDegree(arcs);

        double lengthSum = doubleArcLength + arcLength + linesLength;

        LinkedHashMap<FitnessAttribute, Object> result = new LinkedHashMap<>();
        result.put(SHAPE_LENGTH, lengthSum);
        result.put(DOUBLE_ARC_LENGTH, doubleArcLength);
        result.put(ARC_LENGTH, arcLength);
        result.put(LINE_LENGTH, linesLength);
        result.put(BOX_LENGTH, boxLength);
        result.put(BASE_WIDTH, baseWidth);
        result.put(AREA, area);
        result.put(TOP_HALF_AREA, upperHalfArea);
        result.put(BOTTOM_HALF_AREA, lowerHalfArea);
        result.put(MID_Y_AREA, middleHalfArea);
        result.put(MID_X_AREA, middleXHalfArea);
        result.put(TRIANGLE_BASE_AREA, triangleArea);
        result.put(TRIANGLE_PIECE_AREA, triangularity);
        result.put(MIN_DEGREE, minDegree);
        result.put(AVERAGE_DEGREE, averageDegree);
        result.put(HEIGHT, height);
        result.put(WIDTH, width);
        result.put(CENTROID, centroid(piece));
        result.put(SYMMETRIC, !asymmetric);


        t.end("Attributes");
        return result;
    }

    public static Point centroid(Piece svg) {
        List<Line> converted = svg.getAsLines();
        List<Point> points = converted.stream().map(Part::getEndPos)
                .collect(Collectors.toList());

        points.add(0, converted.get(0).getStartPos());

        return Point.of(
                points.stream().mapToDouble(Point::getX).average().getAsDouble(),
                points.stream().mapToDouble(Point::getY).average().getAsDouble()
        );
    }

    public static double figureHeight(List<Line> lines) {
        return Configuration.Piece.HEIGHT -
                DoubleStream.concat(
                        lines.stream().mapToDouble(p -> p.getEndPos().getY()),
                        lines.stream().mapToDouble(p -> p.getStartPos().getY())
                )
                        .min()
                        .orElse(Configuration.Piece.HEIGHT);
    }

    public static double figureWidth(List<Line> lines) {
        final DoubleSummaryStatistics stats = DoubleStream.concat(
                lines.stream().mapToDouble(p -> p.getEndPos().getX()),
                lines.stream().mapToDouble(p -> p.getStartPos().getX())
        ).summaryStatistics();
        return stats.getMax() - stats.getMin();
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

    public static double getMinDegree(Piece piece) {
        return getMinDegree(getArcs(piece.getAllParts()));
    }

    public static double getMinDegree(List<Double> arcs) {
        return arcs.stream().mapToDouble(a -> a).min().orElse(0.0);
    }

    public static double getMaxDegree(List<Double> arcs) {
        return arcs.stream().mapToDouble(a -> a).max().orElse(0.0);
    }

    public static double getAverageDegree(List<Double> arcs) {
        return arcs.stream().mapToDouble(a -> a).average().getAsDouble();
    }

    /**
     * Returns list of arcs in degree for piece Note that arc are converted to lines so its very
     * likely that most of the arc will be close to 180 degrees
     */
    public static List<Double> getArcs(List<Part> allParts) {

        Point a = Point.of(Configuration.Piece.WIDTH * 2, Configuration.Piece.HEIGHT);
        Point b;
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
        }

        return result;

    }
}
