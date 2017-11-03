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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PartMutation {

    public static List<Part> mutate(Part part) {
        if (part instanceof Line) {
            return mutate((Line) part);
        } else if (part instanceof Arc) {
            return Collections.singletonList(mutate((Arc) part));
        } else if (part instanceof DoubleArc) {
            // do nothing
            return Collections.singletonList(mutate((DoubleArc) part));
        }
        throw new RuntimeException("New part type?");
    }

    private static List<Part> mutate(Line line) {
        double random = Math.random();

        final Point start = line.getStartPos();
        final Point end = line.getEndPos();
        final Point middle = Point.of(
                (start.getX() + end.getX()) / 2.0,
                (start.getY() + end.getY()) / 2.0);

        if (random < Configuration.Evolution.Mutation.CHANCE_TO_SPLIT_LINE) {
            // split line
            return Arrays.asList(
                    new Line(start, middle),
                    new Line(middle, end)
            );
        } else {
            // convert Line to Arc
            // and add small curve
            double newX = RandomUtils.randomRange(middle.getX() - 5, middle.getX() + 5);
            double newY = RandomUtils.randomRange(middle.getY() - 5, middle.getY() + 5);
            return Collections.singletonList(new Arc(end, Point.of(newX, newY)));
        }
    }

    private static Part mutate(Arc arc) {
        final Point end = arc.getEndPos();

        return new DoubleArc(end, arc.getQ(), arc.getQ());
    }

    private static Part mutate(DoubleArc arc) {
        final Point end = arc.getEndPos();

        return new Line(end);
    }
}
