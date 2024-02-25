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
      StackDFS s = new StackDFS();
      s.process_graph(g);
      // HeuristicSearch s = new HeuristicSearch();
      // enumerate
      EnumerateDFS e = new EnumerateDFS(g.CUR_MAX_CLIQUE_SIZE - 1);
      int v = e.process_graph(g);
      int count = 0;
      while (v < g.n_vert) {
        ArrayList<Integer> t = g.get_max_clique(v);
        v = e.process_graph(g);
        System.out.println(count + ":" + t.size() + " " + t.toString());
        count += 1;
      }
      System.out.printf("%d cliques found\n", count);
    }
  }
}
