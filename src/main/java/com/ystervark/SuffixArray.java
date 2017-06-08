package com.ystervark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class SuffixArray {

	public static void prn(Object... os) {
		for (Object e : os)
			System.out.print(e + " ");
		System.out.println();
	}

	public static void prnText(Text t) {
		for (int i = 0; i < t.size(); ++i) {
			System.out.print(t.get_at(i) + " ");
		}
		System.out.println();
	}

	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new
		InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);

		String text = in.readLine();
		int[] result = compute(text);

		for (int e : result)
		out.println(e);

		in.close();
		out.close();

		// stressTest();
	}

	public static int[] compute(String s) {
		SuffArray sa = new SuffArray(new int[s.length()], 0, s.length());
		Text text = new Alphabetic(s.toCharArray());
		Buckets buckets = new Buckets();
		SAIS(text, sa, buckets);

		return sa.value();
	}

	public static interface Text {

		public int size();

		public int get_at(int i);

		public void set_at(int i, int v);

		public void pat_down();

		public void pat_down(int start, int end);

		public String toString();
	}

	public static class Alphabetic implements Text {
		private char[] source;

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

		public void pat_down() {
			for (int i = 0; i < size(); ++i)
				set_at(i, -1);
		}

		public void pat_down(int start, int end) {
			for (int i = start; i < end; ++i)
				set_at(i, -1);
		}

		public String toString() {
			return Arrays.toString(source);
		}
	}

	public static class SuffArray implements Text {
		private int[] source;
		private int begin;
		private int end;

		public SuffArray(int[] source, int begin, int end) {
			this.source = source;
			this.begin = begin;
			this.end = end;
		}

		public final int size() {
			return end - begin;
		}

		public int get_at(int i) {
			return source[begin + i];
		}

		public void set_at(int i, int v) {
			source[begin + i] = v;
		}

		public int[] value() {
			return source;
		}

		public void pat_down() {
			for (int i = 0; i < size(); ++i)
				set_at(i, -1);
		}

		public void pat_down(int start, int end) {
			for (int i = start; i < end; ++i)
				set_at(i, -1);
		}

		public String toString() {
			return Arrays.toString(source);
		}
	}

	public static class Numeric implements Text {
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

		public void pat_down() {
			for (int i = 0; i < size(); ++i)
				set_at(i, -1);
		}

		public void pat_down(int start, int end) {
			for (int i = start; i < end; ++i)
				set_at(i, -1);
		}

		public String toString() {
			return Arrays.toString(source);
		}
	}

	public static enum SuffixType {
		ASCENDING, DESCENDING, VALLEY
	}

	public static SuffixType[] computeSuffixTypes(Text text) {
		SuffixType[] types = new SuffixType[text.size()];
		types[text.size() - 1] = SuffixType.VALLEY;
		for (int i = text.size() - 1; i > 0; --i) {
			int curr = text.get_at(i);
			int prev = text.get_at(i - 1);

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

	public static void SAIS(Text text, SuffArray sa, Buckets buckets) {
		if (text.size() == 1) {
			sa.set_at(0, 0);
			return;
		} else if (text.size() == 2) {
			sa.set_at(0, 1);
			sa.set_at(1, 0);
			return;
		}
		// prep
		// prn("begin SAIS");
		// prn(sa.toString());
		SuffixType[] types = computeSuffixTypes(text);
		buckets.build_vocab_list(text);

		// check types
		// for (SuffixType e : types)
		// System.out.print(e + " ");
		// System.out.println();

		// divide
		// STEP 0
		/// pat down the sand
		sa.pat_down();

		/// sort W-strings
		//// insert valley suffixes
		buckets.get_tail_ptrs();

		/**
		 * FIXME This is a puzzle: if I loop forwards, one example passes, but
		 * if I loop backwards, the other example passes I actually think the
		 * grader may be wrong. hmmmmm...
		 */
		// for (int i = 0; i < text.size(); ++i) {
		for (int i = text.size() - 1; i >= 0; --i) {
			if (types[i] == SuffixType.VALLEY) {
				int ch = text.get_at(i);
				int idx = buckets.get_tail_ptr(ch);
				sa.set_at(idx, i);
			}
		}
		// prn("after step 0:");
		// prnText(sa);
		// STEP 1
		//// insert down prefixes
		buckets.get_head_ptrs();
		for (int i = 0; i < sa.end; ++i) {
			int curr = sa.get_at(i);
			if (curr > 0) {
				if (types[curr - 1] == SuffixType.DESCENDING) {
					int ch = text.get_at(curr - 1);
					int idx = buckets.get_head_ptr(ch);
					sa.set_at(idx, curr - 1);
				}
			}
		}
		// prn("after step 1:");
		// prnText(sa);
		// STEP 2
		//// insert up prefixes
		buckets.get_tail_ptrs();
		for (int i = sa.end - 1; i >= 0; --i) {
			int curr = sa.get_at(i);
			if (curr > 0) {
				if (types[curr - 1] == SuffixType.ASCENDING || types[curr - 1] == SuffixType.VALLEY) {
					int ch = text.get_at(curr - 1);
					int idx = buckets.get_tail_ptr(ch);
					sa.set_at(idx, curr - 1);
				}
			}
		}
		// prn("after step 2:");
		// prnText(sa);
		/// construct T'
		//// pack w-strings into left half of SA
		int num_w_strs = 0;
		for (int i = 0; i < sa.size(); ++i) {
			int idx = sa.get_at(i);
			sa.set_at(i, -1);
			if (types[idx] == SuffixType.VALLEY) {
				sa.set_at(num_w_strs, idx);
				num_w_strs++;
			}
		}
		// sa.pat_down(num_w_strs, sa.size());
		// prnText(sa);
		//// insert lexical names in right half of SA
		int rank = 0, prev_idx = 0;
		int half = sa.size() / 2;
		for (int i = 0; i < num_w_strs; ++i) {
			int curr_idx = sa.get_at(i);
			if (prev_idx == 0 || !wStringsEqual(prev_idx, curr_idx, text, types)) {
				rank++;
			}
			sa.set_at(half + curr_idx / 2, rank);
			prev_idx = curr_idx;
			// prnText(sa);
		}
		// prn("after inserting lexical names into right half:");
		// prnText(sa);
		//// compact lexical names to form the reduced instance T' (text_prime)
		// prn("creating T'");
		for (int i = half, j = half; i < sa.size(); ++i) {
			int curr = sa.get_at(i);
			// prn("curr:", curr);
			if (curr != -1) {
				sa.set_at(i, -1);
				sa.set_at(j, curr);
				j += 1;
				// prnText(sa);
			}
		}
		// prn("is this T' ?");
		// prnText(sa);
		// conquer
		/// test if recursion is required
		if (rank < num_w_strs) {
			//// recur!
			// prn("recursion!");
			types = null;// throw away to save memory. can recompute later
			Text text_prime = new Numeric(sa.source, half, half + num_w_strs);
			SuffArray sa_prime = new SuffArray(sa.source, 0, num_w_strs);

			SAIS(text_prime, sa_prime, buckets);

			types = computeSuffixTypes(text);
			buckets.build_vocab_list(text);

			// prn("post-recursion:");
			// prnText(sa);
			/// reconstruct order of w-suffixes from T'
			//// place w-suffixes into T'
			for (int i = 0, pos = 0; i < text.size(); ++i)
				if (types[i] == SuffixType.VALLEY)
					sa.set_at(half + pos++, i);
			// prn("checkpoint:");
			// prnText(sa);
			//// replace SA' with w-suffixes
			for (int i = 0; i < num_w_strs; ++i) {
				int curr = sa.get_at(i);
				int idx = sa.get_at(half + curr);
				sa.set_at(i, idx);
			}
			// prn("checkpoint 2:");
			// prnText(sa);
		}

		// prnText(sa);
		// combine
		// prn("starting combine phase");
		/// pat down sand (leave w-suffixes)
		sa.pat_down(num_w_strs, sa.size());
		// prnText(sa);
		/// move w-suffixes into buckets
		// prn("move w-suffixes into buckets:");
		buckets.get_tail_ptrs();
		for (int i = num_w_strs - 1; i >= 0; --i) {
			// prn("begin", i);
			int curr = sa.get_at(i);
			if (curr > 0) {
				int ch = text.get_at(curr);
				// prn("text:");
				// prnText(text);
				int idx = buckets.get_tail_ptr(ch);
				// prn("curr:", curr, "idx:", idx, "sa len:", sa.size(), "ch:",
				// ch);
				sa.set_at(i, -1);
				sa.set_at(idx, curr);
			}
		}
		// prnText(sa);
		/// left to right sweep
		// prn("insert down suffixes into head positions:");
		buckets.get_head_ptrs();
		for (int i = 0; i < sa.size(); ++i) {
			int curr = sa.get_at(i);
			if (curr > 0 && types[curr - 1] == SuffixType.DESCENDING) {
				int idx = buckets.get_head_ptr(text.get_at(curr - 1));
				sa.set_at(idx, curr - 1);
			}
		}
		// prnText(sa);
		/// right to left sweep
		// prn("insert up suffixes into head positions:");
		buckets.get_tail_ptrs();
		for (int i = sa.size() - 1; i >= 0; --i) {
			int curr = sa.get_at(i);
			if (curr > 0 && (types[curr - 1] == SuffixType.ASCENDING || types[curr - 1] == SuffixType.VALLEY)) {
				int idx = buckets.get_tail_ptr(text.get_at(curr - 1));
				sa.set_at(idx, curr - 1);
			}
		}
		// prn("voila?");
		// prnText(sa);
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

		public void insert_vocab(int e) {
			// prn("inserting", e, "into vocab");
			// prn("v_end=", v_end);
			if (e >= counts.length) {
				// counts = java.util.Arrays.copyOf(counts, e + 1);
				throw new IllegalArgumentException(
						"You said your alphabet was < " + vocab.length + " but now you've given me an " + e + "!");
			}

			// this means it's the first time we're encountering this
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

		public void build_vocab_list(Text text) {
			int alph_size = (text.size() > 256) ? text.size() : 256;
			this.vocab = new int[alph_size];
			this.counts = new int[alph_size];
			this.v_end = 0;
			for (int i = 0; i < text.size(); ++i)
				insert_vocab(text.get_at(i));
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

		public int get_tail_ptr(int i) {
			int result = pointers[i];
			pointers[i] -= 1;
			return result;
		}

		public int get_head_ptr(int i) {
			int result = pointers[i];
			pointers[i] += 1;
			return result;
		}

		public int get_vocab(int i) {
			return vocab[i];
		}
	}

	public static int[] naiveSuffixArray(String s) {
		String[] str = new String[s.length()];
		int[] res = new int[s.length()];

		for (int i = 0; i < s.length(); ++i) {
			str[i] = s.substring(i);
		}
		Arrays.sort(str);

		for (int i = 0; i < s.length(); ++i) {
			res[i] = s.length() - str[i].length();
		}

		// for (int i = 0; i < str.length; ++i) {
		// System.out.println(res[i] + " -> " + str[i]);
		// }

		return res;
	}

	public static void stressTest() {
		while (true) {
			Random r = new Random();
			int len = r.nextInt(120);
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < len; ++i) {
				char c = (char) (r.nextInt(26) + 65);
				b.append(c);
			}
			b.append('$');
			String s = b.toString();
			prn("str:", s);

			int[] expect = naiveSuffixArray(s);
			int[] actual = compute(s);

			if (!Arrays.equals(expect, actual)) {
				throw new IllegalStateException("Bliksem! It breaks on input:\n" + s);
			}
		}
	}
}
