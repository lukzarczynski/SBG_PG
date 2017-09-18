/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import com.lukzar.model.Point;

public class Configuration {

    public static int NUMBER_OF_EVOLUTIONS = 10;

    public static boolean ALLOW_INTERSECTIONS = false;

    public static class PieceGeneration {
        public static final int LINE_PERCENT = 10;
        public static final int ARC_PERCENT = 70;
        //         DOUBLE_ARC_PERCENT = 100 - line - arc;
        public static final int MIN_PARTS = 2;
        public static final int MAX_PARTS = 5;
        public static final int MAX_TRIES = 5;
    }

    public static class Evolution {
        public static final int INITIAL_SIZE = 500;
        public static final int TOURNAMENT_SIZE = 5;
        public static final int CROSSOVER_SIZE = 200;
        public static final double MUTATION_RATE = 0.1;
        public static final double MUTATION_OFFSET = 20.0;
    }

    public static class Piece {
        public static final Point START = Point.of(120, 200);
        public static final double WIDTH = 200;
        public static final double HEIGHT = 200;
        public static final double MIN_DEGREE = 15;
    }
}
