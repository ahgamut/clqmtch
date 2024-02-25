package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.BitSet;

public class Vertex {
  int N;
  int spos;
  int mcs;
  ArrayList<Integer> neibs;
  BitSet bits;

  Vertex() {
    this.N = 0;
    this.spos = 0;
    this.mcs = 0;
    this.neibs = new ArrayList<>();
    this.bits = null;
  }

  Vertex(int N) {
    this.N = N;
    this.spos = 0;
    this.mcs = 0;
    this.neibs = new ArrayList<>(N);
    this.bits = new BitSet(N);
  }

  Vertex(ArrayList<Integer> neibs) {
    this.neibs = (ArrayList<Integer>) neibs.clone();
    this.N = neibs.size();
    this.spos = 0;
    this.mcs = 0;
    this.bits = new BitSet(this.N);
  }

  public void disp() {
    System.out.printf(
        "%d %s :: current clique: %s%n",
        this.neibs.get(this.spos), this.neibs, this.give_clique().toString());
  }

  public void clique_disp() {
    for (int i = 0; i < this.N; ++i) {
      if (this.bits.get(i)) System.out.printf("%d ", this.neibs.get(i));
    }
    System.out.println();
  }

  public ArrayList<Integer> give_clique() {
    ArrayList<Integer> res = new ArrayList<>(this.mcs);
    for (int i = 0; i < this.N; ++i) {
      if (this.bits.get(i)) res.add(this.neibs.get(i));
    }
    return res;
  }
}
