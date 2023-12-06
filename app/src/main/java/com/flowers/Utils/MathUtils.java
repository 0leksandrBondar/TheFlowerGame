package com.flowers.Utils;

import static java.lang.Math.sqrt;

public class MathUtils {

    public static boolean equals(float a, float b, float Epsilon) {
        return a == b ? true : Math.abs(a - b) < Epsilon;
    }

    public static float vectorLen(float x, float y) {
        return (float) sqrt(x * x + y * y);
    }
}
