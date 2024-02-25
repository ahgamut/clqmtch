package org.ahgamut.clqmtch;

import java.util.ArrayList;

/** Hello world! */
public class MMDriver {
  public static void main(String[] args) {
    MMReader reader = new MMReader();
    reader.readFile(args[0]);
    if (!reader.edges.isEmpty()) {
      Graph g = new Graph();
      g.load_edges(reader.num_vertices, reader.num_edges, reader.edges);
      System.out.println(g.toString());
      // StackDFS s = new StackDFS();
      HeuristicSearch s = new HeuristicSearch();
      s.process_graph(g);
      ArrayList<Integer> t = g.get_max_clique();
      System.out.println(t.size() + " " + t.toString());
    }
  }
}
