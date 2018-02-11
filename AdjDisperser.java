package disperser;

import java.util.ArrayList;
import java.util.HashSet;

public class AdjDisperser extends Disperser {

  private ArrayList<HashSet<Integer>> adjSource;
  private static final int DEFAULT_MAX_STEPS = 1000;

  public AdjDisperser(Layout source, int maxSteps) {
    super(source, maxSteps);
    adjSource = new ArrayList<HashSet<Integer>>(source.m * source.n);
    for (int k = 0; k < source.m * source.n; k++) {
      adjSource.add(source.adj(k));
    }
    avgEnergySource = averageEnergy(source);
  }

  public AdjDisperser(Layout source) {
    this(source, DEFAULT_MAX_STEPS);
  }

  public AdjDisperser() {
    this(new Layout(), DEFAULT_MAX_STEPS);
    defaultLabels();
  }

  @Override
  public double computeEnergy(Layout lay, int k) {
    double e = 0.0;
    int id = lay.getId(k);
    for (int a : lay.adj(k)) {
      if (adjSource.get(id).contains(a)) {
        e++;
      }
    }
    lay.setEnergyById(id, e);
    return e;
  }

  public static void main(String[] args) {
    Disperser d = new AdjDisperser();
    d.printExample("default");
    d.source.swapIds(0, 1);
    d.printExample("swap");
    d = new AdjDisperser(new RandomLayout());
    d.printExample("random");
  }

}
