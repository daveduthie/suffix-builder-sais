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
      if ( (types[i] == SuffixType.VALLEY || types[j] == SuffixType.VALLEY) && i != w1 )
          return true;
		}
	}
}
