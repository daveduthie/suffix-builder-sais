package com.ystervark;

import java.util.*;
import java.io.*;

public class NaiveTree {
  public String toString() {
    return "This is a naive class";
  }

	class FastScanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		FastScanner() {
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

	public void printTree(TreeNode t, int offset, char c) {

		if (offset > 10) {
			System.out.println("quitting...");
			System.exit(100);
		}

		for (int i = 0; i < offset; ++i) {
			System.out.print(".");
		}
		System.out.print(c + " " + t.start);
		if (t.start != t.end) {
			System.out.print("..." + t.end);
		}
		System.out.println();

		for (Map.Entry<Character, TreeNode> e : t.children.entrySet()) {
			printTree(e.getValue(), offset + 1, e.getKey());
		}
	}

	public static class TreeNode {
		// fields
		int start;
		int end;
		Map<Character, TreeNode> children;

		// constructors
		TreeNode(int start) {
			this.start = start;
			this.end = start;
			this.children = new TreeMap<Character, TreeNode>();
		}

		TreeNode(int start, int end) {
			this.start = start;
			this.end = end;
			this.children = new TreeMap<Character, TreeNode>();
		}

		// methods
		TreeNode match(Character c) {
			return children.get(c);
		}

		void splitAfter(String s, int i) {
			/*
			 * if ((start == end) || !(start < s.length())) {
			 * System.out.println("bad split"); System.exit(200); }
			 */

			TreeNode tmp = new TreeNode(i + 1, this.end);

			this.end = i;
			if (tmp.end < tmp.start) {
				tmp.end = tmp.start;
			}

			tmp.children.putAll(this.children);
			this.children.clear();

			this.children.put(s.charAt(i + 1), tmp);
		}

		void splice(String s, int from, int to) {
			char c = s.charAt(from);
			TreeNode t = children.get(c);

			// choices:
			if (t == null) {
				// no key c in children where c is s[from]
				// -> insert a new child
				children.put(c, new TreeNode(from, to));
			} else {
				int matchLength = matchLength(s, t.start, t.end, from, to);
				int lastMatch = t.start + matchLength - 1;

				if (lastMatch == t.end) {
					// pattern (s[from...to]) matches whole of s[start...end]
					// -> traverse a child without modifying
					t.splice(s, from + matchLength, to);
				} else {
					// pattern does not match whole of s[start...end]
					// -> break child node
					t.splitAfter(s, lastMatch);
					// -> insert new node
					t.splice(s, from + matchLength, to);
				}
			}
		}

		void edges(List<int[]> l) {
			
			if (this.start != -1) {
				int[] s_e = {this.start, this.end + 1};
				l.add(s_e);
				// l.add(s.substring(this.start, this.end + 1));
			}
			for (TreeNode child : children.values()) {
				child.edges(l);
			}
		}
		
		List<int[]> allEdges() {
			List<int[]> l = new ArrayList<>();
			edges(l);
			return l;
		}
	}

	// Build a suffix tree of the string text and return a list
	// with all of the labels of its edges (the corresponding
	// substrings of the text) in any order.
	public static List<int[]> computeSuffixTreeEdges(String text) {
		TreeNode root = new TreeNode(-1);
		for (int i = 0, j = text.length() - 1; i < text.length(); ++i) {
			root.splice(text, i, j);
			// printTree(root, 0, '*');
			// System.out.println("++++++++++++++");
		}

		List<int[]> result = new ArrayList<int[]>();
		root.edges(result);
		return result;
	}

	public static int matchLength(String s, int start, int end, int from, int to) {
		int matchLength = 0;

		/*
		 * if (start < 0) { System.out.println("bad idea"); System.exit(100); }
		 */

		while (!(start > end) && from < s.length() && s.charAt(start) == s.charAt(from)) {
			matchLength++;
			start++;
			from++;

		}

		return matchLength;
	}

	static public void main(String[] args) throws IOException {
		new NaiveTree().run();
	}

	public void print(List<int[]> x, String text) {
		for (int[] a : x) {
			System.out.println(text.substring(a[0], a[1]));
		}
	}

	public void run() throws IOException {
		FastScanner scanner = new FastScanner();
		String text = scanner.next();
		List<int[]> edges = computeSuffixTreeEdges(text);
		print(edges, text);
	}
}

