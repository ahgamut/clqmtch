package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.Stack;

public class StackDFS {
  private final Stack<SearchState> states;
  private final ArrayList<Integer> to_remove;
  int candidates_left;
  int clique_size;
  int clique_potential;
  int i;
  int j;
  int k;
  int vert;
  int start;
  int ans;

  StackDFS() {
    this.states = new Stack<>();
    this.to_remove = new ArrayList<>();
  }

  public void process_graph(Graph G) {
    this.states.ensureCapacity(G.CLIQUE_LIMIT);
    this.to_remove.ensureCapacity(G.CLIQUE_LIMIT);
    this.process_vertex(G, G.CUR_MAX_CLIQUE_LOCATION);
    for (i = G.n_vert - 1; i > 0; i--) {
      if (G.vertices.get(i).mcs <= G.CUR_MAX_CLIQUE_SIZE
          || G.CUR_MAX_CLIQUE_SIZE >= G.CLIQUE_LIMIT) {
        continue;
      }
      this.process_vertex(G, i);
    }
  }

  public void process_vertex(Graph G, int cur) {
    Vertex vcur = G.vertices.get(cur);
    SearchState x = new SearchState(vcur);
    this.clique_potential = 1;

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
        this.clique_potential++;
      }
    }

    if (this.clique_potential <= G.CUR_MAX_CLIQUE_SIZE) {
      return;
    }

    // always use std::move when pushing on to stack
    states.push(x);
    clique_size = 1;

    start = 0;
    while (!states.isEmpty()) {
      if (G.CUR_MAX_CLIQUE_SIZE >= G.CLIQUE_LIMIT) break;
      // strong assumption:
      // the top of the stack always leads to a clique larger than the current max
      // (checking is done before pushing on to the stack)
      SearchState cur_state = states.peek();
      candidates_left = cur_state.cand.cardinality();
      // clique_size == cur_state.res.cardinality()
      //
      // because clique_size (and effectively clique_potential)
      // are changed every time the stack changes,
      // calling res.count() is unnecessary.

      // in case cur_state.start_at was cleared,
      // move to the next valid position now,
      // so when we return to cur_state it is proper
      cur_state.start_at = cur_state.cand.nextSetBit(cur_state.start_at);

      for (j = cur_state.start_at; j < vcur.N; j = cur_state.start_at) {
        cur_state.cand.clear(j);
        cur_state.start_at = cur_state.cand.nextSetBit(cur_state.start_at + 1);
        candidates_left--;
        clique_potential = candidates_left + 1 + clique_size;

        // ensure only the vertices found in the below loop are removed later
        to_remove.clear();

        vert = vcur.neibs.get(j);
        start = G.vertices.get(vert).spos;

        for (k = cur_state.start_at;
            k < vcur.N && clique_potential > G.CUR_MAX_CLIQUE_SIZE;
            k = cur_state.cand.nextSetBit(k + 1)) {
          if (!G.vertices.get(vert).neibs.contains(k)) to_remove.add(k);

          start += ans;
          clique_potential = (candidates_left - to_remove.size()) + clique_size + 1;
        }

        // is the current maximum beatable?
        if (clique_potential > G.CUR_MAX_CLIQUE_SIZE) {
          // no candidates left => clique cannot grow

          // clique_potential = clique size + 1 ie the edge (cur, vert)
          // this clique has beaten the existing maximum
          if (candidates_left == 0) {
            // include vert as part of the clique and copy
            cur_state.res.set(j);
            vcur.bits.clear();
            vcur.bits.or(cur_state.res);
            vcur.mcs = clique_potential;
            G.CUR_MAX_CLIQUE_SIZE = clique_potential;
            G.CUR_MAX_CLIQUE_LOCATION = cur;

            // search can now continue without vert
            cur_state.res.clear(j);
          } else // clique may still grow to beat the maximum
          {
            SearchState future_state = new SearchState(cur_state);

            // remove invalid members from the candidate set
            for (int ll : to_remove) future_state.cand.clear(ll);

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
      if (j == vcur.N) {
        // all potential cliques that can contain (cur, v) have been searched
        // where v is at position cur_state.id in list of cur's neighbors
        states.pop();

        // remove v from clique consideration because the future
        // candidates will not have it as part of the clique
        cur_state.res.clear(cur_state.id);
        clique_size--;
      }
    }

    if (!states.empty()) // the search was terminated due to CLIQUE_LIMIT
    {
      // release memory of all candidates on the stack
      states.clear();
    }
    // release memory of x.res i.e. the space for
    // the clique allocated at the base of the stack
  }
}
