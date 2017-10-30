/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {

    public static boolean INIT_POP_TRIANGLE = true;
    public static String TARGET_PIECE = "queen";

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


    public static HashMap<String, HashMap<String, Double>> getTargetFeatureValues()
    {
        HashMap<String, HashMap<String, Double>> hm = new HashMap<>();

        HashMap<String, Double> pawn = new HashMap<>();
        hm.put("pawn", pawn);
        pawn.put("widthRatio",                 .450);
        pawn.put("heightRatio",                .525);
        pawn.put("areaRatio",                  .119);
        pawn.put("topRatio",                   .293);
        pawn.put("middleRatio",                .286);
        pawn.put("symmetryRatio",             1.0);
        pawn.put("innerhalfXRatio",            .0); // todo
        pawn.put("baseTriangleAreaRatio",     1.0);
        pawn.put("piecelikeTriangleAreaRatio", .732);
        pawn.put("perimeterRatio",             .0); // todo
        pawn.put("straightLineRatio",          .0);
        pawn.put("curveLineRatio",            1.0);
        pawn.put("sharpAnglesRatio",           .222);
        pawn.put("gentleAnglesRatio",          .333);

        HashMap<String, Double> rook = new HashMap<>();
        hm.put("rook", rook);
        rook.put("widthRatio",                 .539);
        rook.put("heightRatio",                .625);
        rook.put("areaRatio",                  .190);
        rook.put("topRatio",                   .461);
        rook.put("middleRatio",                .178);
        rook.put("symmetryRatio",             1.0);
        rook.put("innerhalfXRatio",            .0); // todo
        rook.put("baseTriangleAreaRatio",      .920);
        rook.put("piecelikeTriangleAreaRatio", .496);
        rook.put("perimeterRatio",             .0); // todo
        rook.put("straightLineRatio",          .287);
        rook.put("curveLineRatio",             .713);
        rook.put("sharpAnglesRatio",           .0);
        rook.put("gentleAnglesRatio",          .294);

        HashMap<String, Double> knight = new HashMap<>();
        hm.put("knight", knight);
        knight.put("widthRatio",                 .0);
        knight.put("heightRatio",                .0);
        knight.put("areaRatio",                  .0);
        knight.put("topRatio",                   .0);
        knight.put("middleRatio",                .0);
        knight.put("symmetryRatio",              .0);
        knight.put("innerhalfXRatio",            .0); // todo
        knight.put("baseTriangleAreaRatio",      .0);
        knight.put("piecelikeTriangleAreaRatio", .0);
        knight.put("perimeterRatio",             .0); // todo
        knight.put("straightLineRatio",          .0);
        knight.put("curveLineRatio",             .0);
        knight.put("sharpAnglesRatio",           .0);
        knight.put("gentleAnglesRatio",          .0);

        HashMap<String, Double> bishop = new HashMap<>();
        hm.put("bishop", bishop);
        bishop.put("widthRatio",                 .0);
        bishop.put("heightRatio",                .0);
        bishop.put("areaRatio",                  .0);
        bishop.put("topRatio",                   .0);
        bishop.put("middleRatio",                .0);
        bishop.put("symmetryRatio",              .0);
        bishop.put("innerhalfXRatio",            .0); // todo
        bishop.put("baseTriangleAreaRatio",      .0);
        bishop.put("piecelikeTriangleAreaRatio", .0);
        bishop.put("perimeterRatio",             .0); // todo
        bishop.put("straightLineRatio",          .0);
        bishop.put("curveLineRatio",             .0);
        bishop.put("sharpAnglesRatio",           .0);
        bishop.put("gentleAnglesRatio",          .0);

        HashMap<String, Double> queen = new HashMap<>();
        hm.put("queen", queen);
        queen.put("widthRatio",                 .523);
        queen.put("heightRatio",                .90);
        queen.put("areaRatio",                  .204);
        queen.put("topRatio",                   .238);
        queen.put("middleRatio",                .287);
        queen.put("symmetryRatio",             1.0);
        queen.put("innerhalfXRatio",            .0); // todo
        queen.put("baseTriangleAreaRatio",      .981);
        queen.put("piecelikeTriangleAreaRatio", .809);
        queen.put("perimeterRatio",             .0); // todo
        queen.put("straightLineRatio",          .0);
        queen.put("curveLineRatio",            1.0);
        queen.put("sharpAnglesRatio",           .267);
        queen.put("gentleAnglesRatio",          .333);

        HashMap<String, Double> king = new HashMap<>();
        hm.put("king", king);
        king.put("widthRatio",                 .0);
        king.put("heightRatio",                .0);
        king.put("areaRatio",                  .0);
        king.put("topRatio",                   .0);
        king.put("middleRatio",                .0);
        king.put("symmetryRatio",              .0);
        king.put("innerhalfXRatio",            .0); // todo
        king.put("baseTriangleAreaRatio",      .0);
        king.put("piecelikeTriangleAreaRatio", .0);
        king.put("perimeterRatio",             .0); // TODO:
        king.put("straightLineRatio",          .0);
        king.put("curveLineRatio",             .0);
        king.put("sharpAnglesRatio",           .0);
        king.put("gentleAnglesRatio",          .0);

        HashMap<String, Double> AVG = new HashMap<>(); // todo - tu powinno być ładnie wyliczane AVG z wcześniej wstawionych wartości, ale java nie ma LINQ porządnego ani resharpera więc nie umiem takiego wyrażenia złożyć ;p.
        hm.put("AVG", AVG);
        AVG.put("widthRatio",                 .0);
        AVG.put("heightRatio",                .0);
        AVG.put("areaRatio",                  .0);
        AVG.put("topRatio",                   .0);
        AVG.put("middleRatio",                .0);
        AVG.put("symmetryRatio",              .0);
        AVG.put("innerhalfXRatio",            .0);
        AVG.put("baseTriangleAreaRatio",      .0);
        AVG.put("piecelikeTriangleAreaRatio", .0);
        AVG.put("perimeterRatio",             .0);
        AVG.put("straightLineRatio",          .0);
        AVG.put("curveLineRatio",             .0);
        AVG.put("sharpAnglesRatio",           .0);
        AVG.put("gentleAnglesRatio",          .0);

        return hm;
    }
}
