package com.ystervark;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ystervark.SuffixArray.SuffixType;
import com.ystervark.SuffixArray.Text;

public class SuffixArrayTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  @Ignore
  public void canInstantiateText() {
    String s = "a";
    Text t = new SuffixArray.Alphabetic(s);
    assertTrue(t.get_at(0) == 'a');
  }

  @Test
  // @Ignore
  public void suffixTypesTest() {
    String s = "TGTGTGTGCACCG$";
    Text text = new SuffixArray.Alphabetic(s);
    SuffixType[] types = SuffixArray.computeSuffixTypes(text);

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
  public void suffixTypesRedux() {
    String s = "AACGATAGCGGTAGA$";
    Text text = new SuffixArray.Alphabetic(s);
    SuffixType[] types = SuffixArray.computeSuffixTypes(text);
    SuffixType[] expect = {
        SuffixType.ASCENDING,
        SuffixType.ASCENDING,
        SuffixType.ASCENDING,
        SuffixType.DESCENDING,
        SuffixType.VALLEY,
        SuffixType.DESCENDING,
        SuffixType.VALLEY,
        SuffixType.DESCENDING,
        SuffixType.VALLEY,
        SuffixType.ASCENDING,
        SuffixType.ASCENDING,
        SuffixType.DESCENDING,
        SuffixType.VALLEY,
        SuffixType.DESCENDING,
        SuffixType.DESCENDING,
        SuffixType.VALLEY };

    assertTrue("compute suffix types", Arrays.equals(types, expect));
  }

  @Test
  // @Ignore
  public void textDotGetAndSet() {
    String s = "CGAT$";
    int[] i = { 3, 1, 4, 2, 0 };
    Text t1 = new SuffixArray.Alphabetic(s);
    Text t2 = new SuffixArray.Numeric(i, 0, i.length);

    assertTrue(t2.get_at(0) == 3);
    t2.set_at(0, 5);
    assertTrue(t2.get_at(0) == 5);

    thrown.expect(UnsupportedOperationException.class);
    t1.set_at(0, 65);

  }

  @Test
  // @Ignore
  public void wStringsEqual() {
    String s = "TGTGTGTGCACCG$";
    Text text = new SuffixArray.Alphabetic(s);
    SuffixType[] types = SuffixArray.computeSuffixTypes(text);

    assertTrue("identical strings are the same", SuffixArray.wStringsEqual(1, 3, text, types));
    assertFalse(
        "non-identical strings are not the same",
        SuffixArray.wStringsEqual(3, 5, text, types));
    assertFalse(
        "non-identical strings are not the same",
        SuffixArray.wStringsEqual(5, 9, text, types));
    assertFalse(
        "non-identical strings are not the same",
        SuffixArray.wStringsEqual(3, 13, text, types));
  }
}
