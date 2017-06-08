package com.ystervark;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class SAISTest {

	@Test
	public void nonRecursiveSAIS() {
		String s = "32210";
		int[] result = SuffixArray.compute(s);
		int[] expect = { 4, 3, 2, 1, 0 };
		assertTrue("computes non-recursive case", Arrays.equals(result, expect));
	}

	@Test
	public void simpleSAIS() {
		String s = "AAA$";
		int[] result = SuffixArray.compute(s);
		int[] expect = { 3, 2, 1, 0 };
		assertTrue("computes easy case", Arrays.equals(result, expect));
	}

	@Test
	public void anotherSimpleSAIS() {
		String s = "GAC$";
		int[] result = SuffixArray.compute(s);
		int[] expect = { 3, 1, 2, 0 };
		assertTrue("computes easy case", Arrays.equals(result, expect));
	}

	@Test
	public void recursiveSAIS() {
		String s = "TGTGTGTGCACCG$";
		int[] result = SuffixArray.compute(s);
		int[] expect = { 13, 9, 8, 10, 11, 12, 7, 5, 3, 1, 6, 4, 2, 0 };
		assertTrue("computes recursive case", Arrays.equals(result, expect));
	}

	@Test
	public void anotherSAIS() {
		String s = "AACGATAGCGGTAGA$";
		int[] result = SuffixArray.compute(s);
		int[] expect = { 15, 14, 0, 1, 12, 6, 4, 2, 8, 13, 3, 7, 9, 10, 11, 5 };
		assertTrue(expect.length == 16);
		assertTrue("computes recursive case", Arrays.equals(result, expect));
	}

	@Test
	public void yetAnotherSAIS() {
		String s = "ACACAA$";
		int[] result = SuffixArray.compute(s);
		int[] expect = { 6, 5, 4, 2, 0, 3, 1 };
		assertTrue(Arrays.equals(result, expect));
	}
}
