package com.guokr.dbn.layers;

import static com.guokr.dbn.util.ANNUtils.biased;
import static com.guokr.dbn.util.MathUtils.binomial;
import static com.guokr.dbn.util.MathUtils.sigmoid;
import static com.guokr.dbn.util.MatrixUtils.compose22;
import static com.guokr.dbn.util.MatrixUtils.random;
import static com.guokr.dbn.util.MatrixUtils.tensorProduct;
import static com.guokr.dbn.util.MatrixUtils.zero;
import mikera.matrixx.AMatrix;
import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public class RBM {

    public int     vnum;
    public int     hnum;
    public AMatrix weights;

    public RBM(int vnum, int hnum, AMatrix weights) {
        this.vnum = vnum;
        this.hnum = hnum;

        if (weights != null) {
            this.weights = weights;
        } else {
            double alpha = 1.0 / this.vnum;
            IMatrix rand = random(vnum, hnum, -alpha, alpha);

            this.weights = compose22(zero(1, 1), zero(1, hnum), zero(vnum, 1), rand);
        }
    }

    public double up(AVector vsample, AVector vweight) {
        return sigmoid(vsample.innerProduct(vweight).value);
    }

    public double down(AVector hsample, AVector hweight) {
        return sigmoid(hsample.innerProduct(hweight).value);
    }

    public void hsample_under_v(AVector hsample, AVector hmean, AVector vsample) {
        for (int i = 0; i < hnum + 1; i++) {
            hmean.set(i, up(vsample, weights.getColumn(i)));
            hsample.set(i, binomial(1, hmean.get(i)));
        }
        hmean.set(0, 1);
        hsample.set(0, 1);
    }

    public void vsample_under_h(AVector vsample, AVector vmean, AVector hsample) {
        for (int i = 0; i < vnum + 1; i++) {
            vmean.set(i, down(hsample, weights.getRow(i)));
            vsample.set(i, binomial(1, vmean.get(i)));
        }
        vmean.set(0, 1);
        vsample.set(0, 1);
    }

    public void gibbs_hvh(AVector phsample, AVector nvmeans, AVector nvsamples, AVector nhmeans, AVector nhsamples) {
        vsample_under_h(nvsamples, nvmeans, phsample);
        hsample_under_v(nhsamples, nhmeans, nvsamples);
    }

    public void contrastive_divergence(int k, double learning_rate, AVector input) {
        AVector phmean = biased(hnum);
        AVector phsample = biased(hnum);

        AVector nvmeans = biased(vnum);
        AVector nvsamples = biased(vnum);

        AVector nhmeans = biased(hnum);
        AVector nhsamples = biased(hnum);

        hsample_under_v(phsample, phmean, input);

        for (int step = 0; step < k; step++) {
            if (step == 0) {
                gibbs_hvh(phsample, nvmeans, nvsamples, nhmeans, nhsamples);
            } else {
                gibbs_hvh(nhsamples, nvmeans, nvsamples, nhmeans, nhsamples);
            }
        }

        IMatrix mp = tensorProduct(input, phmean);
        IMatrix mn = tensorProduct(nvsamples, nhmeans);

        mp.scale(learning_rate);
        mn.scale(-learning_rate);

        weights.add(mp);
        weights.add(mn);
    }

    public void reconstruct(AVector vrecons, AVector vsample) {
        AVector h = Vectorz.newVector(hnum + 1);

        for (int i = 0; i < hnum + 1; i++) {
            h.set(i, up(vsample, weights.getColumn(i)));
        }

        for (int i = 0; i < vnum + 1; i++) {
            vrecons.set(i, sigmoid(weights.getRow(i).innerProduct(h).value));
        }
    }

}
