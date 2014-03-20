package com.guokr.neuseg.util;

import java.util.Iterator;

public class NGramIterator implements Iterator<CharSequence> {

    private char[]        ring;
    private int           n;
    private int           cur;

    private String        line;
    private int           len;
    private int           pos;

    private StringBuilder last;

    public NGramIterator(int n) {
        this.n = n;
        this.cur = 0;
        this.ring = new char[n];
    }

    public void reset(String s) {
        cur = 0;
        pos = n;
        len = s.length();
        if (len >= n) {
            line = s;
            for (int i = 0; i < n; i++) {
                ring[i] = line.charAt(i);
            }
        } else {
            line = "";
        }
    }

    private void poke(char c) {
        ring[cur] = c;
        cur = (cur + 1) % n;
    }

    private StringBuilder peek() {
        StringBuilder builder = new StringBuilder();
        if (len >= n) {
            for (int i = 0; i < n; i++) {
                builder.append(ring[(cur + i) % n]);
            }
        }
        return builder;
    }

    @Override
    public boolean hasNext() {
        return pos < len + n;
    }

    @Override
    public StringBuilder next() {
        if (pos <= len) {
            StringBuilder val = peek();
            last = val;
            if (pos < len && len >= n) {
                poke(line.charAt(pos));
            }
            pos++;
            return val;
        } else {
            pos++;
            return last;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}