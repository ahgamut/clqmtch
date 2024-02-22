package org.ahgamut.clqmtch;

import java.util.ArrayList;
import java.util.BitSet;

public class Vertex {
  int N;
  int spos;
  int mcs;
  ArrayList<Integer> neibs;
  BitSet bits;

  Vertex(int N) {
    this.N = N;
    this.spos = 0;
    this.mcs = 1;
    this.neibs = new ArrayList<>(N);
    this.bits = new BitSet(N);
  }

  Vertex(ArrayList<Integer> neibs) {
    this.neibs = neibs;
    this.N = neibs.size();
    this.spos = 0;
    this.mcs = 1;
    this.bits = new BitSet(this.N);
  }

  public void disp() {
    if (this.N <= 1 || this.mcs <= 1) return;
    System.out.print(this.neibs.toString());
    System.out.printf("current clique: %s%n", this.bits.toString());
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
