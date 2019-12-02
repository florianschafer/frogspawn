package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;

public class ConsistencyPostprocessor implements Postprocessor {

  @Override
  public boolean apply(Cluster cluster) {
    // TODO
    // This is supposed to run after the ancestor similarity step

//
//    TODO / Think about it:
//    Rename all shapers to "postprocessors"
//    Do not re-ensure consistency here, but add another postprocessor stage that works its way up
//    from bottom to top
//    This should be much easier, since we can then traverse each depth level one by one

//    Cluster ptr = originalParent;
//    while (ptr.getParent() != null) {
//      Graph graph = aggregateGraph(ptr);
//      Graph survivors = consistency.ensure(ptr.getParent(), graph);
//
//
//
//    }



    return false;
  }

}
