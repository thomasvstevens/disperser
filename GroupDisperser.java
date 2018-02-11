package disperser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class GroupDisperser extends Disperser {

  HashMap<String, Integer> counts;
  HashMap<String, Integer> edgeCounts;
  private static final int DEFAULT_MAX_STEPS = 1000;
  private static final String DELIM = ",";
  private static final double COST = 1;

  public GroupDisperser(String fileName, int maxSteps) {
    this.maxSteps = maxSteps;
    source = readLabels(fileName);
    rand = new RandomLayout(source);
    current = source;
    avgEnergySource = averageEnergy(source);
    step = 0;
  }

  public GroupDisperser(String fileName) {
    this(fileName, DEFAULT_MAX_STEPS);
  }

  private Layout readLabels(String fileName) {
    int m = 0;
    int n = 0;
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      counts = new HashMap<String, Integer>();
      edgeCounts = new HashMap<String, Integer>();
      ArrayList<String> labelList = new ArrayList<String>();
      String prev = "";
      String[] tokens;
      int i = 0;
      int j;
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        tokens = line.split(DELIM);
        j = 0;
        for (String t : tokens) {
          if (i == 0) {
            if (j == 0) {
              n = tokens.length;
            }
            incrementMap(edgeCounts, t);
          }
          // separate conditionals to count corners twice
          if (j == 0 || j == n - 1) {
            incrementMap(edgeCounts, t);
          }
          incrementMap(counts, t);
          labelList.add(t);
          j++;
        }
        i++;
        prev = line;
      }
      m = i;
      // prev holds the last line of the file
      tokens = prev.split(DELIM);
      for (String t : tokens) {
          incrementMap(edgeCounts, t);
      }
      m = labelList.size() / n;
      labels = new String[m * n];
      for (int k = 0; k < m * n; k++) {
        labels[k] = labelList.get(k);
      }
    } catch (IOException e) {
      System.out.println("File not found: " + fileName);
    }
    return new Layout(m, n);
  }

  private void incrementMap(HashMap<String, Integer> hm, String t) {
    if (hm.containsKey(t)) {
      hm.put(t, hm.get(t) + 1);
    } else {
      hm.put(t, 1);
    }
  }

  @Override
  public double computeEnergy(Layout lay, int k) {
    double e = 0.0;
    int id = lay.getId(k);
    for (int a : lay.adj(k)) {
      if (a < lay.m * lay.n) {
        if (labels[id].equals(labels[a])) {
          e += COST;
        }
      } else {
        // edge
        e += edgeContribution(id);
      }
    }
    lay.setEnergyById(id, e);
    return e;
  }

  private double edgeContribution(int id) {
    HashMap<String, Integer> leaveOutId = new HashMap<String, Integer>(edgeCounts);
    leaveOutId.put(labels[id], leaveOutId.get(labels[id]) - 1);
    return edgeEnergy(edgeCounts) - edgeEnergy(leaveOutId);
  }

  private double edgeEnergy(HashMap<String, Integer> hm) {
    double e = 0.0;
    for (String s : hm.keySet()) {
      e += Math.pow(hm.get(s), 2);
    }
    return Math.sqrt(e);
  }

  public static void main(String[] args) {
    Disperser d;
    if (args.length == 0) {
      d = new GroupDisperser("disperser/PQRS.csv", 1000);
    } else {
      d = new GroupDisperser(args[0]);
    }
    d.printExample("source groups");
    d.minimize();
    d.printExample("minimized");
    d = new GroupDisperser("disperser/PQRS.csv", 1000);
    d.current = new RandomLayout(d.current);
    d.printExample("random");
    d.minimize();
    d.printExample("random minimized");
  }

}
