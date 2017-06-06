package com.ystervark;

public class SuffixArray {

	public static interface Text {
		public int size();

		public int get_at(int i);

		public void set_at(int k, int v);
	}

	public static final class Alphabetic implements Text {
		private char[] source;
		// What's this for? -- not going to define it until I understand this
		// private int pos;

		public Alphabetic(char[] c) {
			source = c;
		}

		public int size() {
			return source.length;
		}

		public int get_at(int i) {
			return source[i];
		}

		public void set_at(int k, int v) {
			source[k] = (char) v;
		}
	}

	public static final class Numeric implements Text {
		private int[] source;

		public Numeric(int[] ints) {
			this.source = ints;
		}

		public int size() {
			return source.length;
		}

		public int get_at(int i) {
			return source[i];
		}

		public void set_at(int k, int v) {
			source[k] = v;
		}
	}

	public static enum SuffixType {
		ASCENDING, DESCENDING, VALLEY
	}

	public static SuffixType[] computeSuffixTypes(Text t) {
		SuffixType[] types = new SuffixType[t.size()];
		types[t.size() - 1] = SuffixType.VALLEY;
		for (int i = t.size() - 1; i > 0; --i) {
			int curr = t.get_at(i);
			int prev = t.get_at(i - 1);

			// set prev type
			if (prev < curr) {
				types[i - 1] = SuffixType.ASCENDING;
			} else if (prev > curr) {
				types[i - 1] = SuffixType.DESCENDING;
				// check if curr type is a valley
				if (types[i] == SuffixType.ASCENDING)
					types[i] = SuffixType.VALLEY;
			} else {
				types[i - 1] = types[i];
			}
		}

		return types;
	}

	public static boolean wStringsEqual(int w1, int w2, Text text, SuffixType[] types) {
		int i = w1;
		int j = w2;
		while (true) {
			if (text.get_at(i) != text.get_at(j) || types[i] != types[j])
				return false;
			if ((types[i] == SuffixType.VALLEY || types[j] == SuffixType.VALLEY) && i != w1)
				return true;
		}
	}

	public static class Buckets {
		// vocab holds a (usually unsorted) array of chars/ints in a Text
		private int[] vocab;
		// counts holds the number of occurences of `e` at `counts[e]`
		private int[] counts;
		// pointers holds a pointer to the head or tail of bucket `e` at
		// `pointers[e]`
		private int[] pointers;

		public Buckets(int n) {
			this.vocab = new int[0];
			this.counts = new int[n];
		}

		public void insert_vocab(int e) {
			if (e >= vocab.length) {
				vocab = java.util.Arrays.copyOf(vocab, vocab.length + 1);
			}
			vocab[vocab.length - 1] = e;

			if (e >= counts.length) {
				counts = java.util.Arrays.copyOf(counts, e + 1);
				// or throw something
			}
			counts[e] += 1;
		}

		public int get_count(int e) {
			if (e >= counts.length)
				throw new IllegalArgumentException(e + " is not in the current vocabulary");
			return counts[e];
		}

		// calculate pointers to head of each \ bucket
		public void computeBucketBounds(Text t) {
			// rebuild vocab list
			vocab = new int[vocab.length];
			for (int i = 0; i < t.size(); ++i)
				insert_vocab(t.get_at(i));
			java.util.Arrays.sort(vocab);

			// iterate over vocab
			if (vocab[0] != 1)
				throw new IllegalStateException("Muffed it! There should be one $ in there.");
			int acc = 1;
			int last_in_vocab = vocab[vocab.length - 1];
			// this is big (!) but it simplifies element access
			pointers = new int[last_in_vocab + 1];
			for (int i = 1; i < vocab.length; ++i) {
				pointers[i - 1] = acc;
				int e = vocab[i];
				acc += counts[e];
			}
		}
	}
}
