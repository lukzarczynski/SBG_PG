package com.lukzar;

import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.DoubleArc;
import com.lukzar.model.elements.Line;

import java.io.IOException;
import java.util.Arrays;

public class Chess {

    public static void generate() throws IOException {

        Piece pawn = new Piece(Point.of(140, 200));
        pawn.add(new Arc(Point.of(125, 180), Point.of(155, 195)));
        pawn.add(new Arc(Point.of(117, 140), Point.of(115, 190)));
        pawn.add(new Arc(Point.of(115, 125), Point.of(140, 130)));
        pawn.add(new Arc(Point.of(100, 95), Point.of(140, 100)));

        Piece rook = new Piece(Point.of(145, 200));
        rook.add(new Arc(Point.of(140, 170), Point.of(165, 190)));
        rook.add(new Arc(Point.of(132, 95), Point.of(115, 180)));
        rook.add(new Arc(Point.of(140, 75), Point.of(140, 95)));
        rook.add(new Line(Point.of(120, 75)));
        rook.add(new Line(Point.of(120, 85)));
        rook.add(new Line(Point.of(110, 85)));
        rook.add(new Line(Point.of(110, 75)));
        rook.add(new Line(Point.of(100, 75)));

        Piece knight = new Piece(Point.of(145, 200));
        knight.convertToAsymmetric();
        knight.add(new Arc(Point.of(135, 170), Point.of(165, 190)));
        knight.add(new DoubleArc(Point.of(110, 40), Point.of(85, 140), Point.of(185, 40)));
        knight.add(new Line(Point.of(100, 55)));
        knight.add(new DoubleArc(Point.of(40, 65), Point.of(60, 55), Point.of(80, 60)));
        knight.add(new Arc(Point.of(35, 85), Point.of(30, 75)));
        knight.add(new DoubleArc(Point.of(80, 90), Point.of(60, 100), Point.of(50, 70)));
        knight.add(new Arc(Point.of(65, 170), Point.of(25, 150)));
        knight.add(new Arc(Point.of(55, 200), Point.of(35, 190)));

        Piece bishop = new Piece(Point.of(145, 200));
        bishop.add(new Arc(Point.of(130, 175), Point.of(165, 195)));
        bishop.add(new Arc(Point.of(117, 122), Point.of(115, 185)));
        bishop.add(new Arc(Point.of(115, 110), Point.of(140, 115)));
        bishop.add(new Arc(Point.of(115, 100), Point.of(137, 105)));
        bishop.add(new Arc(Point.of(105, 45), Point.of(140, 70)));
        bishop.add(new Arc(Point.of(100, 35), Point.of(110, 35)));

        Piece queen = new Piece(Point.of(145, 200));
        queen.add(new Arc(Point.of(130, 170), Point.of(165, 190)));
        queen.add(new Arc(Point.of(117, 110), Point.of(115, 170)));
        queen.add(new Arc(Point.of(115, 95), Point.of(140, 102)));
        queen.add(new Arc(Point.of(115, 85), Point.of(137, 87)));
        queen.add(new Arc(Point.of(140, 30), Point.of(110, 55)));
        queen.add(new Arc(Point.of(105, 30), Point.of(120, 45)));
        queen.add(new Arc(Point.of(100, 20), Point.of(110, 20)));

        Piece king = new Piece(Point.of(145, 200));
        king.add(new Arc(Point.of(130, 170), Point.of(165, 190)));
        king.add(new Arc(Point.of(117, 100), Point.of(115, 170)));
        king.add(new Arc(Point.of(115, 85), Point.of(140, 92)));
        king.add(new Arc(Point.of(115, 75), Point.of(137, 77)));
        king.add(new Arc(Point.of(135, 20), Point.of(140, 55)));
        king.add(new Arc(Point.of(110, 20), Point.of(120, 15)));
        king.add(new Arc(Point.of(100, 5), Point.of(115, 8)));

        Main.writeToFile(Arrays.asList(pawn, rook, knight, bishop, queen, king), "out/chess");
//        Main.writeToFileAsPoints(Arrays.asList(pawn, rook, knight, bishop, queen, king), "out/chess_points");
//        Main.writeToFileAsLines(Arrays.asList(pawn, rook, knight, bishop, queen, king), "out/chess_lines");
    }


    //    @Test
    public void testChess() throws IOException {
        Chess.generate();
    }
}