/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import java.util.function.Function;

@FunctionalInterface
public interface DigestRanking {

  DigestRanking WEIGHT_RANKING = (vertexId, weight, score) -> weight;
  DigestRanking SCORE_RANKING = (vertexId, weight, score) -> score;
  Function<Double, DigestRanking> COMBINED_RANKING
          = weightExp -> (vertexId, weight, score) -> Math.pow(weight, weightExp) * score;

  double compute(int vertexId, double weight, double score);

}
