/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.lukzar.config.Feature.areaRatio;
import static com.lukzar.config.Feature.baseTriangleAreaRatio;
import static com.lukzar.config.Feature.curveLineRatio;
import static com.lukzar.config.Feature.gentleAnglesRatio;
import static com.lukzar.config.Feature.heightRatio;
import static com.lukzar.config.Feature.innerhalfXRatio;
import static com.lukzar.config.Feature.middleRatio;
import static com.lukzar.config.Feature.perimeterRatio;
import static com.lukzar.config.Feature.piecelikeTriangleAreaRatio;
import static com.lukzar.config.Feature.sharpAnglesRatio;
import static com.lukzar.config.Feature.straightLineRatio;
import static com.lukzar.config.Feature.symmetryRatio;
import static com.lukzar.config.Feature.topRatio;
import static com.lukzar.config.Feature.widthRatio;


public class Configuration {

    public static final double MAXIMUM_SIMILARITY = 0.95;
    public static final boolean ALLOW_INTERSECTIONS = false;
    public static final double PERIMETER_RATIO_MULTIPLIER = 2; // nie ruszać //todo - i szkoda, że % nie jest wypisywany przy figurach

    public static String TARGET_PIECE = "queen";

    public static InitShape INIT_POP_SHAPE = InitShape.pawn;

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

    public static int NUMBER_OF_GENERATIONS = 20;

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

    public enum InitShape
    {
        triangle,
        pawn,
        random
    }

    public static class Evolution {
        public static final int TOURNAMENT_SIZE = 5;
        public static final int CROSSOVER_SIZE = 20;
        public static int MAXIMUM_POPULATION_SIZE = 200;

        public static class Mutation {

            public static final double STARTING_POINT_CHANCE = 0.1;
            public static final double CHANCE_TO_CHANGE_POINT = 0.6;
            public static final double CHANCE_TO_CHANGE_PART = 0.38;
            // CHANCE_TO_CONVERT_TO_ASYM = 1 - CHANCE_TO_CHANGE_POINT - CHANCE_TO_CHANGE_PART
            public static final double CHANCE_TO_SPLIT_LINE = 0.8; // 20% to convert to Arc, 80% to split
            public static final double OFFSET = 40;
        }
    }

    public static class Piece {
        public static final double WIDTH = 200;
        public static final double HEIGHT = 200;
        public static final double MIN_DEGREE = 0;
    }


    public static Map<String, Map<Feature, Double>> getTargetFeatureValues()
    {
        Map<String, Map<Feature, Double>> hm = new HashMap<>();

        Map<Feature, Double> pawn = new HashMap<>();
        hm.put("pawn", pawn);
        pawn.put(widthRatio,                 .450);
        pawn.put(heightRatio,                .525);
        pawn.put(areaRatio,                  .119);
        pawn.put(topRatio,                   .293);
        pawn.put(middleRatio,                .286);
        pawn.put(symmetryRatio,             1.0);
        pawn.put(innerhalfXRatio,            .846);
        pawn.put(baseTriangleAreaRatio,     1.0);
        pawn.put(piecelikeTriangleAreaRatio, .732);
        pawn.put(perimeterRatio,             .409);
        pawn.put(straightLineRatio,          .0);
        pawn.put(curveLineRatio,            1.0);
        pawn.put(sharpAnglesRatio,           .222);
        pawn.put(gentleAnglesRatio,          .333);

        Map<Feature, Double> rook = new HashMap<>();
        hm.put("rook", rook);
        rook.put(widthRatio,                 .539);
        rook.put(heightRatio,                .625);
        rook.put(areaRatio,                  .190);
        rook.put(topRatio,                   .461);
        rook.put(middleRatio,                .178);
        rook.put(symmetryRatio,             1.0);
        rook.put(innerhalfXRatio,            .732);
        rook.put(baseTriangleAreaRatio,      .920);
        rook.put(piecelikeTriangleAreaRatio, .496);
        rook.put(perimeterRatio,             .449);
        rook.put(straightLineRatio,          .287);
        rook.put(curveLineRatio,             .713);
        rook.put(sharpAnglesRatio,           .0);
        rook.put(gentleAnglesRatio,          .294);

        Map<Feature, Double> knight = new HashMap<>();
        hm.put("knight", knight);
        knight.put(widthRatio,                 .598);
        knight.put(heightRatio,                .800);
        knight.put(areaRatio,                  .293);
        knight.put(topRatio,                   .312);
        knight.put(middleRatio,                .309);
        knight.put(symmetryRatio,              .776);
        knight.put(innerhalfXRatio,            .732);
        knight.put(baseTriangleAreaRatio,      .922);
        knight.put(piecelikeTriangleAreaRatio, .58);
        knight.put(perimeterRatio,             .436);
        knight.put(straightLineRatio,          .037);
        knight.put(curveLineRatio,             1-.037);
        knight.put(sharpAnglesRatio,           .0);
        knight.put(gentleAnglesRatio,          .667);

        Map<Feature, Double> bishop = new HashMap<>();
        hm.put("bishop", bishop);
        bishop.put(widthRatio,                 .523);
        bishop.put(heightRatio,                .825);
        bishop.put(areaRatio,                  .186);
        bishop.put(topRatio,                   .261);
        bishop.put(middleRatio,                .296);
        bishop.put(symmetryRatio,             1.0);
        bishop.put(innerhalfXRatio,            .877);
        bishop.put(baseTriangleAreaRatio,     1.0);
        bishop.put(piecelikeTriangleAreaRatio, .792);
        bishop.put(perimeterRatio,             .436);
        bishop.put(straightLineRatio,          .0);
        bishop.put(curveLineRatio,            1.0);
        bishop.put(sharpAnglesRatio,           .154);
        bishop.put(gentleAnglesRatio,          .385);

        Map<Feature, Double> queen = new HashMap<>();
        hm.put("queen", queen);
        queen.put(widthRatio,                 .523);
        queen.put(heightRatio,                .90);
        queen.put(areaRatio,                  .204);
        queen.put(topRatio,                   .238);
        queen.put(middleRatio,                .287);
        queen.put(symmetryRatio,             1.0);
        queen.put(innerhalfXRatio,            .852);
        queen.put(baseTriangleAreaRatio,      .981);
        queen.put(piecelikeTriangleAreaRatio, .809);
        queen.put(perimeterRatio,             .493);
        queen.put(straightLineRatio,          .0);
        queen.put(curveLineRatio,            1.0);
        queen.put(sharpAnglesRatio,           .267);
        queen.put(gentleAnglesRatio,          .333);

        Map<Feature, Double> king = new HashMap<>();
        hm.put("king", king);
        king.put(widthRatio,                 .523);
        king.put(heightRatio,                .975);
        king.put(areaRatio,                  .255);
        king.put(topRatio,                   .349);
        king.put(middleRatio,                .25);
        king.put(symmetryRatio,             1.0);
        king.put(innerhalfXRatio,            .826);
        king.put(baseTriangleAreaRatio,      .875);
        king.put(piecelikeTriangleAreaRatio, .691);
        king.put(perimeterRatio,             .47);
        king.put(straightLineRatio,          .0);
        king.put(curveLineRatio,            1.0);
        king.put(sharpAnglesRatio,           .40);
        king.put(gentleAnglesRatio,          .333);

        final Collection<Map<Feature, Double>> baseMaps = hm.values();
        final Map<Feature, Double> AVG = new HashMap<>();
        Stream.of(Feature.values())
                .forEach(key -> AVG.put(key, baseMaps.stream()
                .filter(m -> m.containsKey(key))
                .mapToDouble(m -> m.get(key))
                .average().orElse(0)));

        hm.put("AVG", AVG);

        return hm;
    }
}
