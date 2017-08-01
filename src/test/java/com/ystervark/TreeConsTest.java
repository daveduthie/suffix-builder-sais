package com.ystervark;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

import com.ystervark.TreeCons.Edge;
import com.ystervark.TreeCons.FastTree;

public class TreeConsTest {

  @Test
  @Ignore
  public void testMakeEdge1() {
    //
  }

  @Test
  @Ignore
  public void testBreakEdge1() {
    //
  }

  @Test
  // @Ignore
  public void testCons1() {
    // System.out.println("=====================\n" + "| Started testCons1 |\n" + "=====================");
    String s = "A$";
    int[] sa = { 1, 0 };
    int[] lcp = { 0, 0 };

    FastTree t = new FastTree(sa, lcp, s);

    List<Edge> edges = t.getEdges();
    assertTrue(edges.size() == 2);

    int[][] expect = { { 1, 2 }, { 0, 2 } };

    for (int i = 0; i < expect.length; ++i) {
      // System.out.println(edges.get(i).start + " " + edges.get(i).end);
      assertTrue(expect[i][0] == edges.get(i).start);
      assertTrue(expect[i][1] == edges.get(i).end);
    }
  }

  @Test
  // @Ignore
  public void testCons2() {
    // System.out.println("=====================\n" + "| Started testCons2 |\n" + "=====================");
    String s = "AAA$";
    int[] sa = { 3, 2, 1, 0 };
    int[] lcp = { 0, 0, 1, 2 };

    FastTree t = new FastTree(sa, lcp, s);

    List<Edge> edges = t.getEdges();
    assertTrue(edges.size() == 6);

    int[][] expect = { { 3, 4 }, { 0, 1 }, { 3, 4 }, { 1, 2 }, { 3, 4 }, { 2, 4 } };

    for (int i = 0; i < expect.length; ++i) {
      String subE = s.substring(expect[i][0], expect[i][1]);
      String subA = s.substring(edges.get(i).start, edges.get(i).end);
      // System.out.println(subA + " : " + subE);
      assertTrue(subE.equals(subA));
    }
  }

  @Test
  // @Ignore
  public void testNaiveVsFast1() {
    // System.out
    //     .println("============================\n" + "| Started testNaiveVsFast1 |\n" + "============================");
    String s = "GTAGT$";

    List<int[]> naive = NaiveTree.computeSuffixTreeEdges(s);

    int[] suffixArray = { 5, 2, 3, 0, 4, 1 };
    int[] lcpArray = { 0, 0, 0, 2, 0, 1 };
    FastTree t = new FastTree(suffixArray, lcpArray, s);
    List<Edge> fast = t.getEdges();

    for (int i = 0; i < fast.size(); ++i) {
      String subN = s.substring(naive.get(i)[0], naive.get(i)[1]);
      String subF = s.substring(fast.get(i).start, fast.get(i).end);
      assertTrue(subF.equals(subN));
    }
  }

  @Test
  // @Ignore
  public void testNaiveVsFast2() {
    // System.out
    //     .println("============================\n" + "| Started testNaiveVsFast2 |\n" + "============================");
    String s = "ATAAATG$";

    List<int[]> naive = NaiveTree.computeSuffixTreeEdges(s);

    int[] suffixArray = { 7, 2, 3, 0, 4, 6, 1, 5 };
    int[] lcpArray = { 0, 0, 2, 1, 2, 0, 0, 1 };
    FastTree t = new FastTree(suffixArray, lcpArray, s);
    // System.out.println("\n\n" + s + "::\nSuffixes:");
    // for (int i : suffixArray) {
    //   System.out.println(s.substring(i));
    // }
    // System.out.println();
    // System.out.println(t.toString());
    List<Edge> fast = t.getEdges();

    for (int i = 0; i < fast.size(); ++i) {
      String subN = s.substring(naive.get(i)[0], naive.get(i)[1]);
      String subF = s.substring(fast.get(i).start, fast.get(i).end);
      assertTrue(subF.equals(subN));
    }
  }

  @Test
  public void testOOOCons() {
    // System.out.println("=======================\n" + "| Started testOOOCons |\n" + "=======================");
    String s = "TTUZDDTVOQ$";

    // naive edges
    List<int[]> naive = NaiveTree.computeSuffixTreeEdges(s);

    // fast edges
    int[] suffixArray = com.ystervark.SuffixArray.compute(s);
    int[] lcpArray = com.ystervark.SuffixArray.longestCommonPrefixArray(s, suffixArray);
    // for (int i = 0; i < suffixArray.length; ++i) {
    //   System.out.println(lcpArray[i] + " | " + s.substring(suffixArray[i]));
    // }
    FastTree ft = new FastTree(suffixArray, lcpArray, s);
    List<Edge> fast = ft.getEdges();
    // System.out.println(ft.toString());

    for (int i = 0; i < fast.size(); ++i) {
      String subN = s.substring(naive.get(i)[0], naive.get(i)[1]);
      String subF = s.substring(fast.get(i).start, fast.get(i).end);
      // System.out.println(subN + " : " + subF);
      assertTrue(subF.equals(subN));
    }
  }

  String randomString() {
    java.util.Random r = new java.util.Random();
    int len = r.nextInt(500);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; ++i) {
      int a = r.nextInt(26) + 65;
      sb.append((char) a);
    }
    sb.append("$");
    return sb.toString();
  }

  @Test
  // @Ignore
  public void stressTestConstruction() {
    for (int i = 0; i < 1000; ++i) {
      String s = randomString();
      // System.out.println("s: " + s);

      // fast method
      int[] suffixArray = com.ystervark.SuffixArray.compute(s);
      int[] lcpArray = com.ystervark.SuffixArray.longestCommonPrefixArray(s, suffixArray);
      FastTree ft = new FastTree(suffixArray, lcpArray, s);
      List<Edge> fast = ft.getEdges();
      // System.out.println(ft.toString());

      // slow method
      List<int[]> naive = NaiveTree.computeSuffixTreeEdges(s);

      for (int x = 0; x < fast.size(); ++x) {
        String subN = s.substring(naive.get(x)[0], naive.get(x)[1]);
        String subF = s.substring(fast.get(x).start, fast.get(x).end);
        // System.out.println(subN + " : " + subF);
        assertTrue(subF.equals(subN));
      }
    }
  }

}
