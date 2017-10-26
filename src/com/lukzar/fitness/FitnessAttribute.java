package com.lukzar.fitness;

import com.lukzar.config.Configuration;

import java.util.Map;

import static java.util.Objects.nonNull;

public enum FitnessAttribute {

    SHAPE_LENGTH("Shape Length: %.3f ( 100 %% )"),
    DOUBLE_ARC_LENGTH("Double Arc Length: %.3f ( %s )", SHAPE_LENGTH),
    ARC_LENGTH("Arc Length: %.3f ( %s )", SHAPE_LENGTH),
    LINE_LENGTH("Line Length: %.3f ( %s )", SHAPE_LENGTH),
    BOX_LENGTH("Box Length: %.3f"),
    BASE_WIDTH("Base width Length: %.3f"),
    AREA("Area: %.3f ( 100%%, %s of total area )", Configuration.Piece.WIDTH * Configuration.Piece.HEIGHT),
    UP_HALF_AREA("Upper Half Area: %.3f ( %s )", AREA),
    BOTTOM_HALF_AREA("Lower Half Area: %.3f ( %s )", AREA),
    MID_Y_AREA("Middle Half over Y Area: %.3f ( %s )", AREA),
    MID_X_AREA("Middle Half over X Area: %.3f ( %s )", AREA),
    TRIANGLE_BASE_AREA("Triangle Area (BASE): %.3f ( %s )", AREA),
    TRIANGLE_PIECE_AREA("Triangle Area (PIECE): %.3f ( %s )", AREA),
    MIN_DEGREE("Min Degree: %.3f"),
    HEIGHT("Height: %.3f ( %s )", Configuration.Piece.HEIGHT),
    WIDTH("Width: %.3f ( %s )", Configuration.Piece.WIDTH),
    CENTROID("Centroid: ( %s )"),
    SYMMETRIC("Symmetric: %s"),
    AVERAGE_DEGREE("Average degree: %.3f");

    private final String description;
    private final FitnessAttribute percentAttribute;
    private final Double percentStatic;

    FitnessAttribute(String description) {
        this.description = description;
        this.percentAttribute = null;
        this.percentStatic = null;
    }

    FitnessAttribute(String description,
                     FitnessAttribute attribute) {
        this.description = description;
        this.percentAttribute = attribute;
        this.percentStatic = null;
    }

    FitnessAttribute(String description,
                     Double staticVal) {
        this.description = description;
        this.percentAttribute = null;
        this.percentStatic = staticVal;
    }

    public String getDescription(Object o, Map<FitnessAttribute, Object> attributes) {
        if (nonNull(percentStatic)) {
            return String.format(description, o, percent((Double) o, percentStatic));
        } else if (nonNull(percentAttribute)) {
            return String.format(description, o, percent((Double) o, (Double) attributes.get(percentAttribute)));
        } else {
            return String.format(description, o);
        }
    }

    private String percent(double v, double max) {
        return String.format("%.1f %%", (v / max * 100));
    }


}
