package disperser;

import java.util.HashSet;
import java.util.PriorityQueue;

abstract public class Disperser {

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
    int delta = 0;
    int minDelta = 0;
    int energy = 0;
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
        if (delta == BEST_DELTA) {
          System.out.println(step + "\t" + delta);
          return move;
        }
        if (delta < minDelta) {
          minDelta = delta;
          minMove = move;
        }
      }
    }
    System.out.println(step + "\t" + minDelta);
    return minMove;
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
