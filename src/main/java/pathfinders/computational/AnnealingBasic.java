package pathfinders.computational;

import dto.ShortestPathDTO;
import graph.Graph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AnnealingBasic implements BiFunction<Graph, Integer, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph, Integer optimalCost) {
        var temperature = 200;
        var result = generateInitialResult(graph);

        while (temperature > 0 && result.getCost() > optimalCost) {
            ShortestPathDTO newResult;
            if (result.getPath().size() > 10) {
                newResult = tryImprovingResult(result, graph);
            } else {
                break;
            }

            var delta = Double.valueOf(newResult.getCost() - result.getCost());

            var doTransition = false;
            if (delta < 0) {
                doTransition = true;
            } else {
                var transitionChance = Math.exp(-delta/temperature);
                var seed = Math.random();
                if (seed <= transitionChance) {
                    doTransition = true;
                }
            }

            if (doTransition) {
                result = newResult;
            }

            if (result.getCost() <= optimalCost) {
                return result;
            }

            temperature--;
        }

        return result;
    }

    private ShortestPathDTO tryImprovingResult(ShortestPathDTO result, Graph graph) {
        var newResult = new ShortestPathDTO(result);

        var intersections = newResult.getPath().stream()
                .filter(vertex -> graph.getTransitionsByVertexLabel(vertex).size() > 2)
                .collect(Collectors.toList());

        var seed = Double.valueOf(Math.random() * intersections.size()).intValue();
        var chosenIntersection = intersections.get(seed);
        var chosenIndex = newResult.getPath().indexOf(chosenIntersection);
        var bannedTransition = newResult.getPath().get(newResult.getPath().indexOf(chosenIntersection) + 1);

        var success = retryPathFromTo(
                newResult,
                graph,
                chosenIntersection,
                chosenIndex,
                bannedTransition
        );

        if (success) {
            return newResult;
        } else {
            return result;
        }
    }

    private boolean retryPathFromTo(ShortestPathDTO path, Graph graph, String chosenIntersection, Integer chosenIndex, String bannedTransition) {
        for (int i = chosenIndex; i < path.getPath().size() - 1; i++) {
            var current = path.getPath().get(i);
            var next = path.getPath().get(i + 1);
            var costOfTransition = graph.getTransitionsByVertexLabel(current).stream()
                    .filter(x -> x.getDestination().equals(next))
                    .findFirst()
                    .get()
                    .getWeight();
            path.addCost(-costOfTransition);
        }

        path.getPath().subList(chosenIndex + 1, path.getPath().size() - 1).clear();

        var visitedMap = new HashMap<String, Integer>();
        for (Vertex vertex : graph.getAllVertices()) {
            if (path.getPath().contains(vertex.getLabel()) && !vertex.getLabel().equals(graph.getExitVertexLabel())) {
                visitedMap.put(vertex.getLabel(), 0);
            } else {
                visitedMap.put(vertex.getLabel(), -1);
            }
        }
        visitedMap.put(bannedTransition, 0);
        visitedMap.put(graph.getEntranceVertexLabel(), 0);

        var cursor = chosenIntersection;
        while (!cursor.equals(graph.getExitVertexLabel())) {
            var possibleTransitions = graph.getTransitionsByVertexLabel(cursor).stream()
                    .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                    .collect(Collectors.toList());

            if (possibleTransitions.size() < 1) {
                var resetTo = cursor;
                var found = false;
                var pathIterator = path.getPath().size() - 2;

                while (!found) {
                    if (pathIterator < 0) {
                        return false;
                    }

                    resetTo = path.getPath().get(pathIterator);
                    var possiblePreviousTransitions = graph.getTransitionsByVertexLabel(resetTo).stream()
                            .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                            .collect(Collectors.toList());
                    if (possiblePreviousTransitions.size() > 0) {
                        found = true;
                    }
                    pathIterator--;
                }

                var prevCursor = path.getPath().get(path.getPath().size() - 3);
                var backtickCursor = path.getPath().get(path.getPath().size() - 2);
                while (!backtickCursor.equals(resetTo)) {
                    var finalBacktickCursor = backtickCursor;

                    var costOfTransition = graph.getTransitionsByVertexLabel(prevCursor).stream()
                            .filter(x -> x.getDestination().equals(finalBacktickCursor))
                            .findFirst()
                            .get()
                            .getWeight();
                    path.addCost(-costOfTransition);
                    path.getPath().remove(backtickCursor);

                    prevCursor = path.getPath().get(path.getPath().size() - 3);
                    backtickCursor = path.getPath().get(path.getPath().size() - 2);
                }
                cursor = resetTo;
            } else {
                var seed = 0;
                if (possibleTransitions.size() > 1) {
                    seed = Double.valueOf(Math.random() * possibleTransitions.size()).intValue();
                }

                var chosenTransition = possibleTransitions.get(seed);

                cursor = chosenTransition.getDestination();
                visitedMap.put(cursor, visitedMap.get(cursor) + 1);

                if (!cursor.equals(graph.getExitVertexLabel())) {
                    path.getPath().add(path.getPath().size() - 1, chosenTransition.getDestination());
                }
                path.addCost(chosenTransition.getWeight());
            }
        }

        return true;
    }

    private ShortestPathDTO generateInitialResult(Graph graph) {
        var solution = new ShortestPathDTO();
        solution.setPath(new ArrayList<>());
        solution.setCost(0);

        var visitedMap = new HashMap<String, Integer>();
        for (Vertex vertex : graph.getAllVertices()) {
            visitedMap.put(vertex.getLabel(), -1);
        }
        visitedMap.put(graph.getEntranceVertexLabel(), 0);

        var cursor = graph.getEntranceVertexLabel();
        solution.getPath().add(graph.getEntranceVertexLabel());

        while (!cursor.equals(graph.getExitVertexLabel())) {
            var possibleTransitions = graph.getTransitionsByVertexLabel(cursor).stream()
                    .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                    .collect(Collectors.toList());

            if (possibleTransitions.size() < 1) {
                var resetTo = cursor;
                var found = false;
                var pathIterator = solution.getPath().size() - 2;

                while (!found) {
                    resetTo = solution.getPath().get(pathIterator);
                    var possiblePreviousTransitions = graph.getTransitionsByVertexLabel(resetTo).stream()
                            .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                            .collect(Collectors.toList());
                    if (possiblePreviousTransitions.size() > 0) {
                        found = true;
                    }
                    pathIterator--;
                }

                var prevCursor = solution.getPath().get(solution.getPath().size() - 2);
                var backtickCursor = solution.getPath().get(solution.getPath().size() - 1);
                while (!backtickCursor.equals(resetTo)) {
                    var finalBacktickCursor = backtickCursor;
                    var costOfTransition = graph.getTransitionsByVertexLabel(prevCursor).stream()
                            .filter(x -> x.getDestination().equals(finalBacktickCursor))
                            .findFirst()
                            .get()
                            .getWeight();
                    solution.addCost(-costOfTransition);
                    solution.getPath().remove(backtickCursor);

                    prevCursor = solution.getPath().get(solution.getPath().size() - 2);
                    backtickCursor = solution.getPath().get(solution.getPath().size() - 1);
                }
                cursor = resetTo;
            } else {
                var seed = 0;
                if (possibleTransitions.size() > 1) {
                    seed = Double.valueOf(Math.random() * possibleTransitions.size()).intValue();
                }

                var chosenTransition = possibleTransitions.get(seed);

                cursor = chosenTransition.getDestination();
                visitedMap.put(cursor, visitedMap.get(cursor) + 1);

                solution.getPath().add(chosenTransition.getDestination());
                solution.addCost(chosenTransition.getWeight());
            }
        }

        return solution;
    }
}
