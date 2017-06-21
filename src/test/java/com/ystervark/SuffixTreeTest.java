package com.ystervark;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import com.ystervark.SuffixArray.TreeNode;

public class SuffixTreeTest {

	@Test
	@Ignore
	public void testCreateLeaf() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testBreakEdge() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeTree() {
		String s = "AAA$";
		int[] suffArray = SuffixArray.compute(s);
		int[] lcpArray = SuffixArray.longestCommonPrefixArray(s, suffArray);
		SuffixArray.TreeNode root = SuffixArray.makeTree(s, suffArray, lcpArray);

		ArrayList<SuffixArray.TreeNode> ls = new ArrayList<SuffixArray.TreeNode>();
		SuffixArray.depthFirstList(ls, s, root);

		for (TreeNode e : ls) {
			int start = e.getEdgeStart();
			int end = e.getEdgeEnd() + 1;
			SuffixArray.prn(start, end, s.substring(start, end));
		}
	}

	@Test
	public void testMakeTree2() {
		String s = "TCCTCTATGAGATCCTATTCTATGAAACCTTCAGACCAAAATTCTCCGGC" + "$";
		int[] suffArray = SuffixArray.compute(s);
		int[] lcpArray = SuffixArray.longestCommonPrefixArray(s, suffArray);
		SuffixArray.TreeNode root = SuffixArray.makeTree(s, suffArray, lcpArray);

		ArrayList<SuffixArray.TreeNode> ls = new ArrayList<SuffixArray.TreeNode>();
		SuffixArray.depthFirstList(ls, s, root);

		for (TreeNode e : ls) {
			int start = e.getEdgeStart();
			int end = e.getEdgeEnd() + 1;
			SuffixArray.prn(start, end, s.substring(start, end));
		}
	}
}
