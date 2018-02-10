package io.github.thomasvstevens.disperser;

import java.util.HashMap;
import java.util.HashSet;

/** Container (m x n) for samples with energies. */
public class Layout {

  private final int m;
  private final int n;
  // row-major order 1D arrays
  private int[] ids;
  private int[] energies;
  // special element for each 2D boundary
  private static final int N_EDGES = 4;
  private static final String[] EDGES = {"top", "bottom", "left", "right"};
  private HashMap<String, Integer> edgeIds;

  public Layout(int m, int n) {
    this.m = m;
    this.n = n;
    ids = new int[m * n];
    energies = new int[m * n];
    int k = 0;
    while (k < m * n) {
      ids[k] = k++;
    }
    edgeIds = new HashMap<String, Integer>(N_EDGES);
    for (String edge : EDGES) {
      edgeIds.put(edge, k++);
    }
  }

  public int getId(int i, int j) {
    checkBounds(i, j);
    return ids[i * n + j];
  }

  public int getEnergy(int i, int j) {
    checkBounds(i, j);
    return energies[i * n + j];
  }

  /** Return 4-neighbors of element (i,j) */
  public HashSet<Integer> adj(int i, int j) {
    checkBounds(i, j);
    HashSet<Integer> adjSet = new HashSet<Integer>(4);
    // top
    if (i == 0) {
      adjSet.add(edgeIds.get("top"));
    } else {
      adjSet.add(ids[(i - 1) * n + j]);
    }
    // bottom
    if (i == m - 1) {
      adjSet.add(edgeIds.get("bottom"));
    } else {
      adjSet.add(ids[(i + 1) * n + j]);
    }
    // left
    if (j % n == 0) {
      adjSet.add(edgeIds.get("left"));
    } else {
      adjSet.add(ids[i * n + j - 1]);
    }
    // right
    if (j % n == n - 1) {
      adjSet.add(edgeIds.get("right"));
    } else {
      adjSet.add(ids[i * n + j + 1]);
    }
    return adjSet;
  }

  private void checkBounds(int i, int j) {
    if (i * n + j > m * n - 1) {
      throw new IllegalArgumentException(pairString(i, j) + " out of bounds.");
    }
  }

  public static String pairString(int i, int j) {
    return "(" + i + "," + j + ")";
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < m * n; k++) {
      sb.append(ids[k]);
      if (k % n == n - 1) {
        sb.append("\n");
      } else {
        sb.append("\t");
      }
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    Layout lay = new Layout(8, 12);
    System.out.println(lay);
    System.out.println("Adj" + pairString(0, 0) + "=" + lay.adj(0, 0));
    System.out.println("Adj" + pairString(7, 0) + "=" + lay.adj(7, 0));
    System.out.println("Adj" + pairString(0, 11) + "=" + lay.adj(0, 11));
    System.out.println("Adj" + pairString(7, 11) + "=" + lay.adj(7, 11));
    System.out.println("Adj" + pairString(5, 5) + "=" + lay.adj(5, 5));
  }

}
