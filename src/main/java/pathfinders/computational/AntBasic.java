package pathfinders.computational;

import dto.ShortestPathDTO;
import graph.Graph;
import graph.Transition;
import graph.Vertex;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AntBasic implements BiFunction<Graph, Integer, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph, Integer optimalCost) {
        var a = 1;
        var b = 2;

        var pheromoneValues = new HashMap<String, Double>();
        initPheromone(pheromoneValues, graph);

        ShortestPathDTO result = new ShortestPathDTO(new ArrayList<>(), Integer.MAX_VALUE);

        var numberOfIterations = 0;
        while (numberOfIterations < 10) {
            var antPaths = new ArrayList<ShortestPathDTO>();

            for (int i = 0; i < 12; i++) {
                var antPath = newAnt(graph, pheromoneValues, a, b);
                if (antPath.getCost() <= optimalCost) {
                    return antPath;
                }

                for (String vertex : antPath.getPath()) {
                    pheromoneValues.put(vertex, pheromoneValues.get(vertex) + optimalCost/antPath.getCost());
                }
                antPaths.add(antPath);
            }

            updatePheromone(pheromoneValues, graph);

            result = antPaths.stream().min(Comparator.comparingInt(ShortestPathDTO::getCost)).get();
            if (result.getCost() <= optimalCost) {
                break;
            }

            numberOfIterations++;
        }

        return result;
    }

    private void initPheromone(HashMap<String, Double> pheromoneValues, Graph graph) {
        for (Vertex vertex : graph.getAllVertices()) {
            pheromoneValues.put(vertex.getLabel(), 1D);
        }
    }

    private void updatePheromone(HashMap<String, Double> pheromoneValues, Graph graph) {
        var evaporationRate = 0.85;

        for (Vertex vertex : graph.getAllVertices()) {
            pheromoneValues.put(vertex.getLabel(), pheromoneValues.get(vertex.getLabel()) * evaporationRate);
        }
    }

    private ShortestPathDTO newAnt(Graph graph, HashMap<String, Double> pheromoneValues, Integer a, Integer b) {
        var visitedMap = new HashMap<String, Integer>();
        for (Vertex vertex : graph.getAllVertices()) {
            visitedMap.put(vertex.getLabel(), -1);
        }
        visitedMap.put(graph.getEntranceVertexLabel(), 0);

        var antPath = new ShortestPathDTO();
        antPath.setCost(0);
        antPath.setPath(new ArrayList<>());
        antPath.getPath().add(graph.getEntranceVertexLabel());
        var ant = graph.getEntranceVertexLabel();

        while (!ant.equals(graph.getExitVertexLabel())) {
            var possibleTransitions = graph.getTransitionsByVertexLabel(ant).stream()
                    .filter(transition -> visitedMap.get(transition.getDestination()) == -1)
                    .collect(Collectors.toList());

            if (possibleTransitions.size() < 1) {
                var resetTo = ant;
                var found = false;
                var pathIterator = antPath.getPath().size() - 2;

                while (!found) {
                    resetTo = antPath.getPath().get(pathIterator);
                    var possiblePreviousTransitions = graph.getTransitionsByVertexLabel(resetTo).stream()
                            .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                            .collect(Collectors.toList());
                    if (possiblePreviousTransitions.size() > 0) {
                        found = true;
                    }
                    pathIterator--;
                }

                var prevCursor = antPath.getPath().get(antPath.getPath().size() - 2);
                var backtickCursor = antPath.getPath().get(antPath.getPath().size() - 1);
                while (!backtickCursor.equals(resetTo)) {
                    var finalBacktickCursor = backtickCursor;
                    var costOfTransition = graph.getTransitionsByVertexLabel(prevCursor).stream()
                            .filter(x -> x.getDestination().equals(finalBacktickCursor))
                            .findFirst()
                            .get()
                            .getWeight();
                    antPath.addCost(-costOfTransition);
                    antPath.getPath().remove(backtickCursor);

                    prevCursor = antPath.getPath().get(antPath.getPath().size() - 2);
                    backtickCursor = antPath.getPath().get(antPath.getPath().size() - 1);
                }
                ant = resetTo;
            } else {
                if (possibleTransitions.size() > 1) {
                    var totalAttraction = 0D;
                    var attractionMap = new HashMap<String, Double>();
                    for (Transition transition : possibleTransitions) {
                        var cost = Math.pow(pheromoneValues.get(transition.getDestination()), a) + 1 / Math.pow(transition.getWeight(), b);
                        totalAttraction += cost;
                        attractionMap.put(transition.getDestination(), totalAttraction);
                    }

                    var comparators = attractionMap.entrySet().stream()
                            .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                            .collect(Collectors.toList());
                    var seed = Math.random() * totalAttraction;

                    for (Map.Entry<String, Double> entry : comparators) {
                        if (seed <= entry.getValue()) {
                            ant = entry.getKey();
                            visitedMap.put(ant, visitedMap.get(ant) + 1);
                            antPath.getPath().add(ant);
                            var weight = possibleTransitions.stream()
                                    .filter(transition -> transition.getDestination().equals(entry.getKey()))
                                    .findFirst()
                                    .get()
                                    .getWeight();
                            antPath.addCost(weight);
                            break;
                        }
                    }
                } else {
                    var transition = possibleTransitions.get(0);

                    ant = transition.getDestination();
                    visitedMap.put(ant, visitedMap.get(ant) + 1);

                    antPath.getPath().add(ant);
                    antPath.addCost(transition.getWeight());
                }
            }
        }

        return antPath;
    }
}
