/**
 * 
 */
package com.ystervark;

import java.util.HashSet;

/**
 * @author daveduthie
 *
 */
public class Matching {
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
			int cmp = suffixCompare(text, pattern, suffixArray[mid], skip);
			// prn("cmp:", cmp);
			if (cmp == 0) {
				if (first) {
					if (mid == 0 || suffixCompare(text, pattern, suffixArray[mid - 1], skip) > 0) {
						// prn("gotcha:", mid);
						return mid;
					} else {
						hi = mid;
					}
				} else {
					if (mid == suffixArray.length - 1 || suffixCompare(text, pattern, suffixArray[mid + 1], skip) < 0) {
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
		int[] sa = SuffixArray.computeSA(text);
		HashSet<Integer> locations = new HashSet<Integer>();
		for (String p : patterns) {
			// prn("examining", p);
			HashSet<Integer> matches = matchesInText(text, sa, p);
			locations.addAll(matches);
		}

		return locations;
	}

	public static HashSet<Integer> naiveMultiPatternMatch(String text, String[] patterns) {
		HashSet<Integer> totalMatches = new HashSet<Integer>();

		for (String p : patterns) {
			totalMatches.addAll(naivePatternMatch(text, p));
		}

		return totalMatches;
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

	/**
	 * @param text
	 *            the string for which we want to compare suffixes the string
	 *            for which to build the suffix array
	 * @param suffixArray
	 *            the sorted suffixes of {@code text} (actually just indices
	 *            into {@code text})
	 * @param pattern
	 *            the string to search for in text
	 * @param pos
	 *            the index into the text where we hope to find a match
	 * @return int
	 *         <p>
	 *         -1: pattern is smaller than text
	 *         <p>
	 *         0: pattern matches text
	 *         <p>
	 *         1: pattern is bigger than text
	 */
	public static int suffixCompare(String text, String pattern, int pos, int skip) {
		// prn("called suffixCompare at", pos, "-->", pattern, "==",
		// text.substring(pos), "?");
		/* start skip after beginning of pattern */
		int i = skip;
		/*
		 * start skip after pos'th position in text
		 */
		int j = pos + skip;
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

	public static void prnBool(boolean[] bs) {
		for (int i = 0; i < bs.length; ++i) {
			if (bs[i]) {
				System.out.print(i + " ");
			}
		}
		System.out.println();
	}
}
