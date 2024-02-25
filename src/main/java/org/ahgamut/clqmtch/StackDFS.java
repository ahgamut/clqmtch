package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Stack;

public class StackDFS {

  public void process_graph(Graph G) {
    Stack<SearchState> states = new Stack<>();
    ArrayList<Integer> to_remove = new ArrayList<>();
    states.ensureCapacity(G.CLIQUE_LIMIT);
    to_remove.ensureCapacity(G.CLIQUE_LIMIT);
    this.process_vertex(G, G.CUR_MAX_CLIQUE_LOCATION, states, to_remove);
    for (int i = G.n_vert - 1; i >= 0 && G.CUR_MAX_CLIQUE_SIZE < G.CLIQUE_LIMIT; i--) {
      if (G.vertices.get(i).mcs <= G.CUR_MAX_CLIQUE_SIZE) {
        continue;
      }
      this.process_vertex(G, i, states, to_remove);
    }
  }

  private void process_vertex(
      Graph G, int cur, Stack<SearchState> states, ArrayList<Integer> to_remove) {
    int candidates_left;
    int clique_size;
    int clique_potential;
    int j;
    int k;
    int vert;
    Vertex vcur = G.vertices.get(cur);
    Vertex vvert;
    SearchState x = new SearchState(vcur);
    BitSet res = new BitSet(vcur.N);
    res.set(vcur.spos);
    clique_potential = 1;

    // suppose the graph was only upto vertex cur
    // and cur has the coloring number K, we shall
    // consider only neighbors of cur that have a
    // smaller coloring number than K. (note that
    // no neighbor of cur can coloring number = K)
    // so no matter what the rest of the graph is,
    // we know that this "truncated" graph can contain
    // upto a clique of size K, so let's try to find it.

    for (j = 0; j < vcur.N; j++) {
      vert = vcur.neibs.get(j);
      if (G.vertices.get(vert).mcs < vcur.mcs) {
        x.cand.set(j);
        clique_potential++;
      }
    }

    if (clique_potential <= G.CUR_MAX_CLIQUE_SIZE) return;

    // always use std::move when pushing on to stack
    states.push(x);
    clique_size = 1;

    while (!states.isEmpty()) {
      if (G.CUR_MAX_CLIQUE_SIZE >= G.CLIQUE_LIMIT) break;
      // strong assumption:
      // the top of the stack always leads to a clique larger than the current max
      // (checking is done before pushing on to the stack)
      SearchState cur_state = states.peek();
      // System.out.printf("%d %d %s\n", cur, states.size(), cur_state.toString());
      candidates_left = cur_state.cand.cardinality();
      // clique_size == cur_state.res.cardinality()
      //
      // because clique_size (and effectively clique_potential)
      // are changed every time the stack changes,
      // calling res.count() is unnecessary.

      // in case cur_state.start_at was cleared,
      // move to the next valid position now,
      // so when we return to cur_state it is proper
      // cur_state.start_at = cur_state.cand.nextSetBit(cur_state.start_at);

      for (j = cur_state.start_at; j >= 0 && j < vcur.N; j = cur_state.start_at) {
        cur_state.cand.clear(j);
        cur_state.start_at = cur_state.cand.nextSetBit(j);
        candidates_left--;
        clique_potential = candidates_left + 1 + clique_size;

        // ensure only the vertices found in the below loop are removed later
        to_remove.clear();

        vert = vcur.neibs.get(j);
        vvert = G.vertices.get(vert);

        for (k = cur_state.start_at;
            k >= 0 && k < vcur.N && clique_potential > G.CUR_MAX_CLIQUE_SIZE;
            k = cur_state.cand.nextSetBit(k + 1)) {
          if (!vvert.neibs.contains(vcur.neibs.get(k))) {
            to_remove.add(k);
            clique_potential -= 1;
          }
        }

        // is the current maximum beatable?
        if (clique_potential > G.CUR_MAX_CLIQUE_SIZE) {
          // no candidates left => clique cannot grow

          // clique_potential = clique size + 1 ie the edge (cur, vert)
          // this clique has beaten the existing maximum
          if (candidates_left == 0) {
            // include vert as part of the clique and copy
            res.set(j);
            vcur.bits.clear();
            vcur.bits.or(res);
            vcur.mcs = clique_potential;
            G.CUR_MAX_CLIQUE_SIZE = clique_potential;
            G.CUR_MAX_CLIQUE_LOCATION = cur;

            // search can now continue without vert
            res.clear(j);
          } else // clique may still grow to beat the maximum
          {
            SearchState future_state = new SearchState(cur_state);
            // remove invalid members from the candidate set
            for (int ll : to_remove) future_state.cand.clear(ll);
            res.set(j);
            future_state.id = j;
            future_state.start_at = future_state.cand.nextSetBit(0);

            // clique_potential check has happened before pushing on to the
            // stack; strong assumption is therefore valid
            states.push(future_state);

            // clique_size has increased by 1 due to vert
            clique_size++;
            // the top of the stack has changed,
            // prevent any further operations on cur_state
            break;
          }
        }
        // clique_potential <= CUR_MAX_CLIQUE_SIZE, so
        // this subtree cannot beat the maximum.
        // consider the next value for vert and try again.
      }

      // all verts with id > cur_state.id have been checked
      if (j < 0 || j >= vcur.N) {
        // all potential cliques that can contain (cur, v) have been searched
        // where v is at position cur_state.id in list of cur's neighbors
        states.pop();

        // remove v from clique consideration because the future
        // candidates will not have it as part of the clique
        res.clear(cur_state.id);
        clique_size--;
      }
    }

    if (!states.empty()) // the search was terminated due to CLIQUE_LIMIT
    {
      states.clear();
    }
  }
}
