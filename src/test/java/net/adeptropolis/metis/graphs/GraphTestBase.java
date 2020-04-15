/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

import com.google.common.collect.Lists;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import net.adeptropolis.metis.graphs.traversal.EdgeConsumer;
import net.adeptropolis.metis.graphs.traversal.ParallelEdgeOps;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GraphTestBase {

  /*
  17866   13573   14616   14119   11685   11997   11950   11850   12138   15711
  13573   15921   13928   10683   10537   13017   13103   11201   12481   13251
  14616   13928   15355   11810   11343   12353   14209   10488   12119   14730
  14119   10683   11810   15171   12122    8889    9614   10414    9832   13177
  11685   10537   11343   12122   11751    8678   10064    9407    9538   11418
  11997   13017   12353    8889    8678   12179   12038    9032   10501   12332
  11950   13103   14209    9614   10064   12038   15627   10313   11600   14138
  11850   11201   10488   10414    9407    9032   10313   11011    9946   11200
  12138   12481   12119    9832    9538   10501   11600    9946   11297   12258
  15711   13251   14730   13177   11418   12332   14138   11200   12258   16689
*/
  protected static final CompressedSparseGraph SOME_10_GRAPH = new CompressedSparseGraphBuilder(0)
          .add(0, 0, 17866).add(0, 1, 13573).add(0, 2, 14616).add(0, 3, 14119)
          .add(0, 4, 11685).add(0, 5, 11997).add(0, 6, 11950).add(0, 7, 11850)
          .add(0, 8, 12138).add(0, 9, 15711)
          .add(1, 1, 15921).add(1, 2, 13928).add(1, 3, 10683).add(1, 4, 10537)
          .add(1, 5, 13017).add(1, 6, 13103).add(1, 7, 11201).add(1, 8, 12481)
          .add(1, 9, 13251)
          .add(2, 2, 15355).add(2, 3, 11810).add(2, 4, 11343).add(2, 5, 12353)
          .add(2, 6, 14209).add(2, 7, 10488).add(2, 8, 12119).add(2, 9, 14730)
          .add(3, 3, 15171).add(3, 4, 12122).add(3, 5, 8889).add(3, 6, 9614)
          .add(3, 7, 10414).add(3, 8, 9832).add(3, 9, 13177)
          .add(4, 4, 11751).add(4, 5, 8678).add(4, 6, 10064).add(4, 7, 9407)
          .add(4, 8, 9538).add(4, 9, 11418)
          .add(5, 5, 12179).add(5, 6, 12038).add(5, 7, 9032).add(5, 8, 10501)
          .add(5, 9, 12332)
          .add(6, 6, 15627).add(6, 7, 10313).add(6, 8, 11600).add(6, 9, 14138)
          .add(7, 7, 11011).add(7, 8, 9946).add(7, 9, 11200)
          .add(8, 8, 11297).add(8, 9, 12258)
          .add(9, 9, 16689)
          .build();

  protected static final CompressedSparseGraph WEIGHTED_K20 = new CompressedSparseGraphBuilder(0)
          .add(0, 1, 1.369954183236944)
          .add(0, 2, 0.88293973597774766)
          .add(0, 3, 1.2891515898654977)
          .add(0, 4, 1.1232067844481632)
          .add(0, 5, 0.99379079835266615)
          .add(0, 6, 0.676404656470456)
          .add(0, 7, 0.78504753086149348)
          .add(0, 8, 0.41192045310811221)
          .add(0, 9, 0.52473281908539915)
          .add(0, 10, 1.1304140583522304)
          .add(0, 11, 1.6611105076227122)
          .add(0, 12, 1.2779880089897053)
          .add(0, 13, 1.458319270918023)
          .add(0, 14, 1.5927957292445569)
          .add(0, 15, 1.0824455101401016)
          .add(0, 16, 0.1415971797521359)
          .add(0, 17, 1.2341349863107429)
          .add(0, 18, 1.5871692725093045)
          .add(0, 19, 0.16593922043613019)
          .add(1, 2, 0.95366478488544359)
          .add(1, 3, 0.31678129748508432)
          .add(1, 4, 1.0619184705285554)
          .add(1, 5, 1.8480796489438482)
          .add(1, 6, 0.25693950982499936)
          .add(1, 7, 1.7729612103236341)
          .add(1, 8, 1.1830780316340541)
          .add(1, 9, 1.3284888153077663)
          .add(1, 10, 1.5581117561667122)
          .add(1, 11, 1.0690154608712754)
          .add(1, 12, 0.9095962625695071)
          .add(1, 13, 0.069347897795637853)
          .add(1, 14, 1.4311814453684839)
          .add(1, 15, 1.5742914395400938)
          .add(1, 16, 1.0560496715411642)
          .add(1, 17, 0.44404096131906767)
          .add(1, 18, 0.86872930772732837)
          .add(1, 19, 1.1292102428590578)
          .add(2, 3, 0.90751603139751791)
          .add(2, 4, 0.94601102734029419)
          .add(2, 5, 0.74157344952606152)
          .add(2, 6, 0.53853328618966279)
          .add(2, 7, 0.9998625410796711)
          .add(2, 8, 1.3363623780895542)
          .add(2, 9, 0.73166662553639861)
          .add(2, 10, 0.49848893900544522)
          .add(2, 11, 1.5991322340721608)
          .add(2, 12, 1.0561822249002719)
          .add(2, 13, 0.59552180888586714)
          .add(2, 14, 1.8975347615693865)
          .add(2, 15, 0.73937403069119334)
          .add(2, 16, 0.73187348267139529)
          .add(2, 17, 1.6436276370590948)
          .add(2, 18, 0.58912346175613739)
          .add(2, 19, 0.81479314120467072)
          .add(3, 4, 0.83028719976973353)
          .add(3, 5, 1.3986914200046856)
          .add(3, 6, 1.3610946745469454)
          .add(3, 7, 1.004122640608329)
          .add(3, 8, 1.2931444198159421)
          .add(3, 9, 0.20420562622128546)
          .add(3, 10, 0.86322609612118439)
          .add(3, 11, 1.4611455666731308)
          .add(3, 12, 1.4988282477225219)
          .add(3, 13, 1.1749618046852475)
          .add(3, 14, 0.5175245216558928)
          .add(3, 15, 1.8572683761731485)
          .add(3, 16, 0.65523647033580878)
          .add(3, 17, 0.88410725562365777)
          .add(3, 18, 0.87568896774717297)
          .add(3, 19, 1.3183924335964725)
          .add(4, 5, 1.2538212754037934)
          .add(4, 6, 0.93569734890414868)
          .add(4, 7, 1.8727097590476367)
          .add(4, 8, 1.2645939877321721)
          .add(4, 9, 1.080110593130726)
          .add(4, 10, 0.9988644839269456)
          .add(4, 11, 0.3851054877149811)
          .add(4, 12, 0.47257788080878793)
          .add(4, 13, 1.4398111357346695)
          .add(4, 14, 0.79104442777437867)
          .add(4, 15, 0.81412233863683592)
          .add(4, 16, 1.4951312750080437)
          .add(4, 17, 1.0393195647062226)
          .add(4, 18, 0.77561755277206867)
          .add(4, 19, 1.48925383703551)
          .add(5, 6, 1.2206416428046016)
          .add(5, 7, 1.160397867101145)
          .add(5, 8, 1.6805311325595889)
          .add(5, 9, 0.84444492696171314)
          .add(5, 10, 0.93899094493234125)
          .add(5, 11, 0.99937391035521661)
          .add(5, 12, 1.5568080105526272)
          .add(5, 13, 1.5768360006955935)
          .add(5, 14, 0.57663680049138921)
          .add(5, 15, 1.2564120199959168)
          .add(5, 16, 1.4621964199733557)
          .add(5, 17, 1.5890592980259102)
          .add(5, 18, 0.30005328806973663)
          .add(5, 19, 1.0554046584107848)
          .add(6, 7, 1.2078058189701339)
          .add(6, 8, 1.8952242540391939)
          .add(6, 9, 1.074326645706607)
          .add(6, 10, 1.4092500090452582)
          .add(6, 11, 0.90370869624951045)
          .add(6, 12, 1.0088261813435393)
          .add(6, 13, 0.24090508457189641)
          .add(6, 14, 1.2444490864791682)
          .add(6, 15, 0.59913332788527973)
          .add(6, 16, 0.81653827411610846)
          .add(6, 17, 0.73115608418287226)
          .add(6, 18, 0.24827365735975282)
          .add(6, 19, 0.2322484990116509)
          .add(7, 8, 1.2379873879143726)
          .add(7, 9, 0.86253256851465743)
          .add(7, 10, 0.67220233887104186)
          .add(7, 11, 1.5569538087581218)
          .add(7, 12, 0.87460392172887969)
          .add(7, 13, 1.5734066288671138)
          .add(7, 14, 0.43730538646554173)
          .add(7, 15, 1.3647579180080287)
          .add(7, 16, 1.1487916417976085)
          .add(7, 17, 1.0018238905232197)
          .add(7, 18, 1.3810029039546843)
          .add(7, 19, 0.77197982936234832)
          .add(8, 9, 0.71694341253323945)
          .add(8, 10, 1.4748545255322847)
          .add(8, 11, 0.8382002989831423)
          .add(8, 12, 1.3984559336316775)
          .add(8, 13, 1.5406722164449762)
          .add(8, 14, 1.0578792624173767)
          .add(8, 15, 0.44261348112144849)
          .add(8, 16, 1.482746886459819)
          .add(8, 17, 1.4737652090907338)
          .add(8, 18, 0.72667905380927311)
          .add(8, 19, 1.4127543784911305)
          .add(9, 10, 1.1311388443284236)
          .add(9, 11, 0.38954645809943433)
          .add(9, 12, 1.1036250508507854)
          .add(9, 13, 1.3132359443864741)
          .add(9, 14, 1.1396359394344207)
          .add(9, 15, 1.2297217254933965)
          .add(9, 16, 1.5400268746349406)
          .add(9, 17, 1.0154301048364025)
          .add(9, 18, 1.1575471775641994)
          .add(9, 19, 1.3632499562702673)
          .add(10, 11, 1.1077211111168905)
          .add(10, 12, 0.80724811033053201)
          .add(10, 13, 1.0204557728773949)
          .add(10, 14, 0.78503961012106482)
          .add(10, 15, 1.5865968618898729)
          .add(10, 16, 1.5238900574531082)
          .add(10, 17, 0.34611501769077546)
          .add(10, 18, 0.744036522328403)
          .add(10, 19, 1.8008544601474545)
          .add(11, 12, 1.1457056887280845)
          .add(11, 13, 0.74793222262529946)
          .add(11, 14, 1.0421206031944688)
          .add(11, 15, 0.76613089915430033)
          .add(11, 16, 0.79182632781956752)
          .add(11, 17, 0.19852614137299215)
          .add(11, 18, 1.0361141459483654)
          .add(11, 19, 1.0641527492726186)
          .add(12, 13, 1.979531981990982)
          .add(12, 14, 0.95335648603887879)
          .add(12, 15, 0.80204001875596242)
          .add(12, 16, 0.57293607128603452)
          .add(12, 17, 0.71014173039386153)
          .add(12, 18, 0.55503739976078326)
          .add(12, 19, 0.918738391155969)
          .add(13, 14, 1.3831319130607647)
          .add(13, 15, 1.1446523880390207)
          .add(13, 16, 1.2741918674250314)
          .add(13, 17, 1.2846718444176899)
          .add(13, 18, 1.0826314354809514)
          .add(13, 19, 1.0000553993570414)
          .add(14, 15, 0.72315870021439188)
          .add(14, 16, 0.93752820530685455)
          .add(14, 17, 1.630309252974635)
          .add(14, 18, 1.9513349391435215)
          .add(14, 19, 1.1426109259424335)
          .add(15, 16, 1.466270744112548)
          .add(15, 17, 1.0894992873599925)
          .add(15, 18, 0.85240028962282111)
          .add(15, 19, 1.7636681847721396)
          .add(16, 17, 1.0730274742013524)
          .add(16, 18, 1.0049093999555212)
          .add(16, 19, 1.6547662443834397)
          .add(17, 18, 0.84712526947036659)
          .add(17, 19, 0.67701273593159639)
          .add(18, 19, 0.96629282147179674)
          .build();

  protected final Graph EIGEN_REF_GRAPH = new CompressedSparseGraphBuilder(0)
          .add(0, 1, 1)
          .add(0, 4, 1)
          .add(1, 2, 1)
          .add(1, 3, 1)
          .add(1, 4, 1)
          .add(2, 5, 1)
          .add(3, 4, 1)
          .add(4, 5, 1)
          .build();

  protected final Graph SOME_BIPARTITE_GRAPH = new CompressedSparseGraphBuilder(0)
          .add(0, 1, 2)
          .add(0, 3, 3)
          .add(0, 4, 11)
          .add(1, 2, 5)
          .add(2, 3, 7)
          .add(2, 5, 13)
          .build();

  protected final Graph K43 = new CompressedSparseGraphBuilder(0)
          .add(0, 1, 5)
          .add(0, 3, 11)
          .add(0, 5, 17)
          .add(1, 2, 23)
          .add(1, 4, 31)
          .add(1, 6, 41)
          .add(2, 3, 43)
          .add(2, 5, 53)
          .add(3, 4, 61)
          .add(3, 6, 71)
          .add(4, 5, 73)
          .add(5, 6, 83)
          .build();

  protected final Graph K12 = new CompressedSparseGraphBuilder(0)
          .add(0, 1, 2)
          .add(0, 2, 3)
          .build();

  protected CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();
  private FingerprintingEdgeConsumer fingerprintingConsumer = new FingerprintingEdgeConsumer();

  protected static Graph completeGraph(int size) {
    CompressedSparseGraphBuilder b = new CompressedSparseGraphBuilder(0);
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) {
        b.add(i, j, 1);
      }
    }
    return b.build();
  }

  @Before
  public void init() {
    consumer.reset();
    fingerprintingConsumer.reset();
  }

  protected Graph bandedGraph(int n, int k) {
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder(0);
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < Math.min(i + k, n); j++) {
        builder.add(i, j, 2 * i + 3 * j);
      }
    }
    return builder.build();
  }

  protected Graph butterflyGraph() {
    return new CompressedSparseGraphBuilder(0)
            .add(0, 1, 1)
            .add(0, 2, 1)
            .add(1, 2, 1)
            .add(2, 3, 1)
            .add(0, 4, 1)
            .add(0, 5, 1)
            .add(4, 5, 1)
            .add(5, 6, 1)
            .build();
  }

  protected Graph completeBipartiteWithWeakLink() {
    return new CompressedSparseGraphBuilder(0)
            .add(0, 1, 1)
            .add(0, 2, 1)
            .add(0, 3, 1)
            .add(4, 1, 1)
            .add(4, 2, 1)
            .add(4, 3, 1)
            .add(5, 3, 0.5)
            .add(5, 6, 1)
            .add(5, 8, 1)
            .add(7, 6, 1)
            .add(7, 8, 1)
            .build();
  }

  protected Graph triangle() {
    return new CompressedSparseGraphBuilder(0)
            .add(0, 1, 1)
            .add(1, 2, 1)
            .add(2, 0, 1)
            .build();
  }

  protected Graph largeCircle() {
    return largeCircle(100000);
  }

  protected Graph largeCircle(int size) {
    CompressedSparseGraphBuilder b = new CompressedSparseGraphBuilder(0);
    for (int i = 0; i < size; i++) {
      b.add(i, i + 1, 1);
    }
    b.add(size - 1, size, 1);
    return b.build();
  }

  protected long bandedGraphFingerprint(int n, int k) {
    long fp = 0;
    for (long i = 0; i < n; i++) {
      for (long j = i + 1; j < Math.min(i + k, n); j++) {
        long weight = 2 * i + 3 * j;
        fp += ((i * weight) % (j + 10));
        fp += ((j * weight) % (i + 10));
      }
    }
    return fp;
  }

  protected long traverseFingerprint(Graph graph) {
    ParallelEdgeOps.traverse(graph, fingerprintingConsumer);
    return fingerprintingConsumer.getFingerprint();
  }

  protected static class SubgraphCollectingConsumer implements Consumer<Graph> {

    private final List<Graph> graphs;

    public SubgraphCollectingConsumer() {
      graphs = Lists.newArrayList();
    }

    public List<List<Integer>> vertices() {
      return graphs.stream().sorted(Comparator.comparingInt(Graph::order).thenComparingInt(x -> {
        VertexIterator vertices = x.vertexIterator();
        vertices.hasNext();
        return vertices.globalId();
      })).map(graph -> {
        List<Integer> vertices = Lists.newArrayList();
        VertexIterator iterator = graph.vertexIterator();
        while (iterator.hasNext()) {
          vertices.add(iterator.globalId());
        }
        vertices.sort(Comparator.comparingInt(x -> x));
        return vertices;
      }).sorted(Comparator.comparingInt(p -> p.get(0))).collect(Collectors.toList());
    }

    @Override
    public void accept(Graph graph) {
      graphs.add(graph);
    }
  }

  public static class CollectingEdgeConsumer implements EdgeConsumer {

    private final List<Edge> edges = new ArrayList<>();

    @Override
    public void accept(int u, int v, double weight) {
      synchronized (edges) {
        edges.add(Edge.of(u, v, weight));
      }
    }

    void reset() {
      edges.clear();
    }

    public List<Edge> getEdges() {
      return edges;
    }

  }

  protected static class FingerprintingEdgeConsumer implements EdgeConsumer {

    private final AtomicLong fingerprint;

    public FingerprintingEdgeConsumer() {
      fingerprint = new AtomicLong();
    }

    @Override
    public void accept(int u, int v, double weight) {
      fingerprint.addAndGet((long) ((u * weight) % (v + 10)));
    }

    void reset() {
      fingerprint.set(0);
    }

    public long getFingerprint() {
      return fingerprint.get();
    }

  }

}
