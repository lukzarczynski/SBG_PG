package com.lukzar.services;

import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.exceptions.IntersectsException;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;

import static com.lukzar.Main.CONFIG;
import static com.lukzar.utils.RandomUtils.randomRange;

/**
 * Created by lukasz on 08.07.17.
 */
public class PieceGenerator {


    public static Piece generate() throws IntersectsException {
        int numberOfParts = randomRange(
                CONFIG.getPieceGeneration().getMinParts(),
                CONFIG.getPieceGeneration().getMaxParts());

        final Piece svg = new Piece();

        while (svg.getParts().size() < numberOfParts - 1) {
            svg.getParts().add(generatePart(svg));
            svg.updateStartPoints();
        }
        svg.getParts().add(generateFinalPart(svg));
        svg.updateStartPoints();

        return svg;
    }

    private static Part generateFinalPart(Piece svg) throws IntersectsException {
        int tries = 0;
        while (tries < CONFIG.getPieceGeneration().getMaxTries()) {
            tries++;
            final Part p;
            if (randomRange(0, 99) < CONFIG.getPieceGeneration().getLinePercent()) {
                p = new Line(randomPoint(CONFIG.getPiece().getWidth() / 2));
            } else {
                p = new Arc(randomPoint(CONFIG.getPiece().getWidth() / 2), randomPoint());
            }

            if (!svg.intersectsWithAny(p)) {
                return p;
            }
        }
        throw new IntersectsException("Generating final part failed");
    }

    private static Part generatePart(Piece svg) throws IntersectsException {
        int tries = 0;
        while (tries < CONFIG.getPieceGeneration().getMaxTries()) {
            tries++;
            final Part p;
            if (randomRange(0, 99) < CONFIG.getPieceGeneration().getLinePercent()) {
                p = new Line(randomPoint());
            } else {
                p = new Arc(randomPoint(), randomPoint());
            }

            if (!svg.intersectsWithAny(p)) {
                return p;
            }
        }

        throw new IntersectsException("Generating part failed");
    }

    private static Point randomPoint(int x) {
        int y_min = 0;
        int y_max = CONFIG.getPiece().getHeight();
        return Point.of(x, randomRange(y_min, y_max));
    }

    private static Point randomPoint() {
        int x_min = CONFIG.getPiece().getWidth() / 2;
        int x_max = CONFIG.getPiece().getWidth();

        int y_min = 0;
        int y_max = CONFIG.getPiece().getHeight();

        return Point.of(randomRange(x_min, x_max), randomRange(y_min, y_max));
    }


}
