package com.ystervark;

import org.junit.Test;
import org.junit.Ignore;

import com.ystervark.SuffixArray.SuffixType;
import com.ystervark.SuffixArray.Text;

import static org.junit.Assert.*;

public class SuffixArrayTest {

  @Test
  public void canInstantiateText() {
    char[] c = "a".toCharArray();
    Text t = new SuffixArray.Alphabetic(c);
    assertTrue(t.get_at(0) == 'a');
  }

  @Test
  // @Ignore
  public void SuffixTypesTestEasyPeasy() {
    char[] c = "cgat$".toCharArray();
    Text t = new SuffixArray.Alphabetic(c);
    SuffixType[] types = SuffixArray.computeSuffixTypes(t);

    assertTrue("0th type is correct", types[0] == SuffixType.ASCENDING);
    assertTrue("1st type is correct", types[1] == SuffixType.DESCENDING);
    assertTrue("2nd type is correct", types[2] == SuffixType.VALLEY);
    assertTrue("3rd type is correct", types[3] == SuffixType.DESCENDING);
    assertTrue("4th type is correct", types[4] == SuffixType.VALLEY);
  }

  @Test
  public void SuffixTypesTest() {
    char[] c = "TGTGTGTGCACCG$".toCharArray();
    Text t = new SuffixArray.Alphabetic(c);
    SuffixType[] types = SuffixArray.computeSuffixTypes(t);

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
}
