/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.base.toposort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A topological sort with tie breaking by a comparator, ported from Fabric API.
 * The order is deterministic even in the presence of cycles.
 */
public class NodeSorting {
	public static boolean ENABLE_CYCLE_WARNING = true;

	private NodeSorting() { }

	public static <N extends SortableNode<N>> boolean sort(List<N> sortedNodes, String elementDescription,
			Comparator<N> comparator) {
		List<N> toposort = new ArrayList<>(sortedNodes.size());

		for (N node : sortedNodes) {
			forwardVisit(node, toposort);
		}

		clearStatus(toposort);
		Collections.reverse(toposort);

		Map<N, NodeScc<N>> nodeToScc = new IdentityHashMap<>();

		for (N node : toposort) {
			if (!node.visited) {
				List<N> sccNodes = new ArrayList<>();
				backwardVisit(node, sccNodes);
				sccNodes.sort(comparator);
				NodeScc<N> scc = new NodeScc<>(sccNodes);

				for (N nodeInScc : sccNodes) {
					nodeToScc.put(nodeInScc, scc);
				}
			}
		}

		clearStatus(toposort);

		for (NodeScc<N> scc : nodeToScc.values()) {
			for (N node : scc.nodes) {
				for (N subsequentNode : node.subsequentNodes) {
					NodeScc<N> subsequentScc = nodeToScc.get(subsequentNode);

					if (subsequentScc != scc) {
						scc.subsequentSccs.add(subsequentScc);
						subsequentScc.inDegree++;
					}
				}
			}
		}

		PriorityQueue<NodeScc<N>> pq = new PriorityQueue<>(
				Comparator.comparing(scc -> scc.nodes.get(0), comparator));
		sortedNodes.clear();

		for (NodeScc<N> scc : nodeToScc.values()) {
			if (scc.inDegree == 0) {
				pq.add(scc);
				scc.inDegree = -1;
			}
		}

		boolean noCycle = true;

		while (!pq.isEmpty()) {
			NodeScc<N> scc = pq.poll();
			sortedNodes.addAll(scc.nodes);

			if (scc.nodes.size() > 1) {
				noCycle = false;

				if (ENABLE_CYCLE_WARNING) {
					StringBuilder builder = new StringBuilder();
					builder.append("Found cycle while sorting ").append(elementDescription).append(":\n");

					for (N node : scc.nodes) {
						builder.append("\t").append(node.getDescription()).append("\n");
					}

					System.err.print("[ferrofabric] " + builder);
				}
			}

			for (NodeScc<N> subsequentScc : scc.subsequentSccs) {
				subsequentScc.inDegree--;

				if (subsequentScc.inDegree == 0) {
					pq.add(subsequentScc);
				}
			}
		}

		return noCycle;
	}

	private static <N extends SortableNode<N>> void forwardVisit(N node, List<N> toposort) {
		if (!node.visited) {
			node.visited = true;

			for (N data : node.subsequentNodes) {
				forwardVisit(data, toposort);
			}

			toposort.add(node);
		}
	}

	private static <N extends SortableNode<N>> void clearStatus(List<N> nodes) {
		for (N node : nodes) {
			node.visited = false;
		}
	}

	private static <N extends SortableNode<N>> void backwardVisit(N node, List<N> sccNodes) {
		if (!node.visited) {
			node.visited = true;
			sccNodes.add(node);

			for (N data : node.previousNodes) {
				backwardVisit(data, sccNodes);
			}
		}
	}

	private static class NodeScc<N extends SortableNode<N>> {
		final List<N> nodes;
		final List<NodeScc<N>> subsequentSccs = new ArrayList<>();
		int inDegree = 0;

		private NodeScc(List<N> nodes) {
			this.nodes = nodes;
		}
	}
}