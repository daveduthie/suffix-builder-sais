package com.ystervark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.StringTokenizer;

public class SuffixArray {

	public static void prn(Object... os) {
		for (Object e : os)
			System.out.print(e + " ");
		System.out.println();
	}

	public static void prnBool(boolean[] bs) {
		for (int i = 0; i < bs.length; ++i) {
			if (bs[i]) {
				System.out.println(i);
			}
		}
		System.out.println();
	}

	static class fastscanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		fastscanner() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() throws IOException {
			while (!tok.hasMoreElements())
				tok = new StringTokenizer(in.readLine());
			return tok.nextToken();
		}

		int nextint() throws IOException {
			return Integer.parseInt(next());
		}
	}

	public static void main(String[] args) throws IOException {
		fastscanner scanner = new fastscanner();
		String text = scanner.next() + "$";
		int[] suffixArray = compute(text);
		int patternCount = scanner.nextint();
		boolean[] occurs = new boolean[text.length()];
		for (int patternIndex = 0; patternIndex < patternCount; ++patternIndex) {
			String pattern = scanner.next();
			HashSet<Integer> occurrences = matchesInText(text, suffixArray, pattern);
			for (int x : occurrences) {
				occurs[x] = true;
			}
		}

		prnBool(occurs);

		// stressTest();
	}

	public static int[] compute(String s) {
		Numeric sa = new Numeric(new int[s.length()], 0, s.length());
		Text text = new Alphabetic(s.toCharArray());
		Buckets buckets = new Buckets();
		SAIS(text, sa, buckets);

		// for (int i = 0; i < sa.size(); ++i) {
		// int x = sa.get_at(i);
		// prn(i, x, "->", s.substring(x));
		// }

		return sa.value();
	}

	public static int suffixCompare(String text, int[] suffixArray, String pattern, int pos, int skip) {
		// prn("called suffixCompare at", pos, "-->", pattern, "==", text.substring(pos), "?");
		/* start skip after beginning of pattern */
		int i = skip;
		/*
		 * start at skip after pos'th lexicographically-sorted suffix of text
		 */
		int j = suffixArray[pos] + skip;
		while (i < pattern.length() && j < text.length()) {
			char p = pattern.charAt(i);
			char t = text.charAt(j);
			if (p > t) {
				return 1;
			} else if (p < t) {
				return -1;
			} else {
				i += 1;
				j += 1;
			}
		}
		return 0;
	}

	public static int binarySearch(String text, int[] suffixArray, String pattern, int skip, boolean first) {
		// prn("searching for", (first) ? "first" : "last", "position of", pattern, "in", text);

		int lo = 0;
		int hi = suffixArray.length - 1;

		while (lo <= hi) {
			int mid = (lo + hi) / 2;
			// prn("searching for first between", lo, "-->", mid, "<--", hi);
			int cmp = suffixCompare(text, suffixArray, pattern, mid, skip);
			// prn("cmp:", cmp);
			if (cmp == 0) {
				if (first) {
					if (mid == 0 || suffixCompare(text, suffixArray, pattern, mid - 1, skip) > 0) {
						// prn("gotcha:", mid);
						return mid;
					} else {
						hi = mid;
					}
				} else {
					if (mid == suffixArray.length - 1 || suffixCompare(text, suffixArray, pattern, mid + 1, skip) < 0) {
						// prn("gotcha:", mid);
						return mid;
					} else {
						lo = mid + 1;
					}
				}
			} else if (cmp < 0) {
				// gotta look lower
				hi = mid;
			} else if (cmp > 0) {
				// gotta look higher
				lo = mid + 1;
			} else {
				throw new IllegalStateException("tantrum");
			}
		}

		// prn("cannot find", pattern);
		return -1;
	}

	public static HashSet<Integer> matchesInText(String text, int[] sa, String pattern) {
		int skip = 0;

		// find first idx of match
		int firstIdx = binarySearch(text, sa, pattern, skip, true);
		// find last idx of match
		int lastIdx = binarySearch(text, sa, pattern, skip, false);
		// prn("matches are between", firstIdx, "and", lastIdx);

		HashSet<Integer> result = new HashSet<Integer>();
		if (firstIdx != -1) {
			for (int i = firstIdx; i <= lastIdx; ++i) {
				result.add(sa[i]);
				// prn(sa[i]);
			}
		}

		return result;
	}

	public static HashSet<Integer> multiMatchesInText(String text, String[] patterns) {
		int[] sa = compute(text);
		HashSet<Integer> locations = new HashSet<Integer>();
		for (String p : patterns) {
			// prn("examining", p);
			HashSet<Integer> matches = matchesInText(text, sa, p);
			locations.addAll(matches);
		}

		return locations;
	}

	public static interface Text {

		public int size();

		public int get_at(int i);

		public void set_at(int i, int v);

		public void pat_down();

		public void pat_down(int start, int end);

		public String toString();

		public int[] value();
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

		public int[] value() {
			int[] ret = new int[source.length];
			for (int i = 0; i < source.length; ++i) {
				ret[i] = (int) source[i];
			}
			return ret;
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

		public int[] value() {
			return source;
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

	public static void SAIS(Text text, Text sa, Buckets buckets) {
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
		for (int i = 0; i < sa.size(); ++i) {
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
		for (int i = sa.size() - 1; i >= 0; --i) {
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
			Text text_prime = new Numeric(sa.value(), half, half + num_w_strs);
			Numeric sa_prime = new Numeric(sa.value(), 0, num_w_strs);

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
		// counts holds the number of occurrences of `e` at `counts[e]`
		private int[] counts;
		// pointers holds a pointer to the head or tail of bucket `e` at
		// `pointers[e]`
		private int[] pointers;

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
			int alph_size = (text.size() > 512) ? text.size() / 2 : 256;
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
		}

		public void get_head_ptrs() {
			for (int i = 0, acc = 0; i < vocab.length; ++i) {
				int e = vocab[i];
				pointers[e] = acc;
				acc += counts[e];
			}
		}

		public void get_tail_ptrs() {
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
