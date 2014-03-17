package com.guokr.neuseg.util;

import java.util.Iterator;

public class NGram {

	public static Iterator<CharSequence> ngram(int n, String text)
			throws Exception {
		NGramIterator iter = new NGramIterator(n);
		iter.reset(text);
		return iter;
	}

	public static Iterator<CharSequence> unigram(String text) throws Exception {
		NGramIterator iter = new NGramIterator(1);
		iter.reset(text);
		return iter;
	}

	public static Iterator<CharSequence> bigram(String text) throws Exception {
		NGramIterator iter = new NGramIterator(2);
		iter.reset(text);
		return iter;
	}

	public static Iterator<CharSequence> trigram(String text) throws Exception {
		NGramIterator iter = new NGramIterator(3);
		iter.reset(text);
		return iter;
	}

	public static Iterator<CharSequence> quadgram(String text) throws Exception {
		NGramIterator iter = new NGramIterator(4);
		iter.reset(text);
		return iter;
	}

}
