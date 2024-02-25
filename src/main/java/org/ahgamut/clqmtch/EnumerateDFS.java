package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Stack;

public class EnumerateDFS {
  int REQUIRED_SIZE;
  int clique_potential;
  int clique_size;
  int cur;
  Stack<SearchState> states;
  ArrayList<Integer> to_remove;
  BitSet res;

  EnumerateDFS(int r) {
    this.REQUIRED_SIZE = r;
    this.cur = 0;
    this.states = new Stack<>();
    this.to_remove = new ArrayList<>();
    this.res = null;
  }

  public int process_graph(Graph G) {
    if (REQUIRED_SIZE <= 0) { // 0 cliques of size 0, dummy case
      return G.n_vert;
    } else if (REQUIRED_SIZE == 1 && cur < G.n_vert) {
      // every vertex is a clique of size 1, dummy case
      Vertex vcur = G.vertices.get(cur);
      vcur.bits.clear();
      vcur.bits.set(vcur.spos);
      return cur++;
    }

    states.ensureCapacity(G.CLIQUE_LIMIT);
    to_remove.ensureCapacity(G.CLIQUE_LIMIT);
    int vert;
    int j;
    int k;
    int candidates_left;
    Vertex vcur;
    Vertex vvert;

    while (cur < G.n_vert) // go through all vertices of the graph
    {
      vcur = G.vertices.get(cur);
      if (states.empty()) {
        this.process_vertex(G);
        // a new SearchState has been pushed onto base of the stack
        // OR there are no more vertices left
        continue; // check if cur < G.n_vert
      }

      // strong assumption:
      // the top of the stack always leads to a clique larger than the current max
      // (checking is done before pushing on to the stack)
      SearchState cur_state = states.peek();
      candidates_left = cur_state.cand.cardinality();
      // clique_size == cur_state.res.count()
      //
      // because clique_size (and effectively clique_potential)
      // are changed every time the stack changes,
      // calling res.count() is unnecessary.

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
            k >= 0 && k < vcur.N && clique_potential >= REQUIRED_SIZE;
            k = cur_state.cand.nextSetBit(k + 1)) {
          if (!vvert.neibs.contains(vcur.neibs.get(k))) {
            to_remove.add(k);
            clique_potential--;
          }
        }

        // is it possible to produce a clique of REQUIRED_SIZE?
        if (clique_potential >= REQUIRED_SIZE) {
          // now candidates_left may be nonzero, but only the clique size is
          // necessary. Add 1 so as to count vert as part of the clique
          if (clique_size + 1 >= REQUIRED_SIZE) {
            res.set(j);
            vcur.bits.clear();
            vcur.bits.or(res);
            // search can now continue without vert
            res.clear(j);

            // now the caller can call G.get_max_clique(cur)
            return cur;
            // note that the top of the stack has not been changed,
            // and res is saved as part of the object
            // when process_graph is called again, it will resume searching
            // at this subtree.
          } else // clique may still grow to REQUIRED_SIZE
          {
            SearchState future_state = new SearchState(cur_state);
            // remove invalid members from the candidate set
            for (int ll : to_remove) future_state.cand.clear(ll);
            res.set(j);
            future_state.id = j;
            future_state.start_at = future_state.cand.nextSetBit(0);

            // clique_potential has been checked before pushing on to the
            // stack; strong assumption is therefore valid
            states.push(future_state);

            // clique_size has increased by 1 due to vert
            clique_size++;
            // the top of the stack has changed,
            // prevent any further operations on cur_state
            break;
          }
        }
        // clique_potential <= REQUIRED_SIZE, so
        // this subtree cannot produce any required clique.
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

    // now cur = G.n_vert, there are no more cliques to be found
    // the search stack is empty, and
    // all the necessary G.clear_memory() calls have been made
    return cur;
  }

  public void process_vertex(Graph G) {
    // continue until vertex this->cur can possibly build a clique of REQUIRED_SIZE
    for (cur++; cur < G.vertices.size(); cur++) {
      if (G.vertices.get(cur).mcs < this.REQUIRED_SIZE) continue;
      if (load_vertex(G)) break;
    }
    // there are no more vertices that can possibly build a clique of REQUIRED_SIZE
  }

  public boolean load_vertex(Graph G) {
    Vertex vcur = G.vertices.get(cur);
    Vertex vvert;
    SearchState x = new SearchState(vcur);
    clique_potential = 1;

    for (int j = 0; j < vcur.N; ++j) {
      vvert = G.vertices.get(vcur.neibs.get(j));
      if (vvert.mcs < vcur.mcs) {
        x.cand.set(j);
        clique_potential++;
      }
    }

    if (clique_potential < REQUIRED_SIZE) return false;

    states.push(x);
    this.res = new BitSet(vcur.N);
    res.set(vcur.spos);
    clique_size = 1;

    return true;
  }
}
