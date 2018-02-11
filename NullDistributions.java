package io.github.thomasvstevens.disperser;

public class NullDistributions {
  /**
   * Print average energies for RandomLayouts.
   * @params args[0] number of permutations
   */
  public static void main(String[] args) {
    Disperser d = new AdjDisperser();
    RandomLayout rl = new RandomLayout();
    for (int i = 0; i < Integer.valueOf(args[0]); i++) {
      System.out.printf("%.6f", d.averageEnergy(rl));
      System.out.printf("\n");
      rl.permuteIds();
    }
  }

}
