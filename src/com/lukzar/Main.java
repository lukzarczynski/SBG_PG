package com.lukzar;

import com.lukzar.config.ConfigLoader;
import com.lukzar.config.Configuration;
import com.lukzar.config.Templates;
import com.lukzar.model.Piece;
import com.lukzar.services.PieceSetEvolver;
import com.lukzar.utils.PieceFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        Configuration.targetFeatureValues = ConfigLoader.getConfig("resources/configuration/chess.csv");

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

        final Set<String> targets = new HashSet<>(Configuration.targetFeatureValues.keySet());
        targets.remove("AVG");


        //PieceSetEvolver.EvolverPlusPicker("AVG", 6, 100, 10, "P;R;N;B;Q;K", null);
        PieceSetEvolver.EvolverPlusPicker("AVG", 5, 500, 100, targets, "A");

        //PieceSetEvolver.EvolverPlusEvolver("AVG", 6, 100, 10, "P;R;N;B;Q;K", 6, 100, 20, null);
        PieceSetEvolver.EvolverPlusEvolver("AVG", 5, 500, 100, targets, 6, 100, 20, "A");

    }

    public static void writeToFile(Collection<Piece> pieces, String path) throws IOException {
        writeToFile(pieces, path, new HashMap<>());
    }

    public static void writeToFile(Collection<Piece> pieces, String path, HashMap<Piece, String> names) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
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
                getConfiguration(),
                pieces.stream()
                        .map(PieceFormatter::asPoints)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    // ignore this
    public static void writeToFileAsLines(Collection<Piece> pieces, String path) throws IOException {
        final String content = String.format(Templates.getListTemplate(),
                getConfiguration(),
                pieces.stream()
                        .map(PieceFormatter::asLines)
                        .collect(Collectors.joining("\n"))
        );
        writeToFile(content, path);
    }

    private static String getConfiguration() {
        StringBuilder builder = new StringBuilder("<ul>");
        Configuration.getDescription().forEach(c -> {

            builder.append("<li>")
                    .append(c)
                    .append("</li>");
        });
        return builder.append("</ul>").toString();
    }


}
