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
		sa = SuffixArray.computeSA(text);
	}

	@Test
	public void test() {
		pattern = "DA";
		int result = Matching.suffixCompare(text, pattern, sa[8], 0);
		assertTrue(result == 0);
	}

	@Test
	public void test2() {
		pattern = "GA";
		int result = Matching.suffixCompare(text, pattern, sa[8], 0);
		assertTrue(result > 0);
	}

	@Test
	public void test3() {
		pattern = "AB";
		HashSet<Integer> result = Matching.matchesInText(text, sa, pattern);
		int[] expect = { 0, 5, 2 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}

	@Test
	public void test4() {
		String[] patterns = { "AB", "A", "C" };
		HashSet<Integer> result = Matching.multiMatchesInText(text, patterns);
		int[] expect = { 0, 2, 5, 7 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}

	@Test
	public void test5() {
		String[] patterns = { "BA", "D", "G" };
		HashSet<Integer> result = Matching.multiMatchesInText(text, patterns);
		int[] expect = { 1, 4 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}

	@Test
	public void test6() {
		// ----------- 01234567891012141618202224262830323436384042444648 --- 49
		String text = "TCCTCTATGAGATCCTATTCTATGAAACCTTCAGACCAAAATTCTCCGGC" + "$";
		String[] patterns = { "CCT", "CAC", "GAG", "CAG", "ATC" };
		HashSet<Integer> result = Matching.multiMatchesInText(text, patterns);
		int[] expect = { 1, 13, 27, 31, 8, 11 };
		for (int e : expect) {
			assertTrue("contains " + e, result.contains(e));
		}
	}
}
