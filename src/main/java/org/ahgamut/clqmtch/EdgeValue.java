package org.ahgamut.clqmtch;

public class EdgeValue implements Comparable<EdgeValue> {
  int first;
  int second;

  EdgeValue(int first, int second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public int compareTo(EdgeValue other) {
    if (this.first > other.first) return 1;
    else if (this.first < other.first) return -1;
    else {
      return Integer.compare(this.second, other.second);
    }
  }

  @Override
  public String toString() {
    return "(" + first + "," + second + ")";
  }
}
