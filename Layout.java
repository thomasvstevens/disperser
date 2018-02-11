package disperser;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Container (m x n) for samples with energies. */
public class Layout {

  // m rows x n columns
  public final int m;
  public final int n;
  // row-major order 1D arrays
  private int[] ids;
  private double[] energies;
  // special element for each 2D boundary
  private static final int N_EDGES = 4;
  private static final String[] EDGES = {"top", "bottom", "left", "right"};
  private HashMap<String, Integer> edgeIds;

  public Layout(int m, int n) {
    this.m = m;
    this.n = n;
    ids = new int[m * n];
    energies = new double[m * n];
    Arrays.fill(energies, 0.0);
    int k = 0;
    while (k < m * n) {
      ids[k] = k++;
    }
    edgeIds = new HashMap<String, Integer>(N_EDGES);
    for (String edge : EDGES) {
      edgeIds.put(edge, k++);
    }
  }

  public Layout() {
    this(8, 12);
  }

  /** Deep copies of id[], energies[] */
  public Layout(Layout old) {
    this(old.m, old.n);
    for (int k = 0; k < old.m * old.n; k++) {
      this.ids[k] = old.ids[k];
      this.energies[k] = old.energies[k];
    }
  }

  public int getId(int i, int j) {
    checkBounds(i, j);
    return ids[i * n + j];
  }

  public int getId(int k) {
    checkBounds(k);
    return ids[k];
  }

  public double getEnergyById(int id) {
    checkBounds(id);
    return energies[id];
  }

  public void setEnergyById(int id, double e) {
    checkBounds(id);
    energies[id] = e;
  }

  public HashSet<Integer> adj(int k) {
    return adj(k / n, k % n);
  }

  /** Return ids of 4-neighbors of element (i,j) */
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
  /** Swap ids at two linear indices. */
  public void swapIds(int a, int b) {
    checkBounds(a);
    checkBounds(b);
    int tmp = ids[a];
    ids[a] = ids[b];
    ids[b] = tmp;
  }

  /** Get self and neighbors affected by swap
   * @return list of ids whose neighbors changed due to swap
   */
  public HashSet<Integer> affectedIds(int a, int b) {
    HashSet<Integer> affected = new HashSet<Integer>();
    affected.add(a);
    affected.add(b);
    for (int adja : adj(a)) {
      affected.add(adja);
    }
    for (int adjb : adj(b)) {
      affected.add(adjb);
    }
    for (int edge : edgeIds.values()) {
      affected.remove(edge);
    }
    return affected;
  }

  private void checkBounds(int i, int j) {
    if (i * n + j > m * n - 1) {
      throw new IllegalArgumentException(pairString(i, j) + " out of bounds.");
    }
  }

  private void checkBounds(int k) {
    if (k > m * n - 1) {
      throw new IllegalArgumentException("Linear index " + k + " out of bounds.");
    }
  }

  public static String pairString(int i, int j) {
    return "(" + i + "," + j + ")";
  }

  public void printEnergies() {
    for (int k = 0; k < m * n; k++) {
      System.out.printf("%3.1f", energies[ids[k]]);
      if (k % n == n - 1) {
        System.out.print("\n");
      } else {
        System.out.print("\t");
      }
    }
    System.out.println();
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
    lay.printEnergies();
    System.out.println("Adj" + pairString(0, 0) + "=" + lay.adj(0, 0));
    System.out.println("Adj" + pairString(7, 0) + "=" + lay.adj(7, 0));
    System.out.println("Adj" + pairString(0, 11) + "=" + lay.adj(0, 11));
    System.out.println("Adj" + pairString(7, 11) + "=" + lay.adj(7, 11));
    System.out.println("Adj" + pairString(5, 5) + "=" + lay.adj(5, 5));
  }

}
