package disperser;

import java.util.ArrayList;
import java.util.HashSet;

public class AdjDisperser extends Disperser {

  private ArrayList<HashSet<Integer>> adjSource;
  private static final double BEST_DELTA = -16;
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
    AdjDisperser ad;
    switch (args.length) {
      case 0:  ad = new AdjDisperser();
               break;
      case 2:  ad = new AdjDisperser(
                 new Layout(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
               break;
      case 3:  ad = new AdjDisperser(
                 new Layout(Integer.parseInt(args[0]), Integer.parseInt(args[1])),
                   Integer.parseInt(args[2]));
               break;
      default: System.out.println("USAGE java disperser/AdjDisperser <m> <n> <maxIterations>");
               return;
  }
  ad.printStatus("Input");
  ad.minimize();
  ad.printStatus("Output");
  }

}
