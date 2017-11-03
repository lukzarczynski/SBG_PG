package com.lukzar.fitness;

import com.lukzar.config.Configuration;

import java.util.Map;

import static java.util.Objects.nonNull;

public enum FitnessAttribute {

    FULL_AREA("%.3f"),
    FULL_HEIGHT("%.3f"),
    FULL_WIDTH("%.3f"),
    PERIMETER("%.3f"),
    DOUBLE_ARC_LENGTH("%.3f ", PERIMETER),
    ARC_LENGTH("%.3f ", PERIMETER),
    LINE_LENGTH("%.3f ", PERIMETER),
    BOX_LENGTH("%.3f"),
    BOX_AREA("%.3f ", FULL_AREA),
    BASE_WIDTH("%.3f "),
    PIECE_AREA("%.3f ", FULL_AREA),
    TOP_PIECE_AREA("%.3f ", PIECE_AREA),
    BOTTOM_PIECE_AREA("%.3f ", PIECE_AREA),
    MIDDLE_PIECE_AREA("%.3f ", PIECE_AREA),
    INNER_HALF_X_AREA("%.3f ", PIECE_AREA),
    MIDDLE_FULL_AREA_OVER_X("%.3f ", PIECE_AREA),
    BASE_TRIANGLE_AREA("%.3f ", PIECE_AREA),
    PIECELIKE_TRIANGLE_AREA("%.3f ", PIECE_AREA),
    SYMMETRY_AREA("%.3f ", PIECE_AREA),
    MIN_DEGREE("%.3f"),
    PIECE_HEIGHT("%.3f ", FULL_HEIGHT),
    PIECE_WIDTH("%.3f ", FULL_WIDTH),
    CENTROID("%s"),
    SYMMETRIC("%s"),
    AVERAGE_DEGREE("%.3f"),
    NUMBER_OF_ANGLES("%.0f"),
    NUMBER_OF_SHARP_ANGLES("%.0f ", NUMBER_OF_ANGLES),
    NUMBER_OF_MEDIUM_ANGLES("%.0f ", NUMBER_OF_ANGLES),
    NUMBER_OF_GENTLE_ANGLES("%.0f ", NUMBER_OF_ANGLES);

    private final String description;
    private final FitnessAttribute percentAttribute;

    FitnessAttribute(String description) {
        this.description = description;
        this.percentAttribute = null;
    }

    FitnessAttribute(String description,
                     FitnessAttribute attribute) {
        this.description = description;
        this.percentAttribute = attribute;
    }


    public String getDescription(Object o, Map<FitnessAttribute, Object> attributes) {
        String result = this.name() + ": ";
        if (nonNull(percentAttribute)) {
            String percent = percent((Double) o, (Double) attributes.get(percentAttribute));
            result += String.format(description, o);
            result += String.format(" ( %s of %s )", percent, percentAttribute.name());
        } else {
             result += String.format(description, o);
        }
        return result;
    }

    private String percent(double v, double max) {
        return String.format("%.1f %%", (v / max * 100));
    }


}
