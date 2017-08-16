package com.lukzar.utils;

import com.lukzar.Main;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;
import com.lukzar.model.elements.Line;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BezierUtilTest {

    @Test
    public void test() throws IOException {
        Piece piece = new Piece();
        piece.getParts().add(new Line(Point.of(116.864086, 87.837876)));
//        piece.getParts().add(new Arc(Point.of(141.317428, 184.871990), Point.of(170.529656, 44.560976)));
        piece.getParts().add(new Arc(Point.of(100.000000, 80.939279), Point.of(191.675170, 19.953596)));

        piece.updateStartPoints();

        System.out.println(piece.intersects());

        Piece converted = new Piece();
        converted.getParts().addAll(piece.getConverted());

        System.out.println(converted.intersects());

        Main.writeToFile(Arrays.asList(piece, converted), "out/test.html");
    }

    @Test
    public void test1() throws IOException {
        Piece piece = new Piece();

        piece.getParts().add(new Arc(Point.of(165.338133, 23.292738), Point.of(162.764600, 138.815818)));
        piece.getParts().add(new Arc(Point.of(165.004800, 30.919027), Point.of(110.061532, 146.851712)));
        piece.getParts().add(new Arc(Point.of(100.000000, 61.518794), Point.of(160.114489, 157.239375)));

        List<Double> arcs = FitnessUtil.getArcs(piece);
        for (int i = 0; i < arcs.size(); i++) {
            Double arc = arcs.get(i);
            System.out.println(i + " " + arc);
        }


        Piece converted = new Piece();
        converted.getParts().addAll(piece.getConverted());

        Main.writeToFile(Arrays.asList(piece, converted), "out/test.html");
    }

}