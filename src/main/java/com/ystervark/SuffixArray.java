package com.ystervark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * @author David Duthie
 *
 */
public class SuffixArray {

	public static void prn(Object... os) {
		for (Object e : os)
			System.out.print(e + " ");
		System.out.println();
	}

	public static void prnBool(boolean[] bs) {
		for (int i = 0; i < bs.length; ++i) {
			if (bs[i]) {
				System.out.print(i + " ");
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

		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
	}

	public static void main(String[] args) throws IOException {
		fastscanner scanner = new fastscanner();

		String text = scanner.next();

		int[] suffixArray = new int[text.length()];
		for (int i = 0; i < suffixArray.length; ++i) {
			suffixArray[i] = scanner.nextInt();
		}

		int[] lcpArray = new int[text.length() - 1];
		for (int i = 0; i < lcpArray.length; ++i) {
			lcpArray[i] = scanner.nextInt();
		}

		prn(text);

		TreeNode tree = makeTree(text, suffixArray, lcpArray);
		ArrayList<TreeNode> linearisedTree = new ArrayList<TreeNode>();
		depthFirstList(linearisedTree, text, tree);

		for (TreeNode n : linearisedTree) {
			prn(n.getEdgeStart(), n.getEdgeEnd() + 1);
		}

		// stressTest();
	}

	/**
	 * <summary>Takes a string {@code str} and returns an array of type
	 * {@code int[]}, consisting of indices into the string corresponding to the
	 * lexicographically sorted suffixes of {@code str}.</summary>
	 *
	 * @param str
	 *            the string for which to build the suffix array
	 * @return the lexicographically sorted suffixes of s
	 */
	public static int[] compute(String str) {
		Numeric sa = new Numeric(new int[str.length()], 0, str.length());
		Text text = new Alphabetic(str);
		Buckets buckets = new Buckets();
		SAIS(text, sa, buckets);

		return sa.value();
	}

	public static int suffixCompare(String text, int[] suffixArray, String pattern, int pos, int skip) {
		// prn("called suffixCompare at", pos, "-->", pattern, "==",
		// text.substring(pos), "?");
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

	/**
	 * This is a mess!
	 *
	 * <p>
	 * Just take a look at that awful while loop...
	 *
	 * @param text
	 * @param suffixArray
	 * @param pattern
	 * @param skip
	 * @param first
	 * @return
	 */
	public static int binarySearch(String text, int[] suffixArray, String pattern, int skip, boolean first) {
		// prn("searching for", (first) ? "first" : "last", "position of",
		// pattern, "in", text);

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
				hi = mid - 1;
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
		HashSet<Integer> result = new HashSet<Integer>();

		// check for empty pattern
		if (pattern.length() == 0 || text.length() == 0) {
			return result;
		}

		// find first idx of match
		int firstIdx = binarySearch(text, sa, pattern, skip, true);
		// find last idx of match
		int lastIdx = binarySearch(text, sa, pattern, skip, false);
		// prn("matches are between", firstIdx, "and", lastIdx);

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

	// Suffix Array --> Suffix Tree code
	public static int longestCommonPrefix(String s, int i, int j, int skip) {
		int lcp = Math.max(0, skip);

		while ((i + lcp < s.length()) && (j + lcp < s.length())) {
			if (s.charAt(i + lcp) == s.charAt(j + lcp)) {
				lcp += 1;
			} else {
				break;
			}
		}

		// prn("calculated lcp of", i, s.substring(i), "and", j, s.substring(j),
		// "-->", lcp);
		return lcp;
	}

	public static int[] invertSuffixArray(int[] suffixArray) {
		int[] pos = new int[suffixArray.length];
		for (int i = 0; i < pos.length; ++i) {
			pos[suffixArray[i]] = i;
		}

		return pos;
	}

	public static int[] longestCommonPrefixArray(String str, int[] order) {
		int[] lcpArray = new int[order.length - 1];
		int lcp = 0;
		int[] posInOrder = invertSuffixArray(order);
		int suffix = order[0];

		for (int i = 0; i < order.length - 1; ++i) {
			int orderIndex = posInOrder[suffix];
			// prn("suffix", suffix);
			// prn("examining", orderIndex);
			if (orderIndex == order.length - 1) {
				// prn("that was the last lexicographic suffix in the string");
				lcp = 0;
				suffix = (suffix + 1) % order.length;
				// prn("suffix is now", suffix);
				--i; // don't want to lose a turn
			} else {
				int nextSuffix = order[orderIndex + 1];
				lcp = longestCommonPrefix(str, suffix, nextSuffix, lcp - 1);
				lcpArray[orderIndex] = lcp;
				// prn("set lcpArray[" + orderIndex + "]", "to", lcp);
				suffix = (suffix + 1) % order.length;
			}
		}

		return lcpArray;
	}

	public static class TreeNode implements Iterable<TreeNode> {

		private TreeNode parent;
		private Map<Character, TreeNode> children;
		private int stringDepth;
		private int edgeStart;
		private int edgeEnd;

		public TreeNode(TreeNode parent, int stringDepth, int edgeStart, int edgeEnd) {
			this.parent = parent;
			this.children = new TreeMap<Character, TreeNode>();
			this.stringDepth = stringDepth;
			this.edgeStart = edgeStart;
			this.edgeEnd = edgeEnd;
		}

		/**
		 * @param c
		 *            the character to insert as a child
		 */
		public void addChild(char c) {
			children.put(c, new TreeNode(this, -1, -1, -1));
		}

		/**
		 * @param c
		 *            the character to insert as a child
		 * @param child
		 *            the {@code TreeNode} to add as a child
		 */
		public void addChild(char c, TreeNode child) {
			children.put(c, child);
		}

		/**
		 * @return the stringDepth
		 */
		public int getStringDepth() {
			return stringDepth;
		}

		/**
		 * @param stringDepth
		 *            the stringDepth to set
		 */
		public void setStringDepth(int stringDepth) {
			this.stringDepth = stringDepth;
		}

		/**
		 * @return the edgeStart
		 */
		public int getEdgeStart() {
			return edgeStart;
		}

		/**
		 * @param edgeStart
		 *            the edgeStart to set
		 */
		public void setEdgeStart(int edgeStart) {
			this.edgeStart = edgeStart;
		}

		/**
		 * @return the edgeEnd
		 */
		public int getEdgeEnd() {
			return edgeEnd;
		}

		/**
		 * @param edgeEnd
		 *            the edgeEnd to set
		 */
		public void setEdgeEnd(int edgeEnd) {
			this.edgeEnd = edgeEnd;
		}

		/**
		 * @param c
		 *            the character to look up
		 * @return child node matching c
		 */
		public TreeNode getChild(char c) {
			return children.get(c);
		}

		/**
		 * @param node
		 *            the node to set as a parent
		 */
		public void setParent(TreeNode node) {
			parent = node;
		}

		@Override
		public Iterator<TreeNode> iterator() {
			return children.values().iterator();
		}
	}

	public static TreeNode createLeaf(TreeNode node, String s, int suffix) {
		TreeNode leaf = new TreeNode(node, s.length() - suffix, suffix + node.getStringDepth(), s.length() - 1);
		node.addChild(s.charAt(leaf.getEdgeStart()), leaf);
		return leaf;
	}

	public static TreeNode breakEdge(TreeNode parentNode, String s, int start, int offset) {
		// prn("called breakEdge with start:", start, "and offset:", offset);
		char startChar = s.charAt(start);
		char midChar = s.charAt(start + offset);
		TreeNode upperNode = new TreeNode(parentNode, parentNode.getStringDepth() + offset, start, start + offset - 1);

		TreeNode lowerNode = parentNode.getChild(startChar);

		upperNode.addChild(midChar, lowerNode);
		lowerNode.setParent(upperNode);
		lowerNode.setEdgeStart(lowerNode.getEdgeStart() + offset);
		parentNode.addChild(startChar, upperNode);

		return upperNode;
	}

	/**
	 * NOTE: To iterate over edges in the order required in the assignment,
	 * simply perform a depth-first traversal of the tree, outputting edges as
	 * they are encountered.
	 *
	 * @param s
	 *            the string we are interested in
	 * @param suffixArray
	 *            lexicographically sorted suffixes of s
	 * @param lcpArray
	 *            longest common prefixes for the suffixes
	 * @return a tree
	 */
	public static TreeNode makeTree(String s, int[] suffixArray, int[] lcpArray) {
		TreeNode root = new TreeNode(null, 0, -1, -1);
		int lcpPrev = 0;
		TreeNode currNode = root;

		////////////////////////
		// ArrayList<TreeNode> linear;
		// int start, end, depth;
		////////////////////////

		for (int i = 0; i < s.length(); ++i) {
			int suffix = suffixArray[i];
			// prn("inserting", suffix, ":", s.substring(suffix));

			while (currNode.getStringDepth() > lcpPrev) {
				currNode = currNode.parent;
			}

			///////////////////////
			// depth = currNode.getStringDepth();
			// start = currNode.getEdgeStart();
			// end = currNode.getEdgeEnd();
			// String sub = (start != -1) ? s.substring(start, end + 1) :
			/////////////////////// "ROOT";
			// prn("currNode is:", depth, start, end, sub);
			///////////////////////

			if (currNode.getStringDepth() == lcpPrev) {
				currNode = createLeaf(currNode, s, suffix);
			} else {
				int edgeStart = suffixArray[i - 1] + currNode.getStringDepth();
				int offset = lcpPrev - currNode.getStringDepth();
				////////////////////////
				// prn("depth:", currNode.getStringDepth(), "edgeStart:",
				//////////////////////// edgeStart, "offset:", offset);
				////////////////////////

				TreeNode midNode = breakEdge(currNode, s, edgeStart, offset);

				//////////////////////////
				// linear = new ArrayList<TreeNode>();
				// depthFirstList(linear, s, root);
				// prn("AFTER BREAK");
				// for (TreeNode n : linear) {
				// start = n.getEdgeStart();
				// end = n.getEdgeEnd() + 1;
				// prn(start, end, s.substring(start, end));
				// }
				//////////////////////////

				currNode = createLeaf(midNode, s, suffix);
			}

			/////////////////
			// prn("AFTER INSERTION:");
			// linear = new ArrayList<TreeNode>();
			// depthFirstList(linear, s, root);
			// for (TreeNode n : linear) {
			// start = n.getEdgeStart();
			// end = n.getEdgeEnd() + 1;
			// prn(start, end, s.substring(start, end));
			// }
			// prn("=============================================");
			/////////////////

			if (i < lcpArray.length) {
				lcpPrev = lcpArray[i];
				/////////////////
				// prn("set lcpPrev to", lcpPrev);
				/////////////////
			}
		}

		return root;
	}

	public static ArrayList<TreeNode> depthFirstList(ArrayList<TreeNode> ls, String s, TreeNode node) {
		if (node.parent != null) {
			ls.add(node);
		}

		for (TreeNode child : node) {
			depthFirstList(ls, s, child);
		}

		return ls;
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
		private String source;

		public Alphabetic(String s) {
			source = s;
		}

		public int size() {
			return source.length();
		}

		public int get_at(int i) {
			return source.charAt(i);
		}

		public void set_at(int i, int v) {
			throw new UnsupportedOperationException("Don't mess with the original text, yo.");
		}

		public void pat_down() {
			throw new UnsupportedOperationException("Don't mess with the original text, yo.");
		}

		public void pat_down(int start, int end) {
			throw new UnsupportedOperationException("Don't mess with the original text, yo.");
		}

		public String toString() {
			return source;
		}

		public int[] value() {
			int[] ret = new int[source.length()];
			for (int i = 0; i < source.length(); ++i) {
				ret[i] = (int) source.charAt(i);
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
				counts = java.util.Arrays.copyOf(counts, e * 2);
				// throw new IllegalArgumentException("You said your alphabet
				// was < " + vocab.length + " but now you've given me an " + e +
				// "!");
			}

			// this means it's the first time we're encountering this
			// character
			if (counts[e] == 0) {
				if (v_end >= vocab.length) {
					vocab = java.util.Arrays.copyOf(vocab, v_end * 2);
				}

				vocab[v_end++] = e;
			}

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

	public static HashSet<Integer> naivePatternMatch(String text, String pattern) {
		HashSet<Integer> result = new HashSet<Integer>();

		// check for empty strings
		if (text.length() == 0 || pattern.length() == 0) {
			return result;
		}

		for (int i = 0; i < text.length(); ++i) {
			for (int j = i, k = 0; j < text.length() && k < pattern.length(); ++j, ++k) {
				if (text.charAt(j) != pattern.charAt(k)) {
					break;
				}

				if (k == pattern.length() - 1) {
					result.add(i);
				}
			}
		}
		return result;
	}

	public static HashSet<Integer> naiveMultiPatternMatch(String text, String[] patterns) {
		HashSet<Integer> totalMatches = new HashSet<Integer>();

		for (String p : patterns) {
			totalMatches.addAll(naivePatternMatch(text, p));
		}

		return totalMatches;
	}

	public static String makeRandomString(int len, boolean appendDollar) {
		Random r = new Random();
		StringBuilder b = new StringBuilder(len + 1);

		for (int i = 0; i < len; ++i) {
			char c = (char) (r.nextInt(4) + 65);
			b.append(c);
		}

		if (appendDollar) {
			b.append('$');
		}

		return b.toString();
	}

	public static void stressTest() {
		Random r = new Random();

		while (true) {
			int textLen = r.nextInt(10000);
			String text = makeRandomString(textLen, true);

			int numPatterns = r.nextInt(1000);
			String[] patterns = new String[numPatterns];
			for (int i = 0; i < numPatterns; ++i) {
				int patternLen = r.nextInt(1000);
				String pattern = makeRandomString(patternLen, false);
				patterns[i] = pattern;
			}

			prn("text:", text);
			for (String p : patterns) {
				prn("_patt:", p);
			}

			HashSet<Integer> matches = multiMatchesInText(text, patterns);
			HashSet<Integer> expect = naiveMultiPatternMatch(text, patterns);

			for (int e : expect) {
				if (!matches.contains(e)) {
					throw new IllegalStateException("Broken:\ntext: " + text + "\npatt: " + patterns.toString()
							+ "\nand " + e + " is not in matches");
				}
			}

			for (int m : matches) {
				if (!expect.contains(m)) {
					throw new IllegalStateException("Broken:\ntext:" + text + "\npatt:" + patterns.toString() + "\nand "
							+ m + " is in matches when it shouldn't be");
				} else {
					prn("++++++++++++++++++++++++++++++++++++++++++++++> match:", m);
				}
			}
		}
	}
}
