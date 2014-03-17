package com.guokr.neuseg.ngram;

import java.util.Iterator;

public class NGram {

	public static Iterator<CharSequence> ngram(int n, String line)
			throws Exception {
		NGramIterator iter = new NGramIterator(n);
		iter.reset(line);
		return iter;
	}

	public static Iterator<CharSequence> unigram(String line) throws Exception {
		NGramIterator iter = new NGramIterator(1);
		iter.reset(line);
		return iter;
	}

	public static Iterator<CharSequence> bigram(String line) throws Exception {
		NGramIterator iter = new NGramIterator(2);
		iter.reset(line);
		return iter;
	}

	public static Iterator<CharSequence> trigram(String line) throws Exception {
		NGramIterator iter = new NGramIterator(3);
		iter.reset(line);
		return iter;
	}

	public static Iterator<CharSequence> quadgram(String line) throws Exception {
		NGramIterator iter = new NGramIterator(4);
		iter.reset(line);
		return iter;
	}

}
