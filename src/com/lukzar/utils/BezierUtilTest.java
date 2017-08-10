package com.lukzar.utils;

import com.lukzar.Main;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.DoubleArc;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class BezierUtilTest {

    @Test
    public void test() throws IOException {
        Piece piece = new Piece();

        DoubleArc arc1 = new DoubleArc(Point.of(100, 100), Point.of(200, 0), Point.of(100, 200));
        piece.getParts().add(arc1);

        piece.updateStartPoints();

        System.out.println(piece.intersects());

        Piece converted = new Piece();
        converted.getParts().addAll(piece.getConverted());

        System.out.println(converted.intersects());

        Main.writeToFile(Arrays.asList(piece, converted), "out/test.html");
    }

}