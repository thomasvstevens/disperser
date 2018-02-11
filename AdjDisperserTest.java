package disperser;

public class AdjDisperserTest {
  
  public static void main(String[] args) {
    Disperser d = new AdjDisperser();
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
