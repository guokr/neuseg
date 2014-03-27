package com.guokr.dbn.util;

import static java.lang.Math.E;
import static java.lang.Math.pow;

import java.util.Date;
import java.util.Random;

public class MathUtils {

    public static Random rand = new Random(new Date().getTime());

    public static double uniform(double min, double max) {
        return rand.nextDouble() * (max - min) + min;
    }

    public static int binomial(int n, double p) {
        if (p < 0 || p > 1) {
            return 0;
        }

        int c = 0;
        for (int i = 0; i < n; i++) {
            double r = rand.nextDouble();
            if (r < p)
                c++;
        }

        return c;
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + pow(E, -x));
    }

}
