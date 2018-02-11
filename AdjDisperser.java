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
  }

  @Override
  public int computeEnergy(Layout lay, int k) {
    int e = 0;
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
    System.out.println("=== default ===");
    System.out.println(d.source);
    System.out.println(" Average Energy = " + d.averageEnergy(d.source) + "\n");
    d.source.printEnergies();
    System.out.println("=== swap ===");
    d.source.swapIds(0, 1);
    System.out.println(d.source);
    System.out.println(" Average Energy = " + d.averageEnergy(d.source) + "\n");
    d.source.printEnergies();
    System.out.println("=== random ===");
    RandomLayout rl = new RandomLayout();
    System.out.println(rl);
    System.out.println(" Average Energy = " + d.averageEnergy(rl) + "\n");
    rl.printEnergies();
    System.out.println("=== minimized 4x6 quadrant ===");
    d = new AdjDisperser(new Layout(4, 6));
    d.minimize();
    System.out.println(d.current);
    d.current.printEnergies();
    System.out.println(" Step = " + d.step +", Average Energy = " + d.averageEnergy(d.current) + "\n");
    System.out.println("=== minimized 8x12 default ===");
    d = new AdjDisperser(new Layout());
    d.minimize();
    System.out.println(d.current);
    d.current.printEnergies();
    System.out.println(" Step = " + d.step +", Average Energy = " + d.averageEnergy(d.current) + "\n");
    System.out.println("=== larger: 384-well plate ===");
    d = new AdjDisperser(new Layout(16, 24));
    d.minimize();
    System.out.println(d.current);
    d.current.printEnergies();
    System.out.println(" Step = " + d.step +", Average Energy = " + d.averageEnergy(d.current) + "\n");
  }

}
