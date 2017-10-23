package com.lukzar.services;

import com.lukzar.config.Configuration;
import com.lukzar.exceptions.IntersectsException;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.PolygonUtils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.lukzar.utils.RandomUtils.randomRange;

/**
 * Created by lukasz on 08.07.17.
 */
public class PieceGenerator {


    public static Piece generate() throws IntersectsException {
        int numberOfParts = (int) randomRange(
                Configuration.PieceGeneration.MIN_PARTS,
                Configuration.PieceGeneration.MAX_PARTS);

        final Piece svg = new Piece(
                Point.of(randomRange(
                        Configuration.PieceGeneration.START_MIN,
                        Configuration.PieceGeneration.START_MAX),
                        Configuration.Piece.HEIGHT));

        while (svg.getParts().size() < numberOfParts - 1) {
            svg.add(generatePart(svg));
        }
        svg.add(generateFinalPart(svg));

        if (FitnessUtil.getMinDegree(svg) < Configuration.Piece.MIN_DEGREE) {
            throw new IntersectsException("Degree to small");
        }

        return svg;
    }

    private static Part generateFinalPart(Piece svg) throws IntersectsException {
        int tries = 0;
        while (tries < Configuration.PieceGeneration.MAX_TRIES) {
            tries++;
            final Part p;
            double random = randomRange(0, 99);
            if (random < Configuration.PieceGeneration.LINE_PERCENT) {
                p = new Line(randomPoint(svg,
                        () -> randomPoint(Configuration.Piece.WIDTH / 2)));
            } else if (random < Configuration.PieceGeneration.ARC_PERCENT) {
                p = new Arc(randomPoint(svg,
                        () -> randomPoint(Configuration.Piece.WIDTH / 2)),
                        randomPoint());
            } else {
                p = new DoubleArc(randomPoint(svg,
                        () -> randomPoint(Configuration.Piece.WIDTH / 2)),
                        randomPoint(), randomPoint());
            }


            if (Configuration.ALLOW_INTERSECTIONS || !svg.intersectsWithAny(p)) {
                return p;
            }
        }
        throw new IntersectsException("Generating final part failed");
    }

    private static Part generatePart(Piece svg) throws IntersectsException {
        int tries = 0;
        while (tries < Configuration.PieceGeneration.MAX_TRIES) {
            tries++;
            final Part p;
            double random = randomRange(0, 99);
            if (random < Configuration.PieceGeneration.LINE_PERCENT) {
                p = new Line(randomPoint(svg, PieceGenerator::randomPoint));
            } else if (random < Configuration.PieceGeneration.ARC_PERCENT) {
                p = new Arc(randomPoint(svg, PieceGenerator::randomPoint), randomPoint());
            } else {
                p = new DoubleArc(randomPoint(svg, PieceGenerator::randomPoint),
                        randomPoint(), randomPoint());
            }

            if (Configuration.ALLOW_INTERSECTIONS || !svg.intersectsWithAny(p)) {
                return p;
            }
        }

        throw new IntersectsException("Generating part failed");
    }

    private static Point randomPoint(Piece svg, Supplier<Point> supp) {
        Point p = supp.get();
        List<Point> points = svg.getAsLines().stream().map(Line::getEndPos)
                .collect(Collectors.toList());

        while (true) {
            boolean b = true;
            for (Point pos : points) {
                if (PolygonUtils.distance(pos, p) < 10) {
                    b = false;
                    break;
                }
            }
            if (b) {
                return p;
            }
            p = supp.get();
        }
    }

    private static Point randomPoint(double x) {
        double y_min = 0;
        double y_max = Configuration.Piece.HEIGHT - 10.0;
        return Point.of(x, randomRange(y_min, y_max));
    }

    private static Point randomPoint() {
        double x_min = (Configuration.Piece.WIDTH / 2) + 10.0;
        double x_max = Configuration.Piece.WIDTH;

        double y_min = 0;
        double y_max = Configuration.Piece.HEIGHT - 10.0;

        return Point.of(randomRange(x_min, x_max), randomRange(y_min, y_max));
    }


}
