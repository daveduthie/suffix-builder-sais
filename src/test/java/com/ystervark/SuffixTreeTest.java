package com.ystervark;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

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

    SuffixArray.depthFirstPrint(s, root);
	}

}
