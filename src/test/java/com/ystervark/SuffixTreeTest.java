package com.ystervark;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import com.ystervark.Tree.TreeNode;

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
	@Ignore
	public void testMakeTree() {
		String s = "AAA$";
		int[] suffArray = SuffixArray.computeSA(s);
		int[] lcpArray = Tree.longestCommonPrefixArray(s, suffArray);
		Tree.TreeNode root = Tree.makeTree(s, suffArray, lcpArray);

		ArrayList<TreeNode> ls = new ArrayList<TreeNode>();
		Tree.depthFirstList(ls, s, root);
	}

	@Test
	public void testMakeTree2() {
		String s = "ATAAATG" + "$";
		int[] suffArray = SuffixArray.computeSA(s);
		int[] lcpArray = Tree.longestCommonPrefixArray(s, suffArray);
		TreeNode root = Tree.makeTree(s, suffArray, lcpArray);

		ArrayList<TreeNode> linear = new ArrayList<TreeNode>();
		Tree.depthFirstList(linear, s, root);

		String[] expect = { "$", "A", "A", "ATG$", "TG$", "T", "AAATG$", "G$", "G$", "T", "AAATG$", "G$" };

		for (int i = 0; i < linear.size(); ++i) {
			int start = linear.get(i).getEdgeStart();
			int end = linear.get(i).getEdgeEnd() + 1;
			assertTrue(s.substring(start, end).equals(expect[i]));
			// SuffixArray.prn(start, end, "-->", s.substring(start, end));
		}
	}

	@Test
	@Ignore
	public void testMakeTree3() {
		String s = "ABABAA" + "$";
		int[] suffArray = SuffixArray.computeSA(s);
		int[] lcpArray = Tree.longestCommonPrefixArray(s, suffArray);
		TreeNode root = Tree.makeTree(s, suffArray, lcpArray);

		ArrayList<TreeNode> ls = new ArrayList<TreeNode>();
		Tree.depthFirstList(ls, s, root);
	}
}
