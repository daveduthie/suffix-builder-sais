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

		public void set_at(int i, int v) {
			source[i] = (char) v;
		}
	}

	public static final class Numeric implements Text {
		private int[] source;
		// We're sharing arrays, so have to delineate which part of the array
		// we're allowed to work with
		private int begin;
		private int end;// points to last index + 1

		public Numeric(int[] text, int begin, int end) {
			this.begin = begin;
			this.end = end;
			this.source = text;
		}

		public int size() {
			return end - begin;
		}

		public int get_at(int i) {
			return source[begin + i];
		}

		public void set_at(int i, int v) {
			source[begin + i] = v;
		}
	}

	public static enum SuffixType {
		ASCENDING, DESCENDING, VALLEY
	}

	public static void computeSuffixTypes(Text t, SuffixType[] types) {
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
	}

	public static boolean wStringsEqual(int w1, int w2, Text text, SuffixType[] types) {
		int i = w1;
		int j = w2;
		while (true) {
			if (text.get_at(i) != text.get_at(j) || types[i] != types[j])
				return false;
			if ((types[i] == SuffixType.VALLEY || types[j] == SuffixType.VALLEY) && i != w1)
				return true;
			i++;
			j++;
		}
	}

	public static class Buckets {
		// vocab holds a (usually unsorted) array of chars/ints in a Text
		private int[] vocab;
		private int v_end;
		// counts holds the number of occurences of `e` at `counts[e]`
		private int[] counts;
		// pointers holds a pointer to the head or tail of bucket `e` at
		// `pointers[e]`
		private int[] pointers;
		private boolean built_vocab = false;

		public Buckets(int n) {
			this.vocab = new int[n];
			this.counts = new int[n];
			this.v_end = 0;
		}

		public void insert_vocab(int e) {
			if (e >= counts.length) {
				// counts = java.util.Arrays.copyOf(counts, e + 1);
				throw new IllegalArgumentException(
						"You said your alphabet was < " + vocab.length + " but now you've given me a " + e + "!");
			}

			// this means it's the first time we're ecountering this
			// character
			if (counts[e] == 0)
				vocab[v_end++] = e;
			counts[e] += 1;
		}

		public int get_count(int e) {
			if (e >= counts.length)
				throw new IllegalArgumentException(e + " is not in the current vocabulary");
			return counts[e];
		}

		public void build_vocab_list(Text t) {
			vocab = new int[vocab.length];
			for (int i = 0; i < t.size(); ++i)
				insert_vocab(t.get_at(i));
			vocab = java.util.Arrays.copyOf(vocab, v_end);// trim to size
			java.util.Arrays.sort(vocab);// or else all the zeroes pop to front

			// this is big (!) but it simplifies element access
			int last_in_vocab = vocab[vocab.length - 1];
			pointers = new int[last_in_vocab + 1];

			built_vocab = true;
		}

		public void get_head_ptrs() {
			if (!built_vocab)
				throw new IllegalStateException("Muffed it! You need to build the vocab list first.");

			for (int i = 0, acc = 0; i < vocab.length; ++i) {
				int e = vocab[i];
				pointers[e] = acc;
				acc += counts[e];
			}
		}

		public void get_tail_ptrs() {
			if (!built_vocab)
				throw new IllegalStateException("Muffed it! You need to build the vocab list first.");

			for (int i = 0, acc = 0; i < vocab.length; ++i) {
				int e = vocab[i];
				acc += counts[e]; // acc now points to first head of next bucket
				pointers[e] = acc - 1;
			}
		}

		public int get_pointer(int i) {
			return pointers[i];
		}

		public int get_vocab(int i) {
			return vocab[i];
		}
	}
}
