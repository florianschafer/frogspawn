package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.ConnectedComponents;
import net.adeptropolis.nephila.graph.implementations.RowWeights;
import net.adeptropolis.nephila.graph.implementations.SpectralBipartitioner;

public class RecursiveSpectralClustering {

  private final CSRStorage graph;

  public RecursiveSpectralClustering(CSRStorage graph) {
    this.graph = graph;
  }

  public void compute() {
    CSRStorage.View rootView = graph.defaultView();
    RowWeights rootWeights = new RowWeights(rootView);


    //    foo(graph.defaultView());
  }

//  private void foo(CSRStorage.View view) {
//
//    new ConnectedComponents(view).find(component -> {
//      if (component.size() > 1000) {
//        System.out.println("Component size: " + component.size());
//        new SpectralBipartitioner(component).partition(part -> {
//          if (part.getView().size() > 1000) {
//            System.out.println("Partition size: " + part.getView().size() + ", consistency: " + part.getConsistency());
//            foo(part.getView());
//          }
//        });
//      }
//    });
//
//
//  }




}
