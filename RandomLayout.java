package disperser;

import java.util.Random;

public class RandomLayout extends Layout {

  private static final Random rand = new Random();

  public RandomLayout(int m, int n) {
    super(m, n);
    permuteIds();
  }

  public RandomLayout() {
    this(8, 12);
  }

  public RandomLayout(Layout lay) {
    super(lay);
    permuteIds();
  }

  public void permuteIds() {
    for (int k = m * n - 1; k > 0; k--) {
      swapIds(k, rand.nextInt(k)); // 0 to k-1 inclusive
    }
  }

  public static void main(String[] args) {
    RandomLayout rlay = new RandomLayout(8, 12);
    System.out.println(rlay);
    RandomLayout rlayNew = new RandomLayout(8, 12);
    System.out.println(rlayNew);
    rlay.permuteIds();
    System.out.println(rlay);
  }
}
