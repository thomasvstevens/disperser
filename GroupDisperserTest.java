package disperser;

public class GroupDisperserTest {
  
  public static void main(String[] args) {
    GroupDisperser gd;
    if (args.length == 0) {
      gd = new GroupDisperser("disperser/PQRS.csv", 100);
    } else {
      gd = new GroupDisperser(args[0]);
    }
    gd.printStatus("source groups");
    System.out.println(gd.source);
    // isoenergetic swap
    gd.source.swapIds(13, 37);
    System.out.println("delta(swap) = " +
      gd.computeDelta(gd.source, 13, 37));
    System.out.println("current edgeEnergy = " + gd.current.getEdgeEnergy());
    gd.printStatus("isoenergetic swap (13, 37)");
    System.out.println(gd.source);
    gd.printCounts();
    // group repulsion only swap
    gd.source.swapIds(73, 76);
    System.out.println("delta(swap) = " +
      gd.computeDelta(gd.source, 73, 76));
    System.out.println("current edgeEnergy = " + gd.current.getEdgeEnergy());
    gd.printStatus("group repulsion only swap (73, 76)");
    System.out.println(gd.source);
    gd.printCounts();
    // edge balancing only swap
    gd.source.swapIds(75, 84);
    System.out.println("delta(swap) = " +
      gd.computeDelta(gd.source, 75, 84));
    System.out.println("current edgeEnergy = " + gd.current.getEdgeEnergy());
    gd.printStatus("edge balancing only swap (75, 84)");
    System.out.println(gd.source);
    gd.printCounts();
    // random
    gd = new GroupDisperser("disperser/PQRS.csv");
    gd.current = new RandomLayout(gd.current);
    System.out.println("current edgeEnergy = " + gd.current.getEdgeEnergy());
    gd.printStatus("random");
    gd.printCounts();
    // minimization
    gd = new GroupDisperser("disperser/PQRS.csv", 100);
    gd.printStatus("source");
    gd.printCounts();
    gd.minimize();
    System.out.println("current edgeEnergy = " + gd.current.getEdgeEnergy());
    gd.printStatus("minimized");
    gd.printCounts();
  }

}
