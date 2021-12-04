package pathfinders.computational;

import dto.ShortestPathDTO;
import graph.Graph;
import graph.Vertex;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GeneticBasic implements BiFunction<Graph, Integer, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph, Integer optimalCost) {
        var initialGeneration = new ArrayList<ShortestPathDTO>();

        for (int i = 0; i < 30; i++) {
            var foundBest = tryToAddNewCreatureTo(initialGeneration, graph, optimalCost);
            if (foundBest != null) {
                return foundBest;
            }
        }

        ShortestPathDTO result = new ShortestPathDTO(new ArrayList<>(), Integer.MAX_VALUE);

        List<ShortestPathDTO> generation = new ArrayList<>(initialGeneration);
        var numOfGens = 0;
        while (numOfGens < 15) {
            crossingCycle(generation, graph);

            generation = generation.stream()
                    .sorted(Comparator.comparingInt(ShortestPathDTO::getCost))
                    .limit(15)
                    .collect(Collectors.toList());

            result = generation.stream().min(Comparator.comparingInt(ShortestPathDTO::getCost)).get();
            if (result.getCost() <= optimalCost) {
                return result;
            }

            numOfGens++;
        }

        return result;
    }

    private void crossingCycle(List<ShortestPathDTO> generation, Graph graph) {
        for (int i = 0; i < 15; i++) {
            var seedM = Double.valueOf(Math.random() * generation.size()).intValue();
            var seedF = Double.valueOf(Math.random() * generation.size()).intValue();

            while (seedF == seedM) {
                seedF = Double.valueOf(Math.random() * generation.size()).intValue();
            }

            var paternalSpecimen = generation.get(seedM);
            var maternalSpecimen = generation.get(seedF);

            if (paternalSpecimen.getPath().size() > 5 && maternalSpecimen.getPath().size() > 5) {
                var crossMPos = -1;
                var crossFPos = -1;

                var zoomMFactor = Double.valueOf(Math.random() * paternalSpecimen.getPath().size() / 4 + 2).intValue();
                var zoomFFactor = Double.valueOf(Math.random() * maternalSpecimen.getPath().size() / 4 + 2).intValue();

                for (int j = zoomMFactor; j < paternalSpecimen.getPath().size() - zoomMFactor; j++) {
                    if (maternalSpecimen.getPath().subList(zoomFFactor, maternalSpecimen.getPath().size() - zoomFFactor).contains(paternalSpecimen.getPath().get(j))) {
                        crossMPos = j;
                        crossFPos = maternalSpecimen.getPath().indexOf(paternalSpecimen.getPath().get(j));

                        break;
                    }
                }

                if (crossMPos > -1) {
                    var childSpecimen = new ShortestPathDTO();
                    childSpecimen.setCost(0);
                    childSpecimen.setPath(new ArrayList<>());
                    childSpecimen.getPath().add(paternalSpecimen.getPath().get(0));

                    for (int j = 0; j < crossMPos; j++) {
                        var current = paternalSpecimen.getPath().get(j);
                        var next = paternalSpecimen.getPath().get(j+1);
                        var costOfTransition = graph.getTransitionsByVertexLabel(current).stream()
                                .filter(x -> x.getDestination().equals(next))
                                .findFirst()
                                .get()
                                .getWeight();
                        childSpecimen.getPath().add(next);
                        childSpecimen.addCost(costOfTransition);
                    }

                    for (int j = crossFPos; j < maternalSpecimen.getPath().size() - 1; j++) {
                        var current = maternalSpecimen.getPath().get(j);
                        var next = maternalSpecimen.getPath().get(j+1);
                        var costOfTransition = graph.getTransitionsByVertexLabel(current).stream()
                                .filter(x -> x.getDestination().equals(next))
                                .findFirst()
                                .get()
                                .getWeight();
                        childSpecimen.getPath().add(next);
                        childSpecimen.addCost(costOfTransition);
                    }

                    if (childSpecimen.getCost() < paternalSpecimen.getCost() && childSpecimen.getCost() < maternalSpecimen.getCost()) {
                        generation.add(childSpecimen);
                    }
                }
            }
        }
    }

    private ShortestPathDTO tryToAddNewCreatureTo(ArrayList<ShortestPathDTO> initialGeneration, Graph graph, Integer optimalCost) {
        var specimen = new ShortestPathDTO();
        specimen.setPath(new ArrayList<>());
        specimen.setCost(0);

        var visitedMap = new HashMap<String, Integer>();
        for (Vertex vertex : graph.getAllVertices()) {
            visitedMap.put(vertex.getLabel(), -1);
        }
        visitedMap.put(graph.getEntranceVertexLabel(), 0);

        var cursor = graph.getEntranceVertexLabel();
        specimen.getPath().add(graph.getEntranceVertexLabel());

        while (!cursor.equals(graph.getExitVertexLabel())) {
            var possibleTransitions = graph.getTransitionsByVertexLabel(cursor).stream()
                    .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                    .collect(Collectors.toList());

            if (possibleTransitions.size() < 1) {
                var resetTo = cursor;
                var found = false;
                var pathIterator = specimen.getPath().size() - 2;

                while (!found) {
                    resetTo = specimen.getPath().get(pathIterator);
                    var possiblePreviousTransitions = graph.getTransitionsByVertexLabel(resetTo).stream()
                            .filter(transition -> visitedMap.get(transition.getDestination()) < 0)
                            .collect(Collectors.toList());
                    if (possiblePreviousTransitions.size() > 0) {
                        found = true;
                    }
                    pathIterator--;
                }

                var prevCursor = specimen.getPath().get(specimen.getPath().size() - 2);
                var backtickCursor = specimen.getPath().get(specimen.getPath().size() - 1);
                while (!backtickCursor.equals(resetTo)) {
                    var finalBacktickCursor = backtickCursor;
                    var costOfTransition = graph.getTransitionsByVertexLabel(prevCursor).stream()
                            .filter(x -> x.getDestination().equals(finalBacktickCursor))
                            .findFirst()
                            .get()
                            .getWeight();
                    specimen.addCost(-costOfTransition);
                    specimen.getPath().remove(backtickCursor);

                    prevCursor = specimen.getPath().get(specimen.getPath().size() - 2);
                    backtickCursor = specimen.getPath().get(specimen.getPath().size() - 1);
                }
                cursor = resetTo;
            } else {
                var seed = Double.valueOf(Math.random() * possibleTransitions.size()).intValue();

                var chosenTransition = possibleTransitions.get(seed);

                cursor = chosenTransition.getDestination();
                visitedMap.put(cursor, visitedMap.get(cursor) + 1);

                specimen.getPath().add(chosenTransition.getDestination());
                specimen.addCost(chosenTransition.getWeight());
            }
        }

        if (specimen.getCost() <= optimalCost) {
            return specimen;
        } else {
            initialGeneration.add(specimen);
            return null;
        }
    }
}
