/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {


    public static HashMap<String, double[]> getFitnessWeights()
    {
        HashMap<String, double[]> hm = new HashMap<>();
        hm.put("pawn", new double[] {0.054,	-0.054,	-0.063,	-0.054,	0.119,	0.000,	-0.110,	-0.054,	0.110,	-0.009,	-0.110,	-0.119,	-0.145});
        hm.put("rook", new double[] {-0.116,	0.116,	0.057,	0.116,	0.087,	0.000,	-0.028,	0.116,	0.028,	-0.059,	-0.028,	-0.087,	0.161});
        hm.put("knight", new double[] {-0.122,	0.122,	0.087,	0.122,	0.065,	0.000,	-0.030,	0.122,	0.030,	-0.035,	-0.030,	-0.065,	0.170});
        hm.put("bishop", new double[] {0.106,	-0.106,	-0.187,	-0.106,	0.092,	0.000,	-0.012,	-0.106,	0.012,	-0.080,	-0.012,	-0.092,	-0.087});
        hm.put("queen", new double[] {-0.071,	0.071,	0.167,	0.071,	-0.120,	0.000,	0.024,	0.071,	-0.024,	0.096,	0.024,	0.120,	0.140});
        hm.put("king", new double[] {-0.030,	0.030,	0.072,	0.030,	-0.166,	0.000,	0.125,	0.030,	-0.125,	0.042,	0.125,	0.166,	0.059});

        return hm;
    }


    public static int NUMBER_OF_EVOLUTIONS = 20;

    public static boolean ALLOW_INTERSECTIONS = false;

    public static class PieceGeneration {
        public static final int LINE_PERCENT = 20;
        public static final int ARC_PERCENT = 80;
        //         DOUBLE_ARC_PERCENT = 100 - line - arc;
        public static final int MIN_PARTS = 3;
        public static final int MAX_PARTS = 5;
        public static final int MAX_TRIES = 5;

        public static final int START_MIN = 110;
        public static final int START_MAX = 190;
    }

    public static class Evolution {
        public static final int INITIAL_SIZE = 500;
        public static final int TOURNAMENT_SIZE = 5;
        public static final int CROSSOVER_SIZE = 200;
        public static final double MUTATION_RATE = 0.1;
        public static final double MUTATION_OFFSET = 40;
        public static final double ASYMMETRIC_RATE = 0.01;
    }

    public static class Piece {
        public static final double WIDTH = 200;
        public static final double HEIGHT = 200;
        public static final double MIN_DEGREE = 0;
    }
}
