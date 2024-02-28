package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.Stack;

public class Mapper3Test {
  public static void main(String[] args) {
    double[][] q_pts = {
      {-5, 0},
      {5, 0},
      {0, 10}
    };

    double[][] k_pts = {
            {-10, 0},
            {10, 0},
            {0, 20}
    };

    Mapper3 m = new Mapper3();
    Graph g = m.construct_graph(q_pts, 3, k_pts, 3, 0.005, 0.1, 0.25, 2.5);
    StackDFS s = new StackDFS();
    s.process_graph(g);
    ArrayList<Integer> c = g.get_max_clique();
    int qi;
    int ki;
    for (int i = 0; i < c.size(); ++i) {
        qi = c.get(i) / 3;
        ki = c.get(i) % 3;
        System.out.printf("(%f, %f) -> (%f, %f)\n", q_pts[qi][0], q_pts[qi][1], k_pts[ki][0], k_pts[ki][1]);
    }
    System.out.println(c.toString());
  }
}
