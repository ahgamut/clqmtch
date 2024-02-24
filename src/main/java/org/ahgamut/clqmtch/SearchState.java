package org.ahgamut.clqmtch;

import java.util.BitSet;

public class SearchState {
  int id;
  int start_at;
  BitSet res;
  BitSet cand;

  SearchState(Vertex ver) {
    this.id = ver.spos;
    this.start_at = 0;
    this.res = new BitSet(ver.N);
    this.res.set(ver.spos);
    this.cand = new BitSet(ver.N);
  }

  SearchState(SearchState other) {
    this.id = other.id;
    this.res = (BitSet) other.res.clone();
    this.cand = (BitSet) other.cand.clone();
    this.start_at = this.cand.nextSetBit(0);
  }
}
