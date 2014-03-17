package com.guokr.neuseg.util;

import java.util.Iterator;

public class NGramIterator implements Iterator<CharSequence> {

	private char[] ring;
	private int n;
	private int cur;

	private String line;
	private int len;
	private int pos;

	public NGramIterator(int n) {
		this.n = n;
		this.cur = 0;
		this.ring = new char[n];
	}

	public void reset(String s) {
		cur = 0;
		len = s.length();
		if (len >= n) {
			line = s;
			for (int i = 0; i < n; i++) {
				ring[i] = line.charAt(i);
			}
			pos = n;
		} else {
			len = 0;
			line = "";
			pos = 0;
		}
	}

	private void poke(char c) {
		ring[cur] = c;
		cur = (cur + 1) % n;
	}

	private StringBuilder peek() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < n; i++) {
			builder.append(ring[(cur + i) % n]);
		}
		return builder;
	}

	@Override
	public boolean hasNext() {
		return pos <= len;
	}

	@Override
	public StringBuilder next() {
		StringBuilder val = peek();
		if (pos < len) {
			poke(line.charAt(pos));
		}
		pos++;
		return val;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}