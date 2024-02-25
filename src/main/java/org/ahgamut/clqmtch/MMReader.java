package org.ahgamut.clqmtch;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class MMReader {
  int num_vertices;
  int num_edges;
  ArrayList<EdgeValue> edges;

  MMReader() {
    num_edges = 0;
    num_vertices = 0;
    edges = new ArrayList<>();
  }

  private void readBufferedReader(BufferedReader reader) throws IOException {
    int first, second;

    String line;
    String[] values;
    // skip comments
    while ((line = reader.readLine()) != null) {
      if (line.charAt(0) == '%' || line.charAt(0) == '#') {
        continue;
      }
      break;
    }
    // read header
    values = line.split(" ");
    if (values.length != 3) {
      throw new IOException("could not read vertices!");
    }
    num_vertices = Integer.parseInt(values[0], 10);
    num_edges = Integer.parseInt(values[2], 10);
    for (int i = 0; i <= num_vertices; i++) {
      edges.add(new EdgeValue(i, i));
    }

    while ((line = reader.readLine()) != null) {
      if (line.charAt(0) == '%' || line.charAt(0) == '#') {
        continue;
      }
      values = line.split(" ");
      first = Integer.parseInt(values[0]);
      second = Integer.parseInt(values[1]);
      edges.add(new EdgeValue(first, second));
      edges.add(new EdgeValue(second, first));
    }
    Collections.sort(edges);
  }

  void readFile(String fileName) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
      readBufferedReader(reader);
    } catch (IOException e) {
      System.out.printf("Unable to read file: %s\n", e);
    }
  }

  void readString(String data) {
    try {
      BufferedReader reader = new BufferedReader(new StringReader(data));
      readBufferedReader(reader);
    } catch (IOException e) {
      System.out.printf("Unable to read string: %s\n", e);
    }
  }
}
