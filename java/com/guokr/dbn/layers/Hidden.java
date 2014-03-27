package com.guokr.dbn.layers;

import static com.guokr.dbn.util.MathUtils.binomial;
import static com.guokr.dbn.util.MathUtils.sigmoid;
import static com.guokr.dbn.util.MatrixUtils.compose22;
import static com.guokr.dbn.util.MatrixUtils.random;
import static com.guokr.dbn.util.MatrixUtils.zero;
import mikera.matrixx.AMatrix;
import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;

public class Hidden {
    public int     inum;
    public int     onum;
    public AMatrix weights;

    public Hidden(int inum, int onum) {
        this.inum = inum;
        this.onum = onum;

        double alpha = 1.0 / this.inum;
        IMatrix rand = random(onum, inum, -alpha, alpha);

        this.weights = compose22(zero(1, 1), zero(1, inum), zero(onum, 1), rand);
    }

    public void osample_under_i(AVector osample, AVector isample) {
        for (int i = 0; i < onum + 1; i++) {
            isample.set(0, 1);
            osample.set(i, binomial(1, sigmoid(isample.innerProduct(weights.getRow(i)).value)));
            osample.set(0, 1);
        }
    }
}
