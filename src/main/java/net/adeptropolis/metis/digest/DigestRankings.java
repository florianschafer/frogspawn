/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import java.util.function.Function;

public class DigestRankings {

  public static final DigestRanking WEIGHT_RANKING = (vertexId, weight, score) -> weight;
  public static final DigestRanking SCORE_RANKING = (vertexId, weight, score) -> score;
  public static final Function<Double, DigestRanking> COMBINED_RANKING
          = weightExp -> (vertexId, weight, score) -> Math.pow(weight, weightExp) * score;
  public static final DigestRanking DEFAULT_COMBINED_RANKING = new DefaultCombinedRanking();

  /**
   * Default constructor
   */

  private DigestRankings() {

  }

  private static class DefaultCombinedRanking implements DigestRanking {

    @Override
    public double compute(int vertexId, double weight, double score) {
      return Math.pow(weight, 1.5) * score;
    }
  }

}
