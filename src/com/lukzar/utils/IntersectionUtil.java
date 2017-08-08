package com.lukzar.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.lukzar.model.Point;
import com.lukzar.model.elements.Arc;

/**
 * Created by lukasz on 09.07.17.
 */
public class IntersectionUtil {

    public static boolean arcToArcIntersection(Point arc1Start, Arc arc1, Point arc2Start, Arc arc2) {
        return arcToArcIntersection2(arc1Start, arc1, arc2Start, arc2)
                || arcToArcIntersection2(arc2Start, arc2, arc1Start, arc1);
    }

    public static boolean arcToArcIntersection2(Point arc1Start, Arc arc1, Point arc2Start, Arc arc2) {
        final Point oppositeToQ = calculateParallelogramPoint(arc1Start, arc1.getQ(), arc1.getEndPos());
        final List<Point> intersectionsWithDiagonal =
                lineToArcIntersection(arc1.getQ(), oppositeToQ, arc1Start, arc1);

        if (intersectionsWithDiagonal.size() != 1) {
            return true;
        }

        final Point middleOfArc = intersectionsWithDiagonal.get(0);

        return !(lineToArcIntersection(arc1Start, middleOfArc, arc2Start, arc2).isEmpty()
                && lineToArcIntersection(middleOfArc, arc1.getEndPos(), arc2Start, arc2).isEmpty());
    }

    public static List<Point> lineToArcIntersection(Point lineStart, Point lineEnd, Point arcStart, Arc arc) {
        Point q = arc.getQ();
        Point endPos = arc.getEndPos();
        double[] X = new double[3];

        double[] lx = new double[]{lineStart.getX(), lineEnd.getX()};
        double[] ly = new double[]{lineStart.getY(), lineEnd.getY()};

        double[] px = new double[]{
                arcStart.getX(),
                q.getX() + 1,
                q.getX(),
                endPos.getX()
        };

        double[] py = new double[]{
                arcStart.getY(),
                q.getY(),
                q.getY(),
                endPos.getY()
        };

        double A = ly[1] - ly[0];        //A=y2-y1
        double B = lx[0] - lx[1];        //B=x1-x2
        double C = lx[0] * (ly[0] - ly[1]) +
                ly[0] * (lx[1] - lx[0]);    //C=x1*(y1-y2)+y1*(x2-x1)

        double[] bx = bezierCoefficients(px[0], px[1], px[2], px[3]);
        double[] by = bezierCoefficients(py[0], py[1], py[2], py[3]);

        double[] P = new double[4];
        P[0] = A * bx[0] + B * by[0];		/*t^3*/
        P[1] = A * bx[1] + B * by[1];		/*t^2*/
        P[2] = A * bx[2] + B * by[2];		/*t*/
        P[3] = A * bx[3] + B * by[3] + C;	/*1*/

        double[] r = cubicRoots(P);

        List<Point> result = new ArrayList<>();
    /*verify the roots are in bounds of the linear segment*/
        for (int i = 0; i < 3; i++) {
            double t = r[i];

            X[0] = bx[0] * t * t * t + bx[1] * t * t + bx[2] * t + bx[3];
            X[1] = by[0] * t * t * t + by[1] * t * t + by[2] * t + by[3];

        /*above is intersection point assuming infinitely long line segment,
          make sure we are also in bounds of the line*/
            double s;
            if ((lx[1] - lx[0]) != 0)           /*if not vertical line*/
                s = (X[0] - lx[0]) / (lx[1] - lx[0]);
            else
                s = (X[1] - ly[0]) / (ly[1] - ly[0]);

        /*in bounds?*/
            if (t >= 0 && t <= 1.0 && s >= 0 && s <= 1.0) {
                result.add(Point.of((int) X[0], (int) X[1]));
            }

        }
        return result;

    }

    public static Optional<Point> lineToLineIntersection(Point line1Start, Point l1end,
                                                         Point line2start, Point l2end) {
        double denominator = (line2start.getX() - l2end.getX()) * (line1Start.getY() - l1end.getY())
                - (line2start.getY() - l2end.getY()) * (line1Start.getX() - l1end.getX());
        if (denominator == 0) {
            return Optional.empty();
        }
        Point point = Point.of((int) ((
                        (line2start.getX() * l2end.getY() - line2start.getY() * l2end.getX()) *
                                (line1Start.getX() - l1end.getX()) -
                                (line2start.getX() - l2end.getX()) *
                                        (line1Start.getX() * l1end.getY() - line1Start.getY() * l1end.getX()))
                        / denominator),
                (int) ((
                        (line2start.getX() * l2end.getY() - line2start.getY() * l2end.getX()) *
                                (line1Start.getY() - l1end.getY())
                                - (line2start.getY() - l2end.getY())
                                * (line1Start.getX() * l1end.getY() - line1Start.getY() * l1end.getX()))
                        / denominator));

        if (point.getX() <= Math.min(line1Start.getX(), l1end.getX()) ||
                point.getX() >= Math.max(line1Start.getX(), l1end.getX())) {
            return Optional.empty();
        }
        return Optional.of(point);
    }

    private static Point calculateParallelogramPoint(Point A, Point Q, Point B) {
        int x = Math.min(A.getX(), B.getX()) + Math.max(A.getX(), B.getX()) - Q.getX();
        int y = Math.min(A.getY(), B.getY()) + Math.max(A.getY(), B.getY()) - Q.getY();
        return Point.of(x, y);
    }

    private static double[] cubicRoots(double[] P) {
        double a = P[0];
        double b = P[1];
        double c = P[2];
        double d = P[3];

        double A = b / a;
        double B = c / a;
        double C = d / a;

        double Q, R, D, S, T, Im;

        Q = (3 * B - Math.pow(A, 2)) / 9;
        R = (9 * A * B - 27 * C - 2 * Math.pow(A, 3)) / 54;
        D = Math.pow(Q, 3) + Math.pow(R, 2);    // polynomial discriminant

        double[] t = new double[3];

        if (D >= 0)                                 // complex or duplicate roots
        {
            S = sgn(R + Math.sqrt(D))
                    * Math.pow(Math.abs(R + Math.sqrt(D)), (1.0 / 3));
            T = sgn(R - Math.sqrt(D))
                    * Math.pow(Math.abs(R - Math.sqrt(D)), (1.0 / 3));

            t[0] = -A / 3 + (S + T);                    // real root
            t[1] = -A / 3 - (S + T) / 2;                  // real part of complex root
            t[2] = -A / 3 - (S + T) / 2;                  // real part of complex root
            Im = Math.abs(Math.sqrt(3) * (S - T) / 2);    // complex part of root pair

        /*discard complex roots*/
            if (Im != 0) {
                t[1] = -1;
                t[2] = -1;
            }

        } else                                          // distinct real roots
        {
            double th = Math.acos(R / Math.sqrt(-Math.pow(Q, 3)));

            t[0] = 2 * Math.sqrt(-Q) * Math.cos(th / 3) - A / 3;
            t[1] = 2 * Math.sqrt(-Q) * Math.cos((th + 2 * Math.PI) / 3) - A / 3;
            t[2] = 2 * Math.sqrt(-Q) * Math.cos((th + 4 * Math.PI) / 3) - A / 3;
            Im = 0.0;
        }

    /*discard out of spec roots*/
        for (int i = 0; i < 3; i++)
            if (t[i] < 0 || t[i] > 1.0) t[i] = -1;

    /*sort but place -1 at the end*/
        Arrays.sort(t);

        return t;
    }

    private static int sgn(double number) {
        return number < 0.0 ? -1 : 1;
    }

    private static double[] bezierCoefficients(double P0, double P1, double P2, double P3) {
        double[] Z = new double[4];
        Z[0] = -P0 + 3 * P1 + -3 * P2 + P3;
        Z[1] = 3 * P0 - 6 * P1 + 3 * P2;
        Z[2] = -3 * P0 + 3 * P1;
        Z[3] = P0;
        return Z;
    }

    private static double _bezier_point(double t, double start, double control_1,
                                        double control_2, double end) {
    /* Formula from Wikipedia article on Bezier curves. */
        return start * (1.0 - t) * (1.0 - t) * (1.0 - t)
                + 3.0 * control_1 * (1.0 - t) * (1.0 - t) * t
                + 3.0 * control_2 * (1.0 - t) * t * t
                + end * t * t * t;
    }

    public static double bezier_length(Arc c) {
        double t;
        int i;
        int steps;
        int dotx;
        int doty;
        int previous_dotx = 0;
        int previous_doty = 0;
        double length = 0.0;
        steps = 10;
        for (i = 0; i <= steps; i++) {
            t = (double) i / (double) steps;
            dotx = (int) _bezier_point(t, c.getStartPos().getX(),
                    c.getQ().getX(),
                    c.getQ().getX(),
                    c.getEndPos().getX());
            doty = (int) _bezier_point(t, c.getStartPos().getY(),
                    c.getQ().getY(),
                    c.getQ().getY(),
                    c.getEndPos().getY());
            if (i > 0) {
                double x_diff = dotx - previous_dotx;
                double y_diff = doty - previous_doty;
                length += Math.sqrt(x_diff * x_diff + y_diff * y_diff);
            }
            previous_dotx = dotx;
            previous_doty = doty;
        }
        return length;
    }

}
