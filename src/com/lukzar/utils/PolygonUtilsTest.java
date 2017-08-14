package com.lukzar.utils;

import com.lukzar.Main;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class PolygonUtilsTest {

    @Test
    public void test() throws IOException {
        Piece piece = new Piece();

        piece.getParts().add(new Line(Point.of(169.217349, 160.105185)));
        piece.getParts().add(new Line(Point.of(181.035933, 68.849812)));
        piece.getParts().add(new Line(Point.of(115.938931, 90.970114)));
        piece.getParts().add(new Line(Point.of(100.000000, 172.229783)));

        Piece converted = new Piece();
        converted.getParts().addAll(piece.getConverted());

        Piece bottom = new Piece();
        bottom.getParts().addAll(PolygonUtils.trim(piece.getConverted(), 100, 200));

        Piece top = new Piece();
        top.getParts().addAll(PolygonUtils.trim(piece.getConverted(), 0, 100));

        Piece middle = new Piece();
        middle.getParts().addAll(PolygonUtils.trim(piece.getConverted(), 50, 150));


        Main.writeToFile(Arrays.asList(piece, converted, bottom, top, middle), "out/trim");


    }

}