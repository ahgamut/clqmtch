package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

public class HeuristicSearch {
  public void process_graph(Graph G) {
    int i;
    ArrayList<VData> neighbors = new ArrayList<>();
    neighbors.ensureCapacity(G.max_degree);

    // "memory" allocations for cand, res
    BitSet res = new BitSet(G.max_degree);
    BitSet cand = new BitSet(G.max_degree);

    process_vertex(G, G.CUR_MAX_CLIQUE_LOCATION, res, cand, neighbors);
    for (i = 0; i < G.n_vert && G.CUR_MAX_CLIQUE_SIZE < G.CLIQUE_LIMIT; i++)
    {
      if (G.vertices.get(i).mcs <= G.CUR_MAX_CLIQUE_SIZE) continue;
      process_vertex(G, i, res, cand, neighbors);
    }
  }

  private void process_vertex(Graph G, int cur, BitSet res, BitSet  cand, ArrayList<VData> neighbors) {
    // heuristic assumes that higher degree neighbors are
    // more likely to be part of a clique
    // so it goes through them in O(N^2) to find a clique
    // (dfs is exponential complexity)
    Vertex vcur = G.vertices.get(cur);
    Vertex vvert;
    res.clear(0, vcur.N);
    cand.clear(0, vcur.N);
    res.set(vcur.spos);

    int ans;
    int i, j;
    int candidates_left = 0;
    int cur_clique_size = 1;
    int cand_max = 0;
    int clique_potential = 0;
    VData dat = new VData();
    VData dat2;

    // find all neighbors of cur and sort by decreasing color
    for (i = 0, j = 0; i < vcur.N; i++)
    {
      ans = vcur.neibs.get(i);
      vvert = G.vertices.get(ans);
      dat.load(ans, vvert.mcs, i);
      if (dat.N > vcur.mcs) continue;
      neighbors.add(dat);
      dat = new VData();
      cand.set(i);
      candidates_left++;
    }
    if (candidates_left <= G.CUR_MAX_CLIQUE_SIZE) return;
    Collections.sort(neighbors, Collections.<VData>reverseOrder());

    cand_max = candidates_left;
    // let neib be a high-color neighbor of cur that hasn't been searched earlier
    for (i = 0; i < cand_max; i++)
    {
      dat = neighbors.get(i);
      vvert = G.vertices.get(dat.id);
      // should neib be considered as a candidate?
      if (!cand.get(dat.pos)) continue;

      // it can be part of the current clique
      res.set(dat.pos);
      cur_clique_size++;
      cand.clear(dat.pos);
      candidates_left--;

      // assume neib is a worthwhile candidate
      // modify candidate list: remove all vertices that are not adjacent to neib
      for (j = i + 1; j < cand_max; j++)
      {
        dat2 = neighbors.get(j);

        if (!cand.get(dat2.pos) || vvert.neibs.contains(dat2.id))
          continue;
        else
        {
          candidates_left--;
          cand.clear(dat2.pos);
        }
      }

      clique_potential = cur_clique_size + candidates_left;

      if (clique_potential <= G.CUR_MAX_CLIQUE_SIZE)
      {
        // heuristic assumption was not useful, because
        // potential clique with neib cannot beat the maximum
        break;
      }
      else if (candidates_left == 0)
      {
        // there are no candidates left =>
        // potential has been realized and beaten the current maximum
        // so save the clique's data as the new global maximum
        G.CUR_MAX_CLIQUE_SIZE = cur_clique_size;
        G.CUR_MAX_CLIQUE_LOCATION = cur;
        vcur.bits.clear();
        vcur.bits.or(res);
        /* Note that we are not saving cur.mcs because a proper search through
         * the vertex may give a larger clique */
        break;
      }
      // else, this clique still has potential to beat the maximum, and
      // some candidates left to try, so continue on with the loop
    }
  }

  static class VData implements Comparable<VData>{
    int id, N, pos;

    VData() {
      id = 0;
      N = 0;
      pos = 0;
    }

    public void load(int id, int N, int pos) {
      this.id = id;
      this.N = N;
      this.pos = pos;
    }

    public int compareTo(VData other) {
      return Integer.compare(this.N, other.N);
    }
  }
}
