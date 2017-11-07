package com.lukzar.config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created by lukasz on 05.11.17.
 */
public class ConfigLoader {

    private static final String separator = ",";

    public static Map<String, Map<Feature, Double>> getConfig(String path) throws IOException {
        final Map<String, Map<Feature, Double>> result = new HashMap<>();
        final Scanner scanner = new Scanner(Paths.get(path));
        final String headerRow = scanner.nextLine();
        String[] header = headerRow.split(separator);

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            final String[] split = line.split(separator);
            if (split.length > 0) {
                final HashMap<Feature, Double> conf = new HashMap<>();
                result.put(split[0], conf);
                for (int i = 1; i < split.length; i++) {
                    conf.put(Feature.valueOf(header[i]), Double.valueOf(split[i]));
                }
            }
        }

        addAverage(result);

        return result;
    }

    private static void addAverage(Map<String, Map<Feature, Double>> result) {
        final Collection<Map<Feature, Double>> baseMaps = result.values();
        final Map<Feature, Double> AVG = new HashMap<>();
        Stream.of(Feature.values())
                .forEach(key -> AVG.put(key, baseMaps.stream()
                        .filter(m -> m.containsKey(key))
                        .mapToDouble(m -> m.get(key))
                        .average().orElse(0)));

        result.put("AVG", AVG);
    }
}
