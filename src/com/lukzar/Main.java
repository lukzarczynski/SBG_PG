package com.lukzar;

import com.lukzar.config.Templates;
import com.lukzar.model.Piece;
import com.lukzar.services.PieceSetEvolver;
import com.lukzar.utils.PieceFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

//        Chess.generate();


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
        PieceSetEvolver.EvolverPlusPicker("AVG", 5, 500, 100, "pawn;rook;knight;bishop;queen;king", "A");

        //PieceSetEvolver.EvolverPlusEvolver("AVG", 6, 100, 10, "pawn;rook;knight;bishop;queen;king", 6, 100, 20, null);
        PieceSetEvolver.EvolverPlusEvolver("AVG", 5, 500, 100, "pawn;rook;knight;bishop;queen;king", 6, 100, 20, "A");

    }

    public static void writeToFile(Collection<Piece> pieces, String path) throws IOException {
        writeToFile(pieces, path, new HashMap<>());
    }

    public static void writeToFile(Collection<Piece> pieces, String path, HashMap<Piece, String> names) throws IOException {
        final String content = String.format(Templates.getListTemplate(), pieces.stream()
                .map(p -> names.getOrDefault(p, "") + p.toSvg())
                .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    private static void writeToFile(String content, String path) throws IOException {
        try (FileOutputStream os = new FileOutputStream(new File(path + ".html"))) {
            os.write(content.getBytes());
        }
    }

    // ignore this
    public static void writeToFileAsPoints(Collection<Piece> pieces, String path) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                pieces.stream()
                        .map(PieceFormatter::asPoints)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    // ignore this
    public static void writeToFileAsLines(Collection<Piece> pieces, String path) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                pieces.stream()
                        .map(PieceFormatter::asLines)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }


}
