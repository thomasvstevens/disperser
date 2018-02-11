package io.github.thomasvstevens.disperser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

abstract public class Disperser {

  // layout searching
  protected Layout source;
  protected Layout current;
  protected HashSet<Layout> seen;
  // optimization parameters
  protected int step;
  protected int maxSteps;

  public Disperser(Layout source, int maxSteps) {
    this.source = source;
    this.maxSteps = maxSteps;
    current = source;
    seen = new HashSet<Layout>(maxSteps);
    seen.add(source);
  }
  
  public Disperser(Layout source) {
    this(source, 100);
  }

  public Disperser() {
    this(new Layout(), 100);
  }

  public void minimize() {
    ArrayList<Integer> affected;
    Layout move;
    int delta = 0;
    int minDelta = 0;
    int energy = 0;
    boolean moved = true;
    step = 0;
    while (moved && step < maxSteps) {
      minDelta = 0;
      moved = false;
      for (int a = 0; a < source.m * source.n; a++) {
        for (int b = a + 1; b < source.m * source.n; b++) {
          move = new Layout(current);
          move.swapIds(a, b);
          affected = move.affectedIds(a, b);
          if (!seen.contains(move)) {
            delta = 0;
            for (int id : affected) {
              energy = computeEnergy(move, id);
              delta += energy - current.getEnergyById(id);
            }
            if (delta < minDelta) {
              minDelta = delta;
              current = move;
              moved = true;
            }
          }
        }
      }
      System.out.println(step + "\t" + delta);
      step++;
    }
  }

  abstract public int computeEnergy(Layout lay, int id);

  public double averageEnergy(Layout lay) {
    double total = 0.0;
    for (int id = 0; id < lay.m * lay.n; id++) {
      total += computeEnergy(lay, id);
    }
    return total / (lay.m * lay.n);
  }

}
