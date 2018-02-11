package io.github.thomasvstevens.disperser;

import java.util.ArrayList;
import java.util.HashSet;

public class AdjDisperser extends Disperser {

  private ArrayList<HashSet<Integer>> adjSource;

  public AdjDisperser(Layout source, int maxSteps) {
    super(source, maxSteps);
    adjSource = new ArrayList<HashSet<Integer>>(source.m * source.n);
    for (int k = 0; k < source.m * source.n; k++) {
      adjSource.add(source.adj(k));
    }
  }

  public AdjDisperser(Layout source) {
    this(source, 100);
  }

  public AdjDisperser() {
    this(new Layout(), 100);
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
    System.out.println(d.source);
    System.out.println("Average Energy = " + d.averageEnergy(d.source) + "\n");
    d.source.printEnergies();
    d.source.swapIds(0, 1);
    System.out.println(d.source);
    System.out.println(" Average Energy = " + d.averageEnergy(d.source) + "\n");
    d.source.printEnergies();
    RandomLayout rl = new RandomLayout();
    System.out.println(rl);
    System.out.println(" Average Energy = " + d.averageEnergy(rl) + "\n");
    rl.printEnergies();
  }

}
