/**
 * 
 */
package com.ystervark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author daveduthie
 *
 */
public class Tree {
	public static class TreeNode implements Iterable<TreeNode> {

		private TreeNode parent;
		private Map<Character, TreeNode> children;
		private int stringDepth;
		private int edgeStart;
		private int edgeEnd;

		public TreeNode(TreeNode parent, int stringDepth, int edgeStart, int edgeEnd) {
			this.parent = parent;
			this.children = new TreeMap<Character, TreeNode>();
			this.stringDepth = stringDepth;
			this.edgeStart = edgeStart;
			this.edgeEnd = edgeEnd;
		}

		/**
		 * @param c
		 *            the character to insert as a child
		 */
		public void addChild(char c) {
			children.put(c, new TreeNode(this, -1, -1, -1));
		}

		/**
		 * @param c
		 *            the character to insert as a child
		 * @param child
		 *            the {@code TreeNode} to add as a child
		 */
		public void addChild(char c, TreeNode child) {
			children.put(c, child);
		}

		/**
		 * @param c
		 *            the character to look up
		 * @return child node matching c
		 */
		public TreeNode getChild(char c) {
			return children.get(c);
		}

		/**
		 * @return the edgeEnd
		 */
		public int getEdgeEnd() {
			return edgeEnd;
		}

		/**
		 * @return the edgeStart
		 */
		public int getEdgeStart() {
			return edgeStart;
		}

		/**
		 * @return the stringDepth
		 */
		public int getStringDepth() {
			return stringDepth;
		}

		@Override
		public Iterator<TreeNode> iterator() {
			return children.values().iterator();
		}

		/**
		 * @param edgeEnd
		 *            the edgeEnd to set
		 */
		public void setEdgeEnd(int edgeEnd) {
			this.edgeEnd = edgeEnd;
		}

		/**
		 * @param edgeStart
		 *            the edgeStart to set
		 */
		public void setEdgeStart(int edgeStart) {
			this.edgeStart = edgeStart;
		}

		/**
		 * @param node
		 *            the node to set as a parent
		 */
		public void setParent(TreeNode node) {
			parent = node;
		}

		/**
		 * @param stringDepth
		 *            the stringDepth to set
		 */
		public void setStringDepth(int stringDepth) {
			this.stringDepth = stringDepth;
		}
	}

    public static TreeNode breakEdge(TreeNode parentNode, String s, int start, int offset) {
        // prn("called breakEdge with start:", start, "and offset:", offset);
        char startChar = s.charAt(start);
        char midChar = s.charAt(start + offset);
        TreeNode upperNode = new TreeNode(parentNode, parentNode.getStringDepth() + offset, start, start + offset - 1);

        TreeNode lowerNode = parentNode.getChild(startChar);

        upperNode.addChild(midChar, lowerNode);
        lowerNode.setParent(upperNode);
        lowerNode.setEdgeStart(lowerNode.getEdgeStart() + offset);
        parentNode.addChild(startChar, upperNode);

        return upperNode;
    }

    public static TreeNode createLeaf(TreeNode node, String s, int suffix) {
        TreeNode leaf = new TreeNode(node, s.length() - suffix, suffix + node.getStringDepth(), s.length() - 1);
        node.addChild(s.charAt(leaf.getEdgeStart()), leaf);
        return leaf;
    }

    public static ArrayList<TreeNode> depthFirstList(ArrayList<TreeNode> ls, String s, TreeNode node) {
        if (node.parent != null) {
            ls.add(node);
        }

        for (TreeNode child : node) {
            depthFirstList(ls, s, child);
        }

        return ls;
    }

    public static int[] invertSuffixArray(int[] suffixArray) {
        int[] pos = new int[suffixArray.length];
        for (int i = 0; i < pos.length; ++i) {
            pos[suffixArray[i]] = i;
        }

        return pos;
    }

    public static int longestCommonPrefix(String s, int i, int j, int skip) {
        int lcp = Math.max(0, skip);

        while ((i + lcp < s.length()) && (j + lcp < s.length())) {
            if (s.charAt(i + lcp) == s.charAt(j + lcp)) {
                lcp += 1;
            } else {
                break;
            }
        }

        // prn("calculated lcp of", i, s.substring(i), "and", j, s.substring(j),
        // "-->", lcp);
        return lcp;
    }

    public static int[] longestCommonPrefixArray(String str, int[] order) {
        int[] lcpArray = new int[order.length - 1];
        int lcp = 0;
        int[] posInOrder = invertSuffixArray(order);
        int suffix = order[0];

        for (int i = 0; i < order.length - 1; ++i) {
            int orderIndex = posInOrder[suffix];
            // prn("suffix", suffix);
            // prn("examining", orderIndex);
            if (orderIndex == order.length - 1) {
                // prn("that was the last lexicographic suffix in the string");
                lcp = 0;
                suffix = (suffix + 1) % order.length;
                // prn("suffix is now", suffix);
                --i; // don't want to lose a turn
            } else {
                int nextSuffix = order[orderIndex + 1];
                lcp = longestCommonPrefix(str, suffix, nextSuffix, lcp - 1);
                lcpArray[orderIndex] = lcp;
                // prn("set lcpArray[" + orderIndex + "]", "to", lcp);
                suffix = (suffix + 1) % order.length;
            }
        }

        return lcpArray;
    }

	/**
	 * NOTE: To iterate over edges in the order required in the assignment,
	 * simply perform a depth-first traversal of the tree, outputting edges as
	 * they are encountered.
	 *
	 * @param s
	 *            the string we are interested in
	 * @param suffixArray
	 *            lexicographically sorted suffixes of s
	 * @param lcpArray
	 *            longest common prefixes for the suffixes
	 * @return a tree
	 */
	public static TreeNode makeTree(String s, int[] suffixArray, int[] lcpArray) {
		TreeNode root = new TreeNode(null, 0, -1, -1);
		int lcpPrev = 0;
		TreeNode currNode = root;

		////////////////////////
		// ArrayList<TreeNode> linear;
		// int start, end, depth;
		////////////////////////

		for (int i = 0; i < s.length(); ++i) {
			int suffix = suffixArray[i];
			// prn("inserting", suffix, ":", s.substring(suffix));

			while (currNode.getStringDepth() > lcpPrev) {
				currNode = currNode.parent;
			}

			///////////////////////
			// depth = currNode.getStringDepth();
			// start = currNode.getEdgeStart();
			// end = currNode.getEdgeEnd();
			// String sub = (start != -1) ? s.substring(start, end + 1) :
			/////////////////////// "ROOT";
			// prn("currNode is:", depth, start, end, sub);
			///////////////////////

			if (currNode.getStringDepth() == lcpPrev) {
				currNode = createLeaf(currNode, s, suffix);
			} else {
				int edgeStart = suffixArray[i - 1] + currNode.getStringDepth();
				int offset = lcpPrev - currNode.getStringDepth();
				////////////////////////
				// prn("depth:", currNode.getStringDepth(), "edgeStart:",
				//////////////////////// edgeStart, "offset:", offset);
				////////////////////////

				TreeNode midNode = breakEdge(currNode, s, edgeStart, offset);

				//////////////////////////
				// linear = new ArrayList<TreeNode>();
				// depthFirstList(linear, s, root);
				// prn("AFTER BREAK");
				// for (TreeNode n : linear) {
				// start = n.getEdgeStart();
				// end = n.getEdgeEnd() + 1;
				// prn(start, end, s.substring(start, end));
				// }
				//////////////////////////

				currNode = createLeaf(midNode, s, suffix);
			}

			/////////////////
			// prn("AFTER INSERTION:");
			// linear = new ArrayList<TreeNode>();
			// depthFirstList(linear, s, root);
			// for (TreeNode n : linear) {
			// start = n.getEdgeStart();
			// end = n.getEdgeEnd() + 1;
			// prn(start, end, s.substring(start, end));
			// }
			// prn("=============================================");
			/////////////////

			if (i < lcpArray.length) {
				lcpPrev = lcpArray[i];
				/////////////////
				// prn("set lcpPrev to", lcpPrev);
				/////////////////
			}
		}

		return root;
	}
}
