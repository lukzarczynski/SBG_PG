package com.lukzar;

import com.lukzar.config.Configuration;
import com.lukzar.config.Templates;
import com.lukzar.model.Piece;
import com.lukzar.services.Evolution;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 28.05.17.
 */
public class Main {

    public static Configuration CONFIG;

    static {
        loadConfiguration();
    }

    public static void main(String[] args) throws IOException {
        final Evolution evolution = new Evolution();
        evolution.initialize();
        System.out.println(evolution.getPopulation().size());
        writeToFile(evolution.getPopulation(), "out/initial_population.html");

        for (int i = 0; i < 4; i++) {
            evolution.evolvePopulation();
            System.out.println(evolution.getPopulation().size());
            writeToFile(evolution.getPopulation(), String.format("out/image_%s.html", i));

        }
    }

    public static void writeToFile(Collection<Piece> pieces, String path) throws IOException {
        final File file = new File(path);

        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(String.format(Templates.getListTemplate(), pieces.stream()
                    .map(Piece::toSvg)
                    .collect(Collectors.joining("\n"))
            ).getBytes());
        }
    }

    private static void loadConfiguration() {
        try {
            try (InputStream in = Files.newInputStream(Paths.get("resources/configuration.yml"))) {
                CONFIG = new Yaml().loadAs(in, Configuration.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
