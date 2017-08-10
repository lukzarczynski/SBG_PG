package com.lukzar.model.elements;

import com.lukzar.model.Point;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by lukasz on 04.06.17.
 */
@EqualsAndHashCode
@Getter
@Setter
@RequiredArgsConstructor
public abstract class Part {

    final Point endPos;
    Point startPos;

    public abstract String toSvg();

    public abstract String toSvgReversed();

    public abstract List<Line> convertToLines();

}
