package pathfinders.greedy;

import dto.ShortestPathDTO;
import graph.Graph;
import dto.PathLinkDTO;
import graph.Transition;

import java.util.*;
import java.util.function.Function;

public class DijkstraBasic implements Function<Graph, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph) {
        var valueLabels = new HashMap<String, PathLinkDTO>();
        valueLabels.put(graph.getEntranceVertexLabel(), new PathLinkDTO(graph.getEntranceVertexLabel(), 0));

        var settledValueVertices = new HashSet<String>();

        while (settledValueVertices.size() < graph.getAllVertices().size()) {
            var currentNodeLabel = valueLabels.entrySet().stream()
                    .filter(entry -> !settledValueVertices.contains(entry.getKey()))
                    .min((vertex, value) -> value.getValue().getValue())
                    .get()
                    .getKey();

            var transitions = graph.getTransitionsByVertexLabel(currentNodeLabel);

            for (Transition transition : transitions) {
                Integer value = Integer.MAX_VALUE;

                if (valueLabels.containsKey(transition.getDestination())) {
                    value = valueLabels.get(transition.getDestination()).getValue();
                } else {
                    valueLabels.put(
                            transition.getDestination(),
                            new PathLinkDTO(currentNodeLabel, valueLabels.get(currentNodeLabel).getValue() + transition.getWeight())
                    );
                }

                if (valueLabels.get(currentNodeLabel).getValue() + transition.getWeight() < value) {
                    if (value != Integer.MAX_VALUE) {
                        settledValueVertices.remove(transition.getDestination());
                    }

                    value = valueLabels.get(currentNodeLabel).getValue() + transition.getWeight();
                    valueLabels.put(
                            transition.getDestination(),
                            new PathLinkDTO(currentNodeLabel, value)
                    );
                }
            }

            settledValueVertices.add(currentNodeLabel);
        }

        var pathDTO = new ShortestPathDTO();
        pathDTO.setCost(valueLabels.get(graph.getExitVertexLabel()).getValue());

        var shortestPath = new ArrayList<String>();
        shortestPath.add(graph.getEntranceVertexLabel());

        var backtrackCursor = graph.getExitVertexLabel();
        while (!backtrackCursor.equals(graph.getEntranceVertexLabel())) {
            shortestPath.add(backtrackCursor);
            backtrackCursor = valueLabels.get(backtrackCursor).getLabelFrom();
        }

        pathDTO.setPath(shortestPath);

        return pathDTO;
    }
}
