package net.adeptropolis.nephila.graph.implementations;

public interface EntryVisitor {

  void visit(int rowIdx, int colIdx, double value);

  void reset();

}
