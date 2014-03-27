package com.guokr.dbn;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

import org.junit.Assert;
import org.junit.Test;

import com.guokr.dbn.DBN;

public class DBNTest {

    @Test
    public void test() {

        int[] sizes_per_layer = { 6, 3, 3, 2 };
        DBN dbn = new DBN(sizes_per_layer);

        // pretrain

        int k = 1;
        double pretrain_lr = 0.1 / 6;
        int pretraining_epochs = 1000;

        double[][] traindata = { { 1, 1, 1, 0, 0, 0 }, { 1, 0, 1, 0, 0, 0 }, { 1, 1, 1, 0, 0, 0 },
                { 0, 0, 1, 1, 1, 0 }, { 0, 0, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 0 } };

        for (int i = 0; i < pretraining_epochs; i++) {
            for (double[] item : traindata) {
                dbn.pretrain(k, pretrain_lr, Vectorz.create(item));
            }
        }

        // finetune

        double finetune_lr = 0.1;
        int finetune_epochs = 500;

        double[][] tunedata = { { 1, 0 }, { 1, 0 }, { 1, 0 }, { 0, 1 }, { 0, 1 }, { 0, 1 }, };

        for (int i = 0; i < finetune_epochs; i++) {
            for (int j = 0; j < 6; j++) {
                dbn.finetune(finetune_lr, Vectorz.create(traindata[j]), Vectorz.create(tunedata[j]));
            }
        }

        // test

        double[][] testdata = { { 1, 1, 0, 0, 0, 0 }, { 1, 1, 1, 1, 0, 0 }, { 0, 0, 0, 1, 1, 0 }, { 0, 0, 1, 1, 1, 0 }, };
        double[][] results = { { 1, 0 }, { 1, 0 }, { 0, 1 }, { 0, 1 }, };

        for (int i = 0; i < 4; i++) {
            AVector result = dbn.predict(Vectorz.create(testdata[i]));
            System.out.println(result);
            AVector test = Vectorz.create(results[i]);
            Assert.assertTrue("error is greater than expected!", test.epsilonEquals(result, 0.3));
        }
    }

}
