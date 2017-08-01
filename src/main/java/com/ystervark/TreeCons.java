package com.ystervark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TreeCons {
  class FastScanner {
    StringTokenizer tok = new StringTokenizer("");
    BufferedReader in;

    FastScanner() {
      in = new BufferedReader(new InputStreamReader(System.in));
    }

    String next() throws IOException {
      while (!tok.hasMoreElements())
        tok = new StringTokenizer(in.readLine());
      return tok.nextToken();
    }

    int nextInt() throws IOException {
      return Integer.parseInt(next());
    }
  }

  // Data structure to store edges of a suffix tree.
  public static class Edge {
    // The ending node of this edge.
    int dest;
    // Starting position of the substring of the text
    // corresponding to the label of this edge.
    int start;
    // Position right after the end of the substring of the text
    // corresponding to the label of this edge.
    int end;

    Edge(int dest, int start, int end) {
      this.dest = dest;
      this.start = start;
      this.end = end;
    }
  }

  static class FastTree {
    Map<Integer, List<Edge>> map;
    int current_index;
    int[] predecessor;
    int[] depth;
    final String text;

    FastTree(int[] suffixArray, int[] lcpArray, String text) {
      // Think which of these are necessary
      this.map = new HashMap<Integer, List<Edge>>();
      this.current_index = 0;
      this.predecessor = new int[2 * text.length()];
      this.depth = new int[2 * text.length()];
      this.text = text;

      // add a root node
      map.put(0, new ArrayList<Edge>(5));

      // add the rest
      buildTree(suffixArray, lcpArray, text);
    }

    void makeNode(int parent, int child, int label_len) {
      // System.out.println("called makeNode " + parent + " " + child + " label_len: " + label_len);

      int s = map.size();
      if (child == s) {
        map.put(s, new ArrayList<Edge>(0));
      }
      predecessor[child] = parent;
      depth[child] = depth[parent] + label_len;
    }

    /**
     * @param parent
     *          the parent node under which to insert the new Edge.
     * @param child
     *          the index of the child which the new {@code edge.dest} field will
     *          point to.
     * @param from
     *          the beginning of the edge label.
     * @param to
     *          the end of the edge label.
     * @return the new current index (i.e. the leaf we're resting in)
     */
    int makeEdge(int parent, int child, int from, int to) {
      // System.out.println(
      //     "called makeEdge " + parent + " " + child + " " + from + "->" + to + " " + this.text.substring(from, to));

      map.get(parent).add(new Edge(child, from, to));
      makeNode(parent, child, to - from);
      return child;
    }

    /**
     * @param parent
     *          The element which holds the {@code edge} to be cut.
     * @param offset
     *          Where to cut the edge.
     * @return The location of the latest edge inserted.
     */
    int splitEdge(int parent, int offset) {
      // System.out.println("called splitEdge " + parent + " " + offset);

      // NOTE: we only split the most recently inserted child
      // gather info
      List<Edge> l = map.get(parent);
      Edge e = l.get(l.size() - 1);
      int terminus = e.end;
      int interchange = e.start + offset;
      int old_child = e.dest;
      int s = map.size();

      // modify parent
      e.end = interchange;
      e.dest = s;

      // create new edge in between parent and child
      makeNode(parent, map.size(), offset);
      int child_idx = makeEdge(s, old_child, interchange, terminus);
      return predecessor[child_idx];// that's where we want the next edge to be inserted
    }

    /**
     * @param from
     *          The edge label start.
     * @param to
     *          The edge label end.
     * @param lcp
     *          The longest common prefix between our current insertion and the
     *          previous one.
     * @return The index of the leaf we're sitting in after insertion.
     */
    void insertSuffix(int from, int to, int lcp) {
      int idx = this.current_index;
      // System.out
      //     .println("called insertSuffix " + from + " -> " + to + " " + this.text.substring(from, to) + " lcp: " + lcp);

      // get idx where we want it
      while (depth[idx] > lcp)
        idx = predecessor[idx];

      if (depth[idx] == lcp) {
        this.current_index = makeEdge(idx, map.size(), from + lcp, to);
      } else {
        int offset = lcp - depth[idx];
        int new_idx = splitEdge(idx, offset);
        // System.out.println("AFTER SPLIT::\n" + this.toString() + "|||||||||||||||||||||||||||\n\n\n");
        this.current_index = makeEdge(new_idx, map.size(), from + lcp, to);
      }
    }

    // Build suffix tree of the string text given its suffix array suffix_array
    // and LCP array lcp_array. Return the tree as a mapping from a node ID
    // to the list of all outgoing edges of the corresponding node. The edges in the
    // list must be sorted in the ascending order by the first character of the edge
    // label.
    // Root must have node ID = 0, and all other node IDs must be different
    // nonnegative integers.
    //
    // For example, if text = "ACACAA$", an edge with label "$" from root to a node
    // with ID 1
    // must be represented by new Edge(1, 6, 7). This edge must be present in the
    // list tree.get(0)
    // (corresponding to the root node), and it should be the first edge in the list
    // (because it has the smallest first character of all edges outgoing from the
    // root).
    void buildTree(int[] sa, int[] lcp, String text) {
      for (int i = 0, end = text.length(); i < sa.length; ++i) {
        insertSuffix(sa[i], end, lcp[i]);
        // System.out.println(this.toString());
      }
    }

    List<Edge> getEdges() {
      // Output the edges of the suffix tree in the required order.
      // Note that we use here the contract that the root of the tree
      // will have node ID = 0 and that each vector of outgoing edges
      // will be sorted by the first character of the corresponding edge label.
      //
      // The following code avoids recursion to avoid stack overflow issues.
      // It uses two stacks to convert recursive function to a while loop.
      // This code is an equivalent of
      //
      // OutputEdges(tree, 0);
      //
      // for the following _recursive_ function OutputEdges:
      //
      // public void OutputEdges(Map<Integer, List<Edge>> tree, int nodeId) {
      //   List<Edge> edges = tree.get(nodeId);
      //   for (Edge edge : edges) {
      //     System.out.println(edge.start + " " + edge.end);
      //     OutputEdges(tree, edge.node);
      //   }
      // }
      //
      ArrayList<Edge> result = new ArrayList<>();
      int[] nodeStack = new int[map.size()];
      int[] edgeIndexStack = new int[map.size()];
      nodeStack[0] = 0;
      edgeIndexStack[0] = 0;
      int stackSize = 1;
      while (stackSize > 0) {
        int node = nodeStack[stackSize - 1];
        int edgeIndex = edgeIndexStack[stackSize - 1];
        stackSize -= 1;
        if (map.get(node).isEmpty()) {
          continue;
        }
        if (edgeIndex + 1 < map.get(node).size()) {
          nodeStack[stackSize] = node;
          edgeIndexStack[stackSize] = edgeIndex + 1;
          stackSize += 1;
        }
        result.add(map.get(node).get(edgeIndex));
        nodeStack[stackSize] = map.get(node).get(edgeIndex).dest;
        edgeIndexStack[stackSize] = 0;
        stackSize += 1;
      }

      return result;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < map.size(); ++i) {
        sb.append(i + ":\n");
        List<Edge> l = map.get(i);
        for (Edge e : l) {
          sb.append(
              "|> " + e.dest + ": " + e.start + "-" + e.end + " --> " + this.text.substring(e.start, e.end) + "\n");
        }
        String fringe = (i == this.current_index) ? "^^^^^^^^^^^\n" : "";
        sb.append(fringe);
      }
      sb.append("currIdx     =" + current_index + "\n");
      sb.append("predecessor =" + Arrays.toString(predecessor) + "\n");
      sb.append("depth       =" + Arrays.toString(depth) + "\n");
      sb.append("text        =" + text + "\n=========\n");
      return sb.toString();
    }
  }

  static public void main(String[] args) throws IOException {
    new TreeCons().run();
  }

  public void print(List<Edge> result, String text) {
    for (Edge a : result) {
      System.out.println(a.start + " " + a.end + " -> " + text.substring(a.start, a.end));
    }
  }

  public void print(List<Edge> result) {
    for (Edge a : result) {
      System.out.println(a.start + " " + a.end);
    }
  }

  public void run() throws IOException {
    FastScanner scanner = new FastScanner();
    String text = scanner.next();
    int[] suffixArray = new int[text.length()];
    for (int i = 0; i < suffixArray.length; ++i) {
      suffixArray[i] = scanner.nextInt();
    }
    int[] lcpArray = new int[text.length()];
    lcpArray[0] = 0;
    for (int i = 1; i < text.length(); ++i) {
      lcpArray[i] = scanner.nextInt();
    }
    System.out.println(text);
    // Build the suffix tree and get a mapping from
    // suffix tree node ID to the list of outgoing Edges.
    FastTree t = new FastTree(suffixArray, lcpArray, text);
    List<Edge> result = t.getEdges();
    print(result);
  }
}
