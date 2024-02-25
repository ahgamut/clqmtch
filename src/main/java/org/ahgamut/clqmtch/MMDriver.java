package org.ahgamut.clqmtch;

/** Hello world! */
public class MMDriver {
  public static void main(String[] args) {
    MMReader reader = new MMReader();
    reader.readFile(args[0]);
    if (reader.edges.size() > 0) {
      Graph g = new Graph();
      g.load_edges(reader.num_vertices, reader.num_edges, reader.edges);
      // g.disp();
      // StackDFS s = new StackDFS();
      // s.process_graph(g);
      // g.disp();
      // System.out.println(g.get_max_clique().toString());
    }
  }
}
