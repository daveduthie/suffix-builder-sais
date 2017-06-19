package com.ystervark;

import static org.junit.Assert.*;

import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;

public class MatchTest {

	String text;
	int[] sa;
	String pattern;

	@Before
	public void setUp() {
		// ---- 012345678
		text = "ABABDABC$";
		sa = SuffixArray.compute(text);
	}

	@Test
	public void test() {
		pattern = "DA";
		int result = SuffixArray.suffixCompare(text, sa, pattern, 8, 0);
		assertTrue(result == 0);
	}

	@Test
	public void test2() {
		pattern = "GA";
		int result = SuffixArray.suffixCompare(text, sa, pattern, 8, 0);
		assertTrue(result > 0);
	}

	@Test
	public void test3() {
		pattern = "AB";
		HashSet<Integer> result = SuffixArray.matchesInText(text, sa, pattern);
		int[] expect = { 0, 5, 2 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}

	@Test
	public void test4() {
		String[] patterns = { "AB", "A", "C" };
		HashSet<Integer> result = SuffixArray.multiMatchesInText(text, patterns);
		int[] expect = { 0, 2, 5, 7 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}

	@Test
	public void test5() {
      String[] patterns = { "BA", "D", "G" };
		HashSet<Integer> result = SuffixArray.multiMatchesInText(text, patterns);
		int[] expect = { 1, 4 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}
}