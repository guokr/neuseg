package com.guokr.dbn;

import static com.guokr.dbn.util.ANNUtils.biased;
import static com.guokr.dbn.util.MatrixUtils.opSigmoid;
import static com.guokr.dbn.util.MatrixUtils.opSoftmax;
import static com.guokr.dbn.util.MatrixUtils.transpose;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

import com.guokr.dbn.layers.Hidden;
import com.guokr.dbn.layers.Output;
import com.guokr.dbn.layers.RBM;

public class DBN {

    public int      inum;
    public int      onum;

    public int      lnum;
    public int[]    lsizes;

    public Hidden[] hlayers;
    public RBM[]    blayers;
    public Output   olayer;

    public DBN(int[] lsizes) {
        this.inum = lsizes[0];
        this.onum = lsizes[lsizes.length - 1];
        this.lnum = lsizes.length;
        this.lsizes = lsizes;

        this.hlayers = new Hidden[this.lnum - 2];
        this.blayers = new RBM[this.lnum - 2];

        for (int i = 0; i < this.lnum - 2; i++) {
            int isize = lsizes[i];
            int osize = lsizes[i + 1];

            Hidden sigmoidLayer = new Hidden(isize, osize);
            this.hlayers[i] = sigmoidLayer;
            this.blayers[i] = new RBM(isize, osize, transpose(sigmoidLayer.weights));
        }

        this.olayer = new Output(lsizes[lsizes.length - 2], this.onum);
    }

    public void pretrain(int k, double learning_rate, AVector input) {
        AVector icur = null, iprev = null;
        for (int i = 0; i < lnum - 2; i++) { // layer-wise
            for (int l = 0; l <= i; l++) {
                if (l == 0) {
                    icur = biased(input);
                } else {
                    iprev = icur.clone();

                    icur = biased(lsizes[l]);
                    hlayers[l - 1].osample_under_i(icur, iprev);
                }
            }

            blayers[i].contrastive_divergence(k, learning_rate, icur);
        }
    }

    public void finetune(double learning_rate, AVector input, AVector result) {
        input = biased(input);

        AVector icur, iprev;
        iprev = input;
        icur = biased(lsizes[1]);
        hlayers[0].osample_under_i(icur, iprev);

        for (int i = 1; i < lnum - 2; i++) {
            iprev = icur.clone();
            icur = biased(Vectorz.newVector(lsizes[i + 1]));
            hlayers[i].osample_under_i(icur, iprev);
        }

        olayer.train(learning_rate, icur, result);
    }

    public AVector predict(AVector input) {
        input = biased(input);

        AVector icur = null, iprev = input;

        for (int i = 0; i < lnum - 2; i++) {
            Hidden lcur = hlayers[i];

            icur = lcur.weights.transform(iprev);
            icur.applyOp(opSigmoid);

            iprev = icur;
        }

        AVector result = olayer.weights.transform(icur);
        result.applyOp(opSoftmax(result));

        return result;
    }
}
