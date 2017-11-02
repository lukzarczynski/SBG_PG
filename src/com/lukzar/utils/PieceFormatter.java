/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.utils;

import com.lukzar.model.Piece;
import com.lukzar.model.elements.Line;

import java.util.BitSet;
import java.util.List;

public class PieceFormatter {

    private static final String template = "<div style=\"display: inline-block\">\n" +
            "\n" +
            "    <div style=\"float: left\">\n" +
            "        <svg viewBox=\"0 0 200 200\"\n" +
            "             class=\"svg\"\n" +
            "             height=\"200px\"\n" +
            "             width=\"200px\">\n" +
            "\n" +
            "            <rect height=\"200\" width=\"200\" stroke-width=\"1\" stroke=\"black\" fill=\"none\"/>\n" +
            "\n" +
            "            %s\n" +
            "        </svg>\n" +
            "    </div>\n" +
            "</div>";

    public static String asPoints(Piece piece) {
        final BitSet[] cast = RayCasting.cast(piece.getAsLines());

        final StringBuilder builder = new StringBuilder();

        for (int row = 0; row < cast.length; row++) {
            for (int col = 0; col < cast.length; col++) {
                if (cast[row].get(col)) {
                    builder.append(
                            String.format(
                                    "<circle cx=\"%s\" cy=\"%s\" r=\"0.5\" fill=\"red\" stroke=\"red\"></circle>\n",
                                    col, row
                            )
                    );
                }
            }
        }
        return String.format(template, builder.toString());
    }

    public static String asLines(Piece piece) {
        List<Line> asLines = piece.getAsLines();
        Piece n = new Piece(piece.getStart());
        n.convertToAsymmetric();
        n.addAll(asLines);
        return n.toSvg();
    }
}
