/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.services.evolution;

import com.lukzar.config.Configuration;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;
import com.lukzar.model.elements.Part;
import com.lukzar.utils.RandomUtils;

public class PointMutation {

    public static Part mutate(Part part, Point min, Point max) {
        if (part instanceof Line) {
            return mutate((Line) part, min, max);
        } else if (part instanceof Arc) {
            return mutate((Arc) part, min, max);

        } else if (part instanceof DoubleArc) {
            return mutate((DoubleArc) part, min, max);
        }
        throw new RuntimeException("New part type?");
    }

    private static Line mutate(Line line, Point min, Point max) {
        return new Line(line.getStartPos(), mutate(line.getEndPos(), min, max));
    }

    private static Arc mutate(Arc part, Point min, Point max) {
        if (Math.random() <= 0.5) {
            return new Arc(mutate(part.getEndPos(), min, max), part.getQ());
        } else {
            return new Arc(part.getEndPos(), mutate(part.getQ(), min, max));
        }
    }

    private static DoubleArc mutate(DoubleArc part, Point min, Point max) {
        double random = Math.random();
        if (random <= 0.33) {
            return new DoubleArc(mutate(part.getEndPos(), min, max), part.getQ1(), part.getQ2());
        } else if (random <= 0.66) {
            return new DoubleArc(part.getEndPos(), mutate(part.getQ1(), min, max), part.getQ2());
        } else {
            return new DoubleArc(part.getEndPos(), part.getQ1(), mutate(part.getQ2(), min, max));
        }
    }

    private static Point mutate(Point point, Point min, Point max) {
        double newX = RandomUtils.randomRange(
                point.getX() - Configuration.Evolution.Mutation.OFFSET,
                point.getX() + Configuration.Evolution.Mutation.OFFSET);
        double newY = RandomUtils.randomRange(
                point.getY() - Configuration.Evolution.Mutation.OFFSET,
                point.getY() + Configuration.Evolution.Mutation.OFFSET);

        return Point.of(
                RandomUtils.ensureRange(newX, min.getX(), max.getX()),
                RandomUtils.ensureRange(newY, min.getY(), max.getY())
        );
    }

}
