package com.guokr.dbn.util;

import static com.guokr.dbn.util.MatrixUtils.zero;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public class ANNUtils {

    public static AVector biased(AVector input) {
        AVector b = Vectorz.newVector(input.length() + 1);
        b.set(0, 1);
        for (int i = 0; i < input.length(); i++) {
            b.set(i + 1, input.get(i));
        }
        return b;
    }

    public static AVector biased(int dim) {
        return biased(zero(dim));
    }

}
