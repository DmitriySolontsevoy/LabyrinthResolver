package pathfinders.greedy;

import dto.PathLinkDTO;
import dto.ShortestPathDTO;
import graph.Graph;
import graph.Transition;
import graph.Vertex;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class LeeBasic implements BiFunction<Graph, Integer, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph, Integer optimalCost) {
        var valueLabels = new HashMap<String, PathLinkDTO>();
        valueLabels.put(graph.getEntranceVertexLabel(), new PathLinkDTO(graph.getEntranceVertexLabel(), 0));

        var unsettledValueVertices = graph.getAllVertices().stream()
                .map(Vertex::getLabel)
                .collect(Collectors.toCollection(HashSet::new));

        while (!unsettledValueVertices.isEmpty()) {
            var currentNodeLabel = unsettledValueVertices.stream()
                    .filter(valueLabels::containsKey)
                    .min(Comparator.comparingInt(x -> valueLabels.get(x).getValue()))
                    .get();

            var transitions = graph.getTransitionsByVertexLabel(currentNodeLabel);

            for (Transition transition : transitions) {
                if (unsettledValueVertices.contains(transition.getDestination())) {
                    valueLabels.put(transition.getDestination(),
                            new PathLinkDTO(currentNodeLabel, valueLabels.get(currentNodeLabel).getValue() + transition.getWeight()));
                    if (transition.getDestination().equals(graph.getExitVertexLabel())) {
                        unsettledValueVertices.clear();
                        break;
                    }
                }
            }

            unsettledValueVertices.remove(currentNodeLabel);
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
