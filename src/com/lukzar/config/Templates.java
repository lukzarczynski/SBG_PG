/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Objects.isNull;

public class Templates {

    private static String LIST_TEMPLATE;
    private static String IMAGE_TEMPLATE;

    public static String getListTemplate() {
        if (isNull(LIST_TEMPLATE)) {
            try {
                LIST_TEMPLATE = new String(Files.readAllBytes(Paths.get("resources/templates/image_list_template.html")));
            } catch (IOException e) {
                throw new RuntimeException("Could not load list template", e);
            }
        }
        return LIST_TEMPLATE;
    }

    public static String getImageTemplate() {
        if (isNull(IMAGE_TEMPLATE)) {
            try {
                IMAGE_TEMPLATE = new String(Files.readAllBytes(Paths.get("resources/templates/image_template.svg")));
            } catch (IOException e) {
                throw new RuntimeException("Could not load template", e);
            }
        }
        return IMAGE_TEMPLATE;
    }
}
