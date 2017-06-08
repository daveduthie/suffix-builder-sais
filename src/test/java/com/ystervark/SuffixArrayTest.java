package com.ystervark;

import org.junit.Test;
import org.junit.Ignore;

import com.ystervark.SuffixArray.SuffixType;
import com.ystervark.SuffixArray.Text;

import static org.junit.Assert.*;

public class SuffixArrayTest {

	@Test
	// @Ignore
	public void canInstantiateText() {
		char[] c = "a".toCharArray();
		Text t = new SuffixArray.Alphabetic(c);
		assertTrue(t.get_at(0) == 'a');
	}

	@Test
	// @Ignore
	public void suffixTypesTestEasyPeasy() {
		char[] c = "cgat$".toCharArray();
		Text text = new SuffixArray.Alphabetic(c);
		SuffixType[] types = new SuffixType[text.size()];
		SuffixArray.computeSuffixTypes(text, types);

		assertTrue("0th type is correct", types[0] == SuffixType.ASCENDING);
		assertTrue("1st type is correct", types[1] == SuffixType.DESCENDING);
		assertTrue("2nd type is correct", types[2] == SuffixType.VALLEY);
		assertTrue("3rd type is correct", types[3] == SuffixType.DESCENDING);
		assertTrue("4th type is correct", types[4] == SuffixType.VALLEY);
	}

	@Test
	// @Ignore
	public void suffixTypesTest() {
		char[] c = "TGTGTGTGCACCG$".toCharArray();
		Text text = new SuffixArray.Alphabetic(c);
		SuffixType[] types = new SuffixType[text.size()];
        SuffixArray.computeSuffixTypes(text, types);

		assertTrue(types[0] == SuffixType.DESCENDING);
		assertTrue(types[1] == SuffixType.VALLEY);
		assertTrue(types[2] == SuffixType.DESCENDING);
		assertTrue(types[3] == SuffixType.VALLEY);
		assertTrue(types[4] == SuffixType.DESCENDING);
		assertTrue(types[5] == SuffixType.VALLEY);
		assertTrue(types[6] == SuffixType.DESCENDING);
		assertTrue(types[7] == SuffixType.DESCENDING);
		assertTrue(types[8] == SuffixType.DESCENDING);
		assertTrue(types[9] == SuffixType.VALLEY);
		assertTrue(types[10] == SuffixType.ASCENDING);
		assertTrue(types[11] == SuffixType.ASCENDING);
		assertTrue(types[12] == SuffixType.DESCENDING);
		assertTrue(types[13] == SuffixType.VALLEY);
	}

	@Test
	// @Ignore
	public void textDotGetAndSet() {
		char[] c = "CGAT$".toCharArray();
		int[] i = { 3, 1, 4, 2, 0 };
		Text t1 = new SuffixArray.Alphabetic(c);
		Text t2 = new SuffixArray.Numeric(i, 0, i.length);

		assertTrue(t1.get_at(0) == 67);
		t1.set_at(0, 65);
		assertTrue(t1.get_at(0) == 65);

		assertTrue(t2.get_at(0) == 3);
		t2.set_at(0, 5);
		assertTrue(t2.get_at(0) == 5);
	}

	@Test
	// @Ignore
	public void wStringsEqual() {
		char[] c = "TGTGTGTGCACCG$".toCharArray();
		Text text = new SuffixArray.Alphabetic(c);
		SuffixType[] types = new SuffixType[text.size()];
        SuffixArray.computeSuffixTypes(text, types);

		assertTrue("identical strings are the same", SuffixArray.wStringsEqual(1, 3, text, types));
		assertFalse("non-identical strings are not the same", SuffixArray.wStringsEqual(3, 5, text, types));
		assertFalse("non-identical strings are not the same", SuffixArray.wStringsEqual(5, 9, text, types));
		assertFalse("non-identical strings are not the same", SuffixArray.wStringsEqual(3, 13, text, types));
	}
}
