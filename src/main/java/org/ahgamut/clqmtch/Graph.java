package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.BitSet;

public class Graph
{
  int n_vert;
  int n_edges;
  int max_degree;
  int CUR_MAX_CLIQUE_SIZE;
  int CUR_MAX_CLIQUE_LOCATION;
  int CLIQUE_LIMIT;
  ArrayList<Vertex> vertices;

  Graph()
  {
    this.n_vert = 0;
    this.n_edges = 0;
    this.max_degree = 0;
    this.CUR_MAX_CLIQUE_SIZE = 0;
    this.CUR_MAX_CLIQUE_LOCATION = 0;
    this.CLIQUE_LIMIT = 1;
    this.vertices = new ArrayList<>();
  }

  public void load_edges(
    int num_vertices, int num_edges, ArrayList<EdgeValue> edges)
  {
    this.n_vert = num_vertices;
    this.n_edges = num_edges;
    this.vertices.ensureCapacity(num_vertices);

    int i = 0;
    int j = 0;
    int count;
    int spos = 0;
    ArrayList<Integer> vneibs = new ArrayList<>();
    for (i = 0; i < num_vertices; ++i)
    {
      for (count = 0; j < num_edges && edges.get(j).first == i; ++j)
      {
        vneibs.add(edges.get(j).second);
        if (edges.get(j).second == i)
          spos = i;
        count += 1;
      }
      this.vertices.add(new Vertex(vneibs));
      this.vertices.get(i).spos = spos;
      if (count > this.max_degree)
        this.max_degree = count;
      vneibs.clear();
    }
    this.set_bounds();
  }

  public void set_bounds()
  {
    int cur;
    int j;
    int vert;
    int mcs;
    BitSet sg = new BitSet(this.n_vert);
    for (cur = 0; cur < this.n_vert; cur++)
    {
      sg.clear();
      mcs = 0;
      // greedy vertex coloring: vertex cur is assigned
      // a color one more than the largest color among
      // the vertices to which it is connected
      // (can possibly do a bitset thing here)
      for (j = 0; j < this.vertices.get(cur).spos; j++)
      {
        vert = this.vertices.get(cur).neibs.get(j);
        sg.set(this.vertices.get(vert).mcs);
      }
      for (j = 0; sg.get(j) && j < this.vertices.get(cur).N + 1; ++j);
      mcs = j;
      this.vertices.get(cur).mcs = mcs;
      if (mcs > this.CLIQUE_LIMIT)
      {
        this.CUR_MAX_CLIQUE_LOCATION = cur;
        this.CLIQUE_LIMIT = mcs;
      }
    }
  }

  public ArrayList<Integer> get_max_clique()
  {
    return this.get_max_clique(this.CUR_MAX_CLIQUE_LOCATION);
  }

  public ArrayList<Integer> get_max_clique(int id)
  {
    return this.vertices.get(id).give_clique();
  }

  public void disp()
  {
    for (int i = 0; i < this.n_vert; ++i)
    {
      this.vertices.get(i).disp();
    }
  }
}
