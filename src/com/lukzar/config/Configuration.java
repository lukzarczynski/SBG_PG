/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Configuration {

    public static double MAXIMUM_SIMILARITY = 0.95;

    public static double CHOICE_MAX_SIMILARITY = 0.90;
    public static double CHOICE_MIN_SIMILARITY = 0.50;

    public static final boolean ALLOW_INTERSECTIONS = false;
    public static final double PERIMETER_RATIO_MULTIPLIER = 2; // nie ruszać //todo - i szkoda, że % nie jest wypisywany przy figurach

    public static String TARGET_PIECE = "Q";

    public static InitShape INIT_POP_SHAPE = InitShape.pawn;
    public static int NUMBER_OF_GENERATIONS = 20;
    public static Map<String, Map<Feature, Double>> targetFeatureValues = new HashMap<>();

    public static String InitPopShapeStr() {
        switch (INIT_POP_SHAPE) {
            case pawn:
                return "PWN";
            case triangle:
                return "TRI";
            case random:
                return "RND";
        }
        return "WTF";
    }

    public static List<String> getDescription() {
        List<String> result = new ArrayList<>();
        result.add("MAXIMUM_SIMILARITY: " + MAXIMUM_SIMILARITY);
        result.add("CHOICE_SIMILARITY: (" + CHOICE_MIN_SIMILARITY+", "+CHOICE_MAX_SIMILARITY+")");
        result.add("ALLOW_INTERSECTIONS: " + ALLOW_INTERSECTIONS);
        result.add("PERIMETER_RATIO_MULTIPLIER: " + PERIMETER_RATIO_MULTIPLIER);
        result.add("INIT_POP_SHAPE: " + INIT_POP_SHAPE);

        result.addAll(
                PieceGeneration.getDescription()
                        .stream().map(e -> "PieceGeneration." + e)
                        .collect(Collectors.toList())
        );
        result.addAll(
                Piece.getDescription()
                        .stream().map(e -> "Piece." + e)
                        .collect(Collectors.toList())
        );
        result.addAll(
                Evolution.getDescription()
                        .stream().map(e -> "Evolution." + e)
                        .collect(Collectors.toList())
        );
        return result;
    }

    public enum InitShape {
        triangle,
        pawn,
        random
    }

    public static class PieceGeneration {
        public static final int LINE_PERCENT = 20;
        public static final int ARC_PERCENT = 80;
        //         DOUBLE_ARC_PERCENT = 100 - line - arc;
        public static final int MIN_PARTS = 3;
        public static final int MAX_PARTS = 5;
        public static final int MAX_TRIES = 5;

        public static final int START_MIN = 110;
        public static final int START_MAX = 190;

        public static List<String> getDescription() {
            List<String> result = new ArrayList<>();
            result.add("LINE_PERCENT: " + LINE_PERCENT);
            result.add("ARC_PERCENT: " + ARC_PERCENT);
            result.add("MIN_PARTS: " + MIN_PARTS);
            result.add("MAX_PARTS: " + MAX_PARTS);
            result.add("MAX_TRIES: " + MAX_TRIES);
            result.add("START_MIN: " + START_MIN);
            result.add("START_MAX: " + START_MAX);
            return result;
        }
    }

    public static class Evolution {
        public static int TOURNAMENT_SIZE = 5;
        public static int CROSSOVER_SIZE = 20;
        public static int MAXIMUM_POPULATION_SIZE = 200;

        public static List<String> getDescription() {
            List<String> result = new ArrayList<>();
            result.add("TOURNAMENT_SIZE: " + TOURNAMENT_SIZE);
            result.add("CROSSOVER_SIZE: " + CROSSOVER_SIZE);
            result.add("MAXIMUM_POPULATION_SIZE: " + MAXIMUM_POPULATION_SIZE);
            result.add("Mutation.STARTING_POINT_CHANCE: " + Mutation.STARTING_POINT_CHANCE);
            result.add("Mutation.CHANCE_TO_CHANGE_POINT: " + Mutation.CHANCE_TO_CHANGE_POINT);
            result.add("Mutation.CHANCE_TO_CHANGE_PART: " + Mutation.CHANCE_TO_CHANGE_PART);
            result.add("Mutation.CHANCE_TO_SPLIT_LINE: " + Mutation.CHANCE_TO_SPLIT_LINE);
            result.add("Mutation.OFFSET: " + Mutation.OFFSET);
            return result;
        }

        public static class Mutation {

            public static double STARTING_POINT_CHANCE = 0.1;
            public static double CHANCE_TO_CHANGE_POINT = 0.5;
            public static double CHANCE_TO_CHANGE_PART = 0.38;
            public static double CHANCE_TO_CONVERT_TO_ASYNC = 0.02;
            public static double CHANCE_TO_SCALE = 1.0 - CHANCE_TO_CHANGE_POINT - CHANCE_TO_CHANGE_PART - CHANCE_TO_CONVERT_TO_ASYNC;
            public static double SCALE_OFFSET = 0.1;
            public static double CHANCE_TO_SPLIT_LINE = 0.8; // 20% to convert to Arc, 80% to split
            public static double OFFSET = 40;
        }
    }

    public static class Piece {
        public static final double WIDTH = 200;
        public static final double HEIGHT = 200;
        public static final double MIN_DEGREE = 0;

        public static List<String> getDescription() {
            List<String> result = new ArrayList<>();
            result.add("WIDTH: " + WIDTH);
            result.add("HEIGHT: " + HEIGHT);
            result.add("MIN_DEGREE: " + MIN_DEGREE);
            return result;
        }
    }
}
