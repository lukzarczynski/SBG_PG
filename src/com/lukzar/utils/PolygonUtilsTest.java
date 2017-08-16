package com.lukzar.utils;

import com.lukzar.Main;
import com.lukzar.fitness.FitnessUtil;
import com.lukzar.model.Piece;
import com.lukzar.model.Point;
import com.lukzar.model.elements.Line;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

        System.out.println(FitnessUtil.area(converted));

        double bottomHalf = FitnessUtil.area(piece, 100, 200);
        System.out.println(bottomHalf);
        double topHalf = FitnessUtil.area(piece, 0, 100);
        System.out.println(topHalf);
        System.out.println(bottomHalf + topHalf);

        System.out.println(FitnessUtil.area(piece, 50, 150));
        System.out.println(FitnessUtil.area(piece, 0, 50) + FitnessUtil.area(piece, 150, 200));


        Main.writeToFile(Arrays.asList(piece, converted,
                trim(piece, 100, 200),
                trim(piece, 0, 100),
                trim(piece, 50, 150),
                trim(piece, 0, 50),
                trim(piece, 150, 200)
        ), "out/trim");
    }

    private Piece trim(Piece piece, double min, double max) {
        List<Point> trimmed = PolygonUtils.trim(piece, min, max);
        Piece result = new Piece(trimmed.get(0));

        trimmed.forEach(p -> result.add(new Line(p)));

        double trimArea = FitnessUtil.area(piece, min, max);
        double trimPieceArea = FitnessUtil.area(result);

        System.out.println("Min: " + min + ", Max: " + max);
        System.out.println("\tTrim Area: " + trimArea);
        System.out.println("\tTrim Piece Area: " + trimPieceArea);

        return result;

    }

}