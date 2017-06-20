package com.ystervark;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class LCPArrayTest {

	@Test
	public void testLongestCommonPrefix() {
		String str = "AAA$";
		int skip = 0;
		int lcp = SuffixArray.longestCommonPrefix(str, 2, 1, skip);
		assertTrue(lcp == 1);
		// SuffixArray.prn("____________________");
	}

	@Test
	public void testLongestCommonPrefixArray() {
		String str = "ABABDABC$";
		int[] suffixArray = SuffixArray.compute(str);
		int[] lcpArr = SuffixArray.longestCommonPrefixArray(str, suffixArray);
		int[] expect = { 0, 2, 2, 0, 1, 1, 0, 0 };

		for (int i = 0; i < suffixArray.length; ++i) {
			int x = suffixArray[i];
			SuffixArray.prn(i, x, (i < lcpArr.length) ? lcpArr[i] : "-", "->", str.substring(x));
		}

		assertTrue(Arrays.equals(lcpArr, expect));
		// SuffixArray.prn("____________________");
	}

	@Test
	public void testLongestCommonPrefixArray2() {
		String str = "AAA$";
		int[] suffixArray = SuffixArray.compute(str);
		int[] lcpArr = SuffixArray.longestCommonPrefixArray(str, suffixArray);
		int[] expect = { 0, 1, 2 };

		for (int i = 0; i < suffixArray.length; ++i) {
			int x = suffixArray[i];
			SuffixArray.prn(i, x, (i < lcpArr.length) ? lcpArr[i] : "-", "->", str.substring(x));
		}

		assertTrue(Arrays.equals(lcpArr, expect));
		// SuffixArray.prn("____________________");
	}
}
