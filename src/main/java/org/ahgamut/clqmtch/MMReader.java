package org.ahgamut.clqmtch;

import java.io.*;
import java.util.ArrayList;

public class MMReader {

  private ArrayList<EdgeValue> readBufferedReader(BufferedReader reader) throws IOException {
    ArrayList<EdgeValue> el = new ArrayList<>();
    int num_vertices, num_edges;
    int first, second;

    String line;
    String values[];
    // skip comments
    while ((line = reader.readLine()) != null) {
      if (line.charAt(0) == '%' || line.charAt(0) == '#') {
        continue;
      }
    }
    // read header
    values = line.split(" ");
    if (values.length != 3) {
      throw new IOException("could not read vertices!");
    }
    num_vertices = Integer.parseInt(values[0], 10);
    num_edges = Integer.parseInt(values[2], 10);
    for (int i = 0; i <= num_vertices; i++) {
      el.add(new EdgeValue(i, i));
    }

    while ((line = reader.readLine()) != null) {
      if (line.charAt(0) == '%' || line.charAt(0) == '#') {
        continue;
      }
      values = line.split(" ");
      first = Integer.parseInt(values[0]);
      second = Integer.parseInt(values[1]);
      el.add(new EdgeValue(first, second));
      el.add(new EdgeValue(second, first));
    }
    System.out.println("vertices: " + num_vertices);
    System.out.println("edges: " + num_edges);
    el.sort();
    return el;
  }

  ArrayList<EdgeValue> readFile(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
    return readBufferedReader(reader);
  }

  ArrayList<EdgeValue> readString(String data) throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(data));
    return readBufferedReader(reader);
  }
}
