package pathfinders.greedy;

import dto.HeuristicNodeDTO;
import dto.ShortestPathDTO;
import graph.Graph;
import graph.Transition;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AStarBasic implements BiFunction<Graph, Integer, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph, Integer optimalCost) {
        var valueLabels = new HashMap<String, HeuristicNodeDTO>();
        var openSet = new HashSet<String>();

        openSet.add(graph.getEntranceVertexLabel());
        valueLabels.put(graph.getEntranceVertexLabel(), new HeuristicNodeDTO(graph.getEntranceVertexLabel(), 0, 0));

        var closedSet = new HashSet<String>();

        while (!openSet.isEmpty()) {
            var currentNodeLabel = openSet.stream()
                    .min(Comparator.comparingInt(x -> valueLabels.get(x).getTotalValue()))
                    .get();

            openSet.remove(currentNodeLabel);

            var transitions = graph.getTransitionsByVertexLabel(currentNodeLabel);

            for (Transition transition : transitions) {
                var neighbourParams = new HeuristicNodeDTO();
                neighbourParams.setParentLabel(currentNodeLabel);
                neighbourParams.setFromSourceCostValue(valueLabels.get(currentNodeLabel).getFromSourceCostValue() + transition.getWeight());
                neighbourParams.setHeuristicGoalDistance(calculateHeuristics(transition.getDestination(), graph.getExitVertexLabel()));

                if (openSet.contains(transition.getDestination())
                        && valueLabels.get(transition.getDestination()).getTotalValue()
                        <= neighbourParams.getTotalValue()) {
                    continue;
                }

                if (closedSet.contains(transition.getDestination())
                        && valueLabels.get(transition.getDestination()).getTotalValue()
                        <= neighbourParams.getTotalValue()) {
                    continue;
                } else {
                    openSet.add(transition.getDestination());
                }

                valueLabels.put(transition.getDestination(), neighbourParams);

                if (transition.getDestination().equals(graph.getExitVertexLabel())) {
                    openSet.clear();
                    break;
                }
            }

            closedSet.add(currentNodeLabel);
        }

        var pathDTO = new ShortestPathDTO();
        pathDTO.setCost(valueLabels.get(graph.getExitVertexLabel()).getFromSourceCostValue());

        var shortestPath = new ArrayList<String>();
        shortestPath.add(graph.getEntranceVertexLabel());

        var backtrackCursor = graph.getExitVertexLabel();
        while (!backtrackCursor.equals(graph.getEntranceVertexLabel())) {
            shortestPath.add(backtrackCursor);
            backtrackCursor = valueLabels.get(backtrackCursor).getParentLabel();
        }

        pathDTO.setPath(shortestPath);

        return pathDTO;
    }

    private Integer calculateHeuristics(String source, String goal) {
        var sourceCoordinates = source.split("_");
        var goalCoordinates = goal.split("_");

        var sourceX = Integer.parseInt(sourceCoordinates[0]);
        var sourceY = Integer.parseInt(sourceCoordinates[1]);
        var goalX = Integer.parseInt(goalCoordinates[0]);
        var goalY = Integer.parseInt(goalCoordinates[1]);

        return Math.abs(sourceX - goalX) + Math.abs(sourceY - goalY);
    }
}