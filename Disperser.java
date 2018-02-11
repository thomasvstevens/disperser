package disperser;

import java.util.HashSet;
import java.util.PriorityQueue;

abstract public class Disperser {

  protected String[] labels;
  // layout searching
  protected Layout source;
  protected Layout current;
  protected RandomLayout rand;
  protected double avgEnergySource;
  protected double avgEnergyRand;
  protected double avgEnergyMin;
  // optimization parameters
  protected int step;
  protected int maxSteps;
  private static final int BEST_DELTA = -16;
  private static final double EPS = 0.0001;

  public Disperser(Layout source, int maxSteps) {
    this.source = source;
    this.maxSteps = maxSteps;
    rand = new RandomLayout(source);
    // Don't start from random. Local minimum likely for 8x12.
    current = source;
    step = 0;
  }
  
  public Disperser(Layout source) {
    this(source, 100);
  }

  public Disperser() {
    this(new Layout(), 100);
  }

  protected void defaultLabels() {
    labels = new String[source.m * source.n];
    char[] rowLabels = "ABCDEFGH".toCharArray();
    for (int i = 0; i < source.m; i++) {
      for (int j = 0; j < source.n; j++) {
        labels[i * source.n + j] = String.valueOf(rowLabels[i]) + String.valueOf(j + 1);
      }
    }
  }

  public void minimize() {
    Layout next = null;
    // iterate until fixed point or iteration limit
    while (current != next && step < maxSteps) {
      if (next != null) {
        current = next;
      }
      next = findSteepest();
      step++;
    }
    avgEnergyMin = averageEnergy(current);
  }

  public Layout findSteepest() {
    HashSet<Integer> affected;
    Layout move;
    Layout minMove = current;
    double delta = 0.0;
    double minDelta = 0.0;
    double energy = 0.0;
    for (int a = 0; a < source.m * source.n; a++) {
      for (int b = a + 1; b < source.m * source.n; b++) {
        move = new Layout(current);
        // randomize order of swap combinations
        rand.permuteIds();
        a = rand.getId(a);
        b = rand.getId(b);
        move.swapIds(a, b);
        affected = move.affectedIds(a, b);
        //if (!seen.contains(move)) {
          //seen.add(move);
        delta = 0;
        for (int id : affected) {
          energy = computeEnergy(move, id);
          delta += energy - current.getEnergyById(id);
        }
        // return if found best possible energy change
        if (Math.abs(delta - BEST_DELTA) < EPS) {
          System.out.printf(step + "\t" + "%2.6f" + "\n", delta);
          return move;
        }
        if (delta < minDelta && Math.abs(delta - minDelta) > EPS) {
          minDelta = delta;
          minMove = move;
        }
      }
    }
    System.out.printf(step + "\t" + "%2.6f" + "\n", minDelta);
    return minMove;
  }

  abstract public double computeEnergy(Layout lay, int id);

  public double averageEnergy(Layout lay) {
    double total = 0.0;
    for (int id = 0; id < lay.m * lay.n; id++) {
      total += computeEnergy(lay, id);
    }
    return total / (lay.m * lay.n);
  }

  @Override
  public String toString() {
    if (labels == null) {
      labels = new String[current.m * current.n];
      for (int k = 0; k < current.m * current.n; k++) {
        labels[current.getId(k)] = String.valueOf(current.getId(k));
      }
    }
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < current.m * current.n; k++) {
      sb.append(labels[current.getId(k)]);
      if (k % current.n == current.n - 1) {
        sb.append("\n");
      } else {
        sb.append("\t");
      }
    }
    return sb.toString();
  }

  protected void printExample(String header) {
    System.out.println("=== " + header + " ===");
    System.out.println(this);
    System.out.println(" Average Energy = " + averageEnergy(current) + "\n");
    current.printEnergies();
  }

}
