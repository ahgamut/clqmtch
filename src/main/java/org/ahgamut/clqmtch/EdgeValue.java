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
        else if(this.first < other.first) return -1;
        else {
            if (this.second > other.second) return 1;
            else if(this.second < other.second) return -1;
            else return 0;
        }
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }
}
