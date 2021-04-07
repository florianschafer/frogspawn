/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import com.google.common.collect.Lists;
import lombok.*;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.frogspawn.graphs.similarity.NormalizedCutMetric;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class PostprocessingSettings {

    @Builder.Default
    private final AffiliationMetric affiliationMetric = new DefaultAffiliationMetric();
    @Builder.Default
    private final GraphSimilarityMetric similarityMetric = new NormalizedCutMetric();
    @Builder.Default
    private final int minClusterSize = 50;
    @Builder.Default
    private final double minAffiliation = 0.2;

    @Builder.Default
    private final double minParentSimilarity = 0.09;
    @Builder.Default
    private final double maxParentSimilarity = 0.60;
    @Builder.Default
    private final double parentSimilarityAcceptanceLimit = 0.98;
    @Builder.Default
    private final int minChildren = 0;

    @Builder.Default
    private final SingletonMode singletonMode = SingletonMode.ASSIMILATE;
    @Builder.Default
    private final List<Postprocessor> customPostprocessors = Lists.newArrayList();

}
