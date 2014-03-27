package com.guokr.dbn.layers;

import static com.guokr.dbn.util.MatrixUtils.compose12;
import static com.guokr.dbn.util.MatrixUtils.opSoftmax;
import static com.guokr.dbn.util.MatrixUtils.tensorProduct;
import static com.guokr.dbn.util.MatrixUtils.zero;
import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public class Output {
    public int     inum;
    public int     onum;
    public IMatrix weights;

    public Output(int inum, int onum) {
        this.inum = inum;
        this.onum = onum;

        this.weights = compose12(zero(onum, 1), zero(onum, inum));
    }

    public void train(double learning_rate, AVector x, AVector y) {
        x.set(0, 1);

        AVector py_x = weights.transform(x);
        py_x.applyOp(opSoftmax(py_x));
        py_x.scale(-1);

        AVector dy = Vectorz.create(y);
        dy.add(py_x);
        dy.scale(learning_rate);

        this.weights.add(tensorProduct(dy, x));
    }

    public void predict(AVector x, AVector y) {
        y.set(weights.transform(x));
        y.applyOp(opSoftmax(y));
    }

}
