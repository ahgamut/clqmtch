package org.ahgamut.clqmtch;

import java.util.BitSet;

public class SearchState {
  int id;
  int start_at;
  BitSet cand;

  SearchState(Vertex ver) {
    this.id = ver.spos;
    this.start_at = 0;
    this.cand = new BitSet(ver.N);
  }

  SearchState(SearchState other) {
    this.id = other.id;
    this.cand = (BitSet) other.cand.clone();
    this.start_at = 0;
  }

  public String toString() {
    return String.format("(id=%d, start=%d, cand=%s)", id, start_at, cand.toString());
  }
}
