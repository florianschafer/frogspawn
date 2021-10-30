from __future__ import annotations
from typing import Iterator, List


class ClusterMember:

    def __init__(self, member):
        self.label = member.getLabel()
        self.weight = round(member.getWeight(), 5)
        self.affiliationScore = round(member.getAffiliationScore(), 5)

    def __str__(self):
        return f'{self.label}:{self.affiliationScore}:{self.weight}'


class Cluster:

    def __init__(self, cluster):
        self._cluster = cluster
        self.children = [Cluster(child) for child in self._cluster.getChildren()]

    def members(self) -> Iterator[ClusterMember]:
        return (ClusterMember(member) for member in self._cluster.getMembers())

    def __str__(self):
        members = ', '.join((str(member) for member in self.members()))
        children = ', '.join(str(child) for child in self.children)
        return f'Members: [{members}], children: [{children}]'