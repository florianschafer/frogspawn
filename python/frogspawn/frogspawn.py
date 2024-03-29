#  Copyright (c) Florian Schaefer 2021.
#  SPDX-License-Identifier: Apache-2.0

import os
from collections.abc import Iterator, Generator
from typing import Union, Tuple, Any

import jpype
import jpype.imports

from .cluster import Cluster


def cluster(edges: Union[Iterator[Tuple[Any, Any, float]], Generator[Tuple[Any, Any, float]]],
            min_affiliation: float = None,
            min_cluster_size: int = None,
            relocate_similarity: float = None,
            merge_similarity: float = None,
            # singleton_node: str = None,
            flatten: bool = None,
            # ranking: str = None,
            jvm_size='1G'):

    # Launch the JVM
    if not jpype.isJVMStarted():
        jpype.startJVM(jpype.getDefaultJVMPath(), f'-Xmx{jvm_size}')
        jpype.addClassPath(f'{os.path.join(os.path.dirname(os.path.realpath(__file__)))}/jars/*')

    # Create packages
    from net.adeptropolis.frogspawn.graphs.labeled import LabeledGraphBuilder
    from net.adeptropolis.frogspawn.graphs.labeled.labelings import DefaultLabeling
    from net.adeptropolis.frogspawn import ClusteringSettings
    from net.adeptropolis.frogspawn.clustering import SimpleClustering

    # Create settings
    settings_builder = ClusteringSettings.builder()
    if min_affiliation is not None:
        settings_builder.minAffiliation(min_affiliation)
    if min_cluster_size is not None:
        settings_builder.minClusterSize(min_cluster_size)
    if relocate_similarity is not None:
        settings_builder.minParentSimilarity(relocate_similarity)
    if merge_similarity is not None:
        settings_builder.maxParentSimilarity(merge_similarity)
    if flatten is not None:
        settings_builder.flatten(flatten)
    settings = settings_builder.build()

    # Create graph:
    builder = LabeledGraphBuilder(DefaultLabeling())
    for left, right, weight in edges:
        builder.add(left, right, weight)
    graph = builder.build()

    # Cluster
    root = SimpleClustering.cluster(graph, settings)

    # Wrap and return results
    return Cluster(root)
