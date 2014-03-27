package com.guokr.dbn.util;

import static com.guokr.dbn.util.MathUtils.sigmoid;
import static com.guokr.dbn.util.MathUtils.uniform;
import static java.lang.Math.exp;
import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.IMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.ABoundedOp;
import mikera.vectorz.ops.AFunctionOp;

public class MatrixUtils {

    public static class SoftMax extends ABoundedOp {

        private double max, sum = 0;

        public SoftMax(INDArray array) {
            max = array.elementMax();
            for (double elem : array.toDoubleArray()) {
                sum += exp(elem - max);
            }
        }

        @Override
        public double maxValue() {
            return 1;
        }

        @Override
        public double minValue() {
            return 0;
        }

        @Override
        public double apply(double x) {
            return exp(x - max) / sum;
        }

    }

    public static Op opSigmoid = new AFunctionOp() {
                                   @Override
                                   public double apply(double x) {
                                       return sigmoid(x);
                                   }

                                   @Override
                                   public double minValue() {
                                       return 0;
                                   }

                                   @Override
                                   public double maxValue() {
                                       return 1;
                                   }
                               };

    public static Op opSoftmax(INDArray x) {
        return new SoftMax(x);
    }

    public static AVector zero(int dim) {
        AVector v = Vectorz.newVector(dim);
        v.fill(0);
        return v;
    }

    public static AVector one(int dim) {
        AVector v = Vectorz.newVector(dim);
        v.fill(1);
        return v;
    }

    public static IMatrix zero(int rows, int columns) {
        AMatrix m = Matrixx.newMatrix(rows, columns);
        m.fill(0);
        return m;
    }

    public static IMatrix one(int rows, int columns) {
        AMatrix m = Matrixx.newMatrix(rows, columns);
        m.fill(1);
        return m;
    }

    public static IMatrix constant(int rows, int columns, double x) {
        AMatrix m = Matrixx.newMatrix(rows, columns);
        m.fill(x);
        return m;
    }

    public static IMatrix random(int rows, int columns, double min, double max) {
        AMatrix m = Matrixx.newMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                m.set(i, j, uniform(min, max));
            }
        }
        return m;
    }

    public static IMatrix compose12(IMatrix m11, IMatrix m12) {
        int r = m11.rowCount();
        int c1 = m11.columnCount(), c2 = m12.columnCount(), c = c1 + c2;
        AMatrix m = Matrixx.newMatrix(r, c);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c1; j++) {
                m.set(i, j, m11.get(i, j));
            }
        }
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c2; j++) {
                m.set(i, c1 + j, m12.get(i, j));
            }
        }
        return m;
    }

    public static IMatrix compose21(IMatrix m11, IMatrix m21) {
        int r1 = m11.rowCount(), r2 = m21.rowCount(), r = r1 + r2;
        int c = m11.columnCount();
        AMatrix m = Matrixx.newMatrix(r, c);
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c; j++) {
                m.set(i, j, m11.get(i, j));
            }
        }
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c; j++) {
                m.set(r1 + i, j, m21.get(i, j));
            }
        }
        return m;
    }

    public static AMatrix compose22(IMatrix m11, IMatrix m12, IMatrix m21, IMatrix m22) {
        int r1 = m11.rowCount(), r2 = m22.rowCount(), r = r1 + r2;
        int c1 = m11.columnCount(), c2 = m22.columnCount(), c = c1 + c2;
        AMatrix m = Matrixx.newMatrix(r, c);
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c1; j++) {
                m.set(i, j, m11.get(i, j));
            }
        }
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c1; j++) {
                m.set(r1 + i, j, m21.get(i, j));
            }
        }
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                m.set(i, c1 + j, m12.get(i, j));
            }
        }
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c2; j++) {
                m.set(r1 + i, c1 + j, m22.get(i, j));
            }
        }
        return m;
    }

    public static IMatrix tensorProduct(AVector a, AVector b) {
        int alen = a.length(), blen = b.length();
        AMatrix m = Matrixx.newMatrix(alen, blen);
        for (int i = 0; i < alen; i++) {
            for (int j = 0; j < blen; j++) {
                m.set(i, j, a.get(i) * b.get(j));
            }
        }
        return m;
    }

    public static AMatrix transpose(final AMatrix m) {
        return new AMatrix() {

            private static final long serialVersionUID = 7582775908242448103L;

            @Override
            public int rowCount() {
                return m.columnCount();
            }

            @Override
            public int columnCount() {
                return m.rowCount();
            }

            @Override
            public boolean isFullyMutable() {
                return true;
            }

            @Override
            public double get(int row, int col) {
                return m.get(col, row);
            }

            @Override
            public void set(int row, int col, double val) {
                m.set(col, row, val);
            }

            @Override
            public AMatrix exactClone() {
                return transpose(m.clone());
            }

        };
    }

}
