package com.lukzar;

import com.lukzar.services.PieceSetEvolver;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);


        /*
        String[] pieces = {
                //"pawn",
                //"rook",
                //"knight",
                //"bishop",
                //"queen",
                //"king",
                "AVG"
        };
        for (String piece : pieces) {
            System.out.println("GENERATING " + piece);
            PieceSetEvolver.SimpleGeneration(piece, 20, 200, 10);
        }
        */


        //PieceSetEvolver.EvolverPlusPicker("AVG", 6, 100, 10, "pawn;rook;knight;bishop;queen;king", null);
        PieceSetEvolver.EvolverPlusPicker("AVG", 30, 500, 100, "pawn;rook;knight;bishop;queen;king", "A");

        //PieceSetEvolver.EvolverPlusEvolver("AVG", 6, 100, 10, "pawn;rook;knight;bishop;queen;king", 6, 100, 20, null);
        PieceSetEvolver.EvolverPlusEvolver("AVG", 30, 500, 100, "pawn;rook;knight;bishop;queen;king", 6, 100, 20, "A");

    }





}
