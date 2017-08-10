/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import com.lukzar.model.Point;

import lombok.Data;

@Data
public class Configuration {

    private PieceGeneration pieceGeneration;
    private Evolution evolution;
    private Piece piece;

    @Data
    public static class PieceGeneration {
        private int linePercent;
        private int minParts;
        private int maxParts;
        private int maxTries;
    }

    @Data
    public static class Evolution {
        private int initialSize;
        private int tournamentSize;
        private int crossoverSize;
        private double mutationRate;
        private double mutationOffset;
    }

    @Data
    public static class Piece {
        private Point start;
        private double width;
        private double height;
    }
}
