package disperser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class GroupDisperser extends Disperser {

  HashMap<String, Integer> counts;
  HashMap<String, Integer> goalEdgeCounts;
  private static final int DEFAULT_MAX_STEPS = 1000;
  private static final String DELIM = ",";
  private static final double ADJ_GROUP_COST = 1;

  public GroupDisperser(String fileName, int maxSteps) {
    this.maxSteps = maxSteps;
    source = readLabels(fileName);
    current = source;
    setGoalEdgeCounts();
    setEdgeEnergy(current);
    rand = new RandomLayout(source);
    avgEnergySource = averageEnergy(source);
    step = 0;
  }

  public GroupDisperser(String fileName) {
    this(fileName, DEFAULT_MAX_STEPS);
  }

  private Layout readLabels(String fileName) {
    int m = 0;
    int n = 0;
    counts = new HashMap<String, Integer>();
    HashMap<String, Integer> edgeCounter = new HashMap<String, Integer>();
    ArrayList<String> labelList = new ArrayList<String>();
    String prev = "";
    String[] tokens;
    int i = 0;
    int j;
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        tokens = line.split(DELIM);
        j = 0;
        for (String t : tokens) {
          if (i == 0) {
            if (j == 0) {
              n = tokens.length;
            }
            incrementMap(edgeCounter, t);
          }
          // separate conditionals to count corners twice
          if (j == 0 || j == n - 1) {
            incrementMap(edgeCounter, t);
          }
          incrementMap(counts, t);
          labelList.add(t);
          j++;
        }
        i++;
        prev = line;
      }
    } catch (IOException e) {
      System.out.println("File not found: " + fileName);
    }
    m = i;
    // prev retains the last line of the file
    tokens = prev.split(DELIM);
    for (String t : tokens) {
        incrementMap(edgeCounter, t);
    }
    labels = new String[m * n];
    for (int k = 0; k < m * n; k++) {
      labels[k] = labelList.get(k);
    }
    Layout lay = new Layout(m, n);
    lay.edgeCounts = new HashMap<String, Integer>(edgeCounter);
    return lay;
  }

  private void incrementMap(HashMap<String, Integer> hm, String t) {
    if (hm.containsKey(t)) {
      hm.put(t, hm.get(t) + 1);
    } else {
      hm.put(t, 1);
    }
  }

  private void setGoalEdgeCounts() {
    int sumCounts = 0;
    for (int v : counts.values()) {
      sumCounts += v;
    }
    int sumEdgeCounts = 0;
    for (int v : source.edgeCounts.values()) {
      sumEdgeCounts += v;
    }
    // program integer values to allow zero minimum
    // leave one label for last, forcing sum(goalEdgeCounts) = sumEdgeCounts
    HashSet<String> subKeys = new HashSet<String>(counts.keySet());
    String last = labels[0];
    subKeys.remove(labels[0]);
    int goal;
    goalEdgeCounts = new HashMap<String, Integer>();
    double ratio = (double) sumEdgeCounts / (double) sumCounts;
    for (String s : subKeys) {
      goal = (int) Math.round(counts.get(s) * ratio);
      goalEdgeCounts.put(s, goal);
      sumEdgeCounts -= goal;
    }
    goalEdgeCounts.put(last, sumEdgeCounts);
  }

  @Override
  public double computeEnergy(Layout lay, int k) {
    double e = 0.0;
    int id = lay.getId(k);
    for (int a : lay.adj(k)) {
      if (!lay.isEdgeId(a)) {
        if (labels[id].equals(labels[a])) {
          e += ADJ_GROUP_COST;
        }
      }
    }
    lay.setEnergyById(id, e);
    return e;
  }
  
  private void setEdgeEnergy(Layout lay) {
    double rmse = 0.0;
    for (String s : counts.keySet()) {
      rmse += Math.pow(lay.edgeCounts.get(s) - goalEdgeCounts.get(s), 2);
    }
    rmse /= counts.size();
    lay.setEdgeEnergy(Math.sqrt(rmse));
  }
  
  /** First, update edgeCounts using adjacency lists of swapped indices. */
  @Override
  public double computeDelta(Layout move, int a, int b) {
    String toALabel = labels[move.getId(a)];
    String toBLabel = labels[move.getId(b)];
    /*
    System.out.println("to(" + a + ")=" + toALabel);
    System.out.println("to(" + b + ")=" + toBLabel);
    System.out.println("adj(" + a + ")=" + move.adjList(a));
    System.out.println("adj(" + b + ")=" + move.adjList(b));
    */
    for (int adjA : move.adjList(a)) {
      if (move.isEdgeId(adjA)) {
        move.edgeCounts.put(toALabel, move.edgeCounts.get(toALabel) + 1);
        move.edgeCounts.put(toBLabel, move.edgeCounts.get(toBLabel) - 1);
      }
    }
    for (int adjB : move.adjList(b)) {
      if (move.isEdgeId(adjB)) {
        move.edgeCounts.put(toBLabel, move.edgeCounts.get(toBLabel) + 1);
        move.edgeCounts.put(toALabel, move.edgeCounts.get(toALabel) - 1);
      }
    }
    double delta = super.computeDelta(move, a, b);
    setEdgeEnergy(move);
    delta += (move.getEdgeEnergy() - current.getEdgeEnergy());
    return delta;
  }

  public void printCounts() {
    for (String s : counts.keySet()) {
      System.out.println("counts[" + s + "]\t\t" + counts.get(s));
      System.out.println("current.edgeCounts[" + s + "]\t\t" + current.edgeCounts.get(s));
      System.out.println("goalEdgeCounts[" + s + "]\t\t" + goalEdgeCounts.get(s));
    }
  }

  @Override
  protected void printStatus(String header) {
    System.out.println("=== " + header + " ===");
    System.out.printf("\n  Average group adjacency energy = %2.6f", averageEnergy(current));
    System.out.printf("\n  Edge balance energy = %2.6f\n\n", current.getEdgeEnergy());
    current.printEnergies();
    System.out.println(this);
  }

  public static void main(String[] args) {
    GroupDisperser gd;
    switch (args.length) {
      case 1:  gd = new GroupDisperser(args[0]);
               break;
      case 2:  gd = new GroupDisperser(args[0], Integer.parseInt(args[1]));
               break;
      default: System.out.println("USAGE: java disperser/GroupDisperser layout.csv <maxSteps>");
               return;
    }
    gd.printStatus("Input");
    gd.minimize();
    gd.printStatus("Output");
  }

}
