package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.ConnectedComponents;
import net.adeptropolis.nephila.graphs.algorithms.SpectralBisector;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.PowerIteration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.PriorityQueue;

public class RecursiveClustering {

  private static final Logger LOG = LoggerFactory.getLogger(RecursiveClustering.class.getSimpleName());

  private final Graph graph;
  private final ClusteringSettings settings;
  private final SpectralBisector bisector;
  private final PriorityQueue<Task> queue;

  public RecursiveClustering(Graph graph, ClusteringSettings settings) {
    this.graph = graph;
    this.settings = settings;
    this.bisector = new SpectralBisector(settings.getConvergenceCriterion());
    this.queue = new PriorityQueue<>(Comparator.comparingInt(task -> task.getGraph().size()));
  }

  public Cluster run() {
    Cluster root = new Cluster(null);
    Task initialTask = new Task(graph, Task.GraphType.ROOT, root);
    queue.add(initialTask);
    processQueue();
    return root;
  }

  private void processQueue() {
    while (!queue.isEmpty()) {
      // TODO: Account for pre-recursion structure somewhere here
      Task task = queue.poll();
      if (task.getGraphType() == Task.GraphType.COMPONENT) {
        bisect(task);
      } else {
        decomposeComponents(task);
      }
    }
  }

  /**
   * Bisect the task's graph such that the normalized cut is minimized. Possible scenarios for each partition:
   * <ol>
   *   <li>The partition is either smaller than the allowed minimum cluster size or comprises the full
   *   input graph (which hits an error) -> Add its vertices to the cluster's remainder and terminate</li>
   *   <li>
   *     Ensure consistency of the resulting subgraph vertices and add all non-compliant vertices to the cluster reminder.
   *     For the remaining subgraph, one of two conditions may apply:
   *     <ol>
   *       <li>The remaining consistent subgraph is smaller than the allowed minimum cluster
   *       size -> Add its vertices to the cluster's remainder and terminate</li>
   *       <li>Else: Create a new task with graph type <code>SPECTRAL</code> and add it to the queue.</li>
   *     </ol>
   *   </li>
   * </ol>
   *
   * @param task A recursion task
   */

  private void bisect(Task task) {
    try {
      bisector.bisect(task.getGraph(), settings.getMaxIterations(), partition -> {
        if (partition.size() < settings.getMinClusterSize() || partition.size() == task.getGraph().size()) {
          task.getCluster().addToRemainder(partition);
        } else {
//          View consistentSubgraph = ensureConsistency(branch, partition);
//          if (consistentSubgraph.size() < minPartitionSize) {
//            branch.cluster.addToRemainder(consistentSubgraph);
//          } else {
//            enqueueTask(Task.GraphType.SPECTRAL, task.getCluster(), consistentSubgraph);
//          }
        }
      });
    } catch (PowerIteration.MaxIterationsExceededException e) {
      LOG.warn("Exceeded maximum number of iterations ({}). Not clustering any further.", settings.getMaxIterations());
    }
  }

  /**
   * Decompose the current task's graph into its connected components. There are 3 possible scenarios:
   * <ol>
   *   <li>The input graph was already fully connected -> label the task's graph as connected component and re-add it to the queue</li>
   *   <li>The connected component is smaller than the minimum cluster size -> Add its vertices to the cluster's reminder and terminate</li>
   *   <li>Else -> Label the component as connected and add a proper new task to the queue</li>
   * </ol>
   *
   * @param task A recursion task
   */

  private void decomposeComponents(Task task) {
    ConnectedComponents.find(task.getGraph(), component -> {
      if (component.size() == task.getGraph().size()) {
        task.setGraphType(Task.GraphType.COMPONENT);
        queue.add(task);
      } else if (component.size() < settings.getMinClusterSize()) {
        task.getCluster().addToRemainder(component);
      } else {
        enqueueTask(Task.GraphType.COMPONENT, task.getCluster(), component);
      }
    });
  }

  private void enqueueTask(Task.GraphType graphType, Cluster parent, Graph subgraph) {
    Cluster childCluster = new Cluster(parent);
    Task task = new Task(subgraph, graphType, childCluster);
    // TODO: Add post recursion structure here!
    queue.add(task);
  }

}
