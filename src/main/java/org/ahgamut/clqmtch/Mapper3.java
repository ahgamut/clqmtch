package org.ahgamut.clqmtch;

public class Mapper3 {
  static final double MIN_DIST = 1e-3;
  static final double MIN_ANGLE = 5e-3;
  static final double PI = Math.PI;
  private static double MIN_RATIO;
  private static double MAX_RATIO;
  final double MIN_RATIO_DEFAULT = 0.5;
  final double MAX_RATIO_DEFAULT = 2.5;
  final int NUM_POINTS = 384; /* technically 1024 */

  void invert_combi(int n, int i, Triple[] t, Point[] p) {
    int x = 0;
    int y = 0;
    int z = 0;
    int ii = i;
    /* (x, y, z) is the ith element in the lexicographic ordering
     * of the elements in choose(n, 3). solve for x, y, z.
     * NOTE: 0 <= i, x, y, z < n */

    /* choose(n, 2) elements will start with 0,
     * choose(n-x, 2) elements with start with x */
    for (x = 0; i >= ((n - x - 1) * (n - x - 2)) / 2; ++x) {
      i -= ((n - x - 1) * (n - x - 2)) / 2;
    }

    /* choose ((n-x)-2, 1) elements will start with x, x+1
     * choose ((n-x)-2-y,1) elements will start with x, x+y+1 */
    for (y = 0; i >= ((n - x) - 2 - y); ++y) {
      i -= ((n - x) - 2 - y);
    }

    y = (x + 1) + y;
    z = (y + 1) + i;
    t[ii].construct(x, y, z, p[x], p[y], p[z]);
  }

  Graph construct_graph(double[][] q_pts, int qlen,
                        double[][] k_pts, int klen,
                        double delta, double epsilon,
                        double min_ratio,
                        double max_ratio) {
    /* set ratios before anything */
    MIN_RATIO = min_ratio;
    MAX_RATIO = max_ratio;

    /* arrays and sizes are provided by caller  */
    if (qlen > NUM_POINTS || klen > NUM_POINTS ||
            qlen * klen > NUM_POINTS * NUM_POINTS) {
      throw new RuntimeException("too many points, might cause memory issues\n");
    }
    if (qlen < 3 || klen < 3) {
      throw new RuntimeException("too many points, might cause memory issues\n");
    }

    Point[] q = new Point[qlen];
    Point[] k = new Point[klen];
    int zz;

    for (zz = 0; zz < qlen; zz++) {
      q[zz].x = q_pts[zz][0];
      q[zz].y = q_pts[zz][1];
    }

      for (zz = 0; zz < klen; zz++) {
      k[zz].x = k_pts[zz][0];
      k[zz].y = k_pts[zz][1];
    }

      /* declare Triple arrays and sizes */
    int M = (qlen * (qlen - 1) * (qlen - 2)) / 6;
    int N = (klen * (klen - 1) * (klen - 2)) / 6;
    int valid_M = 0;
    int valid_N = 0;
    Triple []qt = new Triple[M];
    Triple []kt = new Triple[N];

    AdjMat adjmat = new AdjMat(qlen, klen);
    // default initialized to zero
    Graph res = new Graph();

    int ix, iy;
    boolean[] check = new boolean[8];

    // int i1, j1, k1;
    Coeff3 c1 = new Coeff3(0, 0,0 );
    // int i2, j2, k2;
    Coeff3 c2 = new Coeff3(0, 0,0 );


    /* fill the first set of triples */
    for (ix = 0; ix < M; ++ix) {
      invert_combi(qlen, ix, qt, q);
      valid_M += qt[ix].valid ? 1 : 0;
    }

    /* fill the second set of triples */
    for (iy = 0; iy < N; ++iy) {
      invert_combi(klen, iy, kt, k);
      valid_N += kt[iy].valid ? 1 : 0;
    }

   /* construct the correspondence graph */
    for (ix = 0; ix < M; ix++) {
      for (iy = 0; iy < N; iy++) {
        if (qt[ix].valid && kt[iy].valid) {
          /* the compare call needs to happen here */
          /* and then you write into adjmat */
          qt[ix].ret0(c1);
          qt[ix].compare(kt[iy], check, delta, epsilon);
          if (check[0]) {
            kt[iy].ret0(c2);
            adjmat.add_edge(c1, c2);
          }
          if (check[1]) {
            kt[iy].ret1(c2);
            adjmat.add_edge(c1, c2);
          }
          if (check[2]) {
            kt[iy].ret2(c2);
            adjmat.add_edge(c1, c2);
          }
          if (check[3]) {
            kt[iy].ret3(c2);
            adjmat.add_edge(c1, c2);
          }
          if (check[4]) {
            kt[iy].ret4(c2);
            adjmat.add_edge(c1, c2);
          }
          if (check[5]) {
            kt[iy].ret5(c2);
            adjmat.add_edge(c1, c2);
          }
        }
      }
    }

    System.out.printf("%d valid triangles out of %d in Q\n", valid_M, M);
    System.out.printf("%d valid triangles out of %d in K\n", valid_N, N);

    res.load_matrix(adjmat.matsize, adjmat.mat);

    /* reset ratios to default */
    MIN_RATIO = MIN_RATIO_DEFAULT;
    MAX_RATIO = MAX_RATIO_DEFAULT;

    /* send the answer back */
    return res;
  }

    private static class Triple {
    int i;
    int j;
    int k;
    boolean valid;
    boolean inited;
    double as, bs, cs;
    double at, bt, ct;

    Triple() {
      i = j = k = 0;
      valid = inited = false;
      as = bs = cs = 0.0;
      at = bt = ct = 0.0;
    }

    void construct(int i, int j, int k, Point a, Point b, Point c) {
      /* CALLER NEEDS TO ENSURE THAT ii, jj, kk are < 1024 */
      this.i = i;
      this.j = j;
      this.k = k;
      this.as = Math.hypot(c.x - b.x, c.y - b.y);
      this.at = StableAngle.calc(a.x - c.x, a.y - c.y, b.x - a.x, b.y - a.y);
      this.bs = Math.hypot(a.x - c.x, a.y - c.y);
      this.bt = StableAngle.calc(b.x - a.x, b.y - a.y, c.x - b.x, c.y - b.y);
      this.cs = Math.hypot(b.x - a.x, b.y - a.y);
      this.ct = StableAngle.calc(c.x - b.x, c.y - b.y, a.x - c.x, a.y - c.y);
      this.valid = this.get_valid();
      this.inited = true;
    }

    void compare(Triple other, boolean[] check, double delta, double epsilon) {
      check[0] = this.binary_cmp0(other, delta, epsilon);
      check[1] = this.binary_cmp1(other, delta, epsilon);
      check[2] = this.binary_cmp2(other, delta, epsilon);
      check[3] = this.binary_cmp3(other, delta, epsilon);
      check[4] = this.binary_cmp4(other, delta, epsilon);
      check[5] = this.binary_cmp5(other, delta, epsilon);
    }

    boolean get_valid() {
      return (as > MIN_DIST && bs > MIN_DIST && cs > MIN_DIST);
    }

    boolean binary_cmp0(Triple other, double delta, double epsilon) {
      // angle_compare
      boolean a = L2Metric.calc(this.at, other.at, this.bt, other.bt, this.ct, other.ct) < delta;
      // sr_compare
      double r1 = this.as / other.as;
      double r2 = this.bs / other.bs;
      double r3 = this.cs / other.cs;
      boolean b = L1Metric.calc(r1, r2, r2, r3, r3, r1) < epsilon;
      // construct ratio
      double side_ratio = (r1 + r2 + r3) / 3;

      return a && b && (side_ratio >= MIN_RATIO) && (side_ratio <= MAX_RATIO);
    }

    boolean binary_cmp1(Triple other, double delta, double epsilon) {
      // angle_compare
      boolean a = L2Metric.calc(this.at, other.at, this.bt, other.ct, this.ct, other.bt) < delta;
      // sr_compare
      double r1 = this.as / other.as;
      double r2 = this.bs / other.cs;
      double r3 = this.cs / other.bs;
      boolean b = L1Metric.calc(r1, r2, r2, r3, r3, r1) < epsilon;
      // construct ratio
      double side_ratio = (r1 + r2 + r3) / 3;

      return a && b && (side_ratio >= MIN_RATIO) && (side_ratio <= MAX_RATIO);
    }

    boolean binary_cmp2(Triple other, double delta, double epsilon) {
      // angle_compare
      boolean a = L2Metric.calc(this.at, other.bt, this.bt, other.at, this.ct, other.ct) < delta;
      // sr_compare
      double r1 = this.as / other.bs;
      double r2 = this.bs / other.as;
      double r3 = this.cs / other.cs;
      boolean b = L1Metric.calc(r1, r2, r2, r3, r3, r1) < epsilon;
      // construct ratio
      double side_ratio = (r1 + r2 + r3) / 3;

      return a && b && (side_ratio >= MIN_RATIO) && (side_ratio <= MAX_RATIO);
    }

    boolean binary_cmp3(Triple other, double delta, double epsilon) {
      // angle_compare
      boolean a = L2Metric.calc(this.at, other.bt, this.bt, other.ct, this.ct, other.at) < delta;
      // sr_compare
      double r1 = this.as / other.bs;
      double r2 = this.bs / other.cs;
      double r3 = this.cs / other.as;
      boolean b = L1Metric.calc(r1, r2, r2, r3, r3, r1) < epsilon;
      // construct ratio
      double side_ratio = (r1 + r2 + r3) / 3;

      return a && b && (side_ratio >= MIN_RATIO) && (side_ratio <= MAX_RATIO);
    }

    boolean binary_cmp4(Triple other, double delta, double epsilon) {
      // angle_compare
      boolean a = L2Metric.calc(this.at, other.ct, this.bt, other.bt, this.ct, other.at) < delta;
      // sr_compare
      double r1 = this.as / other.cs;
      double r2 = this.bs / other.bs;
      double r3 = this.cs / other.as;
      boolean b = L1Metric.calc(r1, r2, r2, r3, r3, r1) < epsilon;
      // construct ratio
      double side_ratio = (r1 + r2 + r3) / 3;

      return a && b && (side_ratio >= MIN_RATIO) && (side_ratio <= MAX_RATIO);
    }

    boolean binary_cmp5(Triple other, double delta, double epsilon) {
      // angle_compare
      boolean a = L2Metric.calc(this.at, other.ct, this.bt, other.at, this.ct, other.bt) < delta;
      // sr_compare
      double r1 = this.as / other.cs;
      double r2 = this.bs / other.as;
      double r3 = this.cs / other.bs;
      boolean b = L1Metric.calc(r1, r2, r2, r3, r3, r1) < epsilon;
      // construct ratio
      double side_ratio = (r1 + r2 + r3) / 3;

      return a && b && (side_ratio >= MIN_RATIO) && (side_ratio <= MAX_RATIO);
    }

    void ret0(Coeff3 c) {
      c.i = i;
      c.j = j;
      c.k = k;
    }

    void ret1(Coeff3 c) {
      c.i = i;
      c.j = k;
      c.k = j;
    }

    void ret2(Coeff3 c) {
      c.i = j;
      c.j = i;
      c.k = k;
    }

    void ret3(Coeff3 c) {
      c.i = j;
      c.j = k;
      c.k = i;
    }

    void ret4(Coeff3 c) {
      c.i = k;
      c.j = j;
      c.k = i;
      /* ?? */
    }

    void ret5(Coeff3 c) {
      c.i = k;
      c.j = i;
      c.k = j;
    }
  }

  private static class Coeff3 {
    int i, j, k;

    Coeff3(int i, int j, int k) {
      this.i = i;
      this.j = j;
      this.k = k;
    }
  }

  private static class Point {
    double x;
    double y;

    Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  private static class L1Metric {
    public static double calc(double a1, double a2, double b1, double b2, double c1, double c2) {
      return Math.abs(a1 - a2) + Math.abs(b1 - b2) + Math.abs(c1 - c2);
    }
  }

  static class L2Metric {
    public static double calc(double a1, double a2, double b1, double b2, double c1, double c2) {
      return Math.hypot(a1 - a2, Math.hypot(b1 - b2, c1 - c2));
    }
  }

  private static class StableAngle {
    public static double calc(double u1, double u2, double v1, double v2) {
      // https://people.eecs.berkeley.edu/~wkahan/MathH110/Cross.pdf
      // Section 13
      double mod_u = Math.hypot(u1, u2);
      double mod_v = Math.hypot(v1, v2);
      double numerator = Math.hypot(u1 * mod_v - v1 * mod_u, u2 * mod_v - v2 * mod_u);
      double denominator = Math.hypot(u1 * mod_v + v1 * mod_u, u2 * mod_v + v2 * mod_u);
      return Math.atan2(numerator, denominator);
    }
  }

  private static class AdjMat {
    int matsize;
    int qlen;
    int klen;
    char [][]mat;

    AdjMat(int qlen, int klen) {
      this.qlen = qlen;
      this.klen = klen;
      int n = qlen * klen;
      this.matsize = n;
      this.mat = new char[n][n];
    }

    void add_edge(int i1, int i2, int j1, int j2) {
      mat[i1 * klen + i2][j1 * klen + j2] = 1;
    }

    void add_edge(Coeff3 c1, Coeff3 c2) {
      this.add_edge(c1.i, c2.i, c1.j, c2.j);
      this.add_edge(c1.j, c2.j, c1.k, c2.k);
      this.add_edge(c1.i, c2.i, c1.k, c2.k);
    }
  }
}
