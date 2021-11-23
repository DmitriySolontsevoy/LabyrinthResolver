package pathfinders.computational;

import dto.ShortestPathDTO;
import graph.Graph;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneticBasic implements Function<Graph, ShortestPathDTO> {

    @Override
    public ShortestPathDTO apply(Graph graph) {
        var initialGeneration = new ArrayList<ShortestPathDTO>();

        for (int i = 0; i < 100; i++) {
            tryToAddNewCreatureTo(initialGeneration, graph);
        }

        ShortestPathDTO result = null;
        List<ShortestPathDTO> generation = new ArrayList<>(initialGeneration);
        var numOfGens = 0;
        while (numOfGens < 50) {
            mutationCycle(generation, graph);
            crossingCycle(generation, graph);

            generation = generation.stream()
                    .sorted(Comparator.comparingInt(ShortestPathDTO::getCost))
                    .limit(50)
                    .collect(Collectors.toList());

            result = generation.stream().min(Comparator.comparingInt(ShortestPathDTO::getCost)).get();
            numOfGens++;
        }

        return result;
    }

    private void mutationCycle(List<ShortestPathDTO> generation, Graph graph) {
        for (ShortestPathDTO specimen : generation) {
            var checkSet = new LinkedHashSet<String>();

            var knotStartPos = -1;
            var knotEndPos = -1;

            for (int i = 0; i < specimen.getPath().size(); i++) {
                if (!checkSet.add(specimen.getPath().get(i))) {
                    knotEndPos = i;
                    knotStartPos = new ArrayList<>(checkSet).indexOf(specimen.getPath().get(i));

                    break;
                }
            }

            if (knotStartPos > -1) {
                for (int i = knotStartPos; i < knotEndPos - 1; i++) {
                    var current = specimen.getPath().get(i);
                    var next = specimen.getPath().get(i+1);
                    var costOfTransition = graph.getTransitionsByVertexLabel(current).stream()
                            .filter(x -> x.getDestination().equals(next))
                            .findFirst()
                            .get()
                            .getWeight();
                    specimen.addCost(-costOfTransition);
                }

                specimen.getPath().subList(knotStartPos, knotEndPos).clear();
            }
        }
    }

    private void crossingCycle(List<ShortestPathDTO> generation, Graph graph) {
        for (int i = 0; i < 50; i++) {
            var seedM = Double.valueOf(Math.random() * generation.size()).intValue();
            var seedF = Double.valueOf(Math.random() * generation.size()).intValue();

            while (seedF == seedM) {
                seedF = Double.valueOf(Math.random() * generation.size()).intValue();
            }

            var paternalSpecimen = generation.get(seedM);
            var maternalSpecimen = generation.get(seedF);

            var crossMPos = -1;
            var crossFPos = -1;

            var zoomMFactor = Double.valueOf(Math.random() * paternalSpecimen.getPath().size() / 3 + 2).intValue();
            var zoomFFactor = Double.valueOf(Math.random() * maternalSpecimen.getPath().size() / 3 + 2).intValue();

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

    private void tryToAddNewCreatureTo(ArrayList<ShortestPathDTO> initialGeneration, Graph graph) {
        var specimen = new ShortestPathDTO();
        specimen.setPath(new ArrayList<>());
        specimen.setCost(0);
        var cursor = graph.getEntranceVertexLabel();
        specimen.getPath().add(graph.getEntranceVertexLabel());

        while (!cursor.equals(graph.getExitVertexLabel())) {
            var possibleTransitions = graph.getTransitionsByVertexLabel(cursor);

            if (possibleTransitions.size() < 1) {
                break;
            }

            var seed = Double.valueOf(Math.random() * possibleTransitions.size()).intValue();

            var chosenTransition = possibleTransitions.get(seed);

            cursor = chosenTransition.getDestination();
            specimen.getPath().add(chosenTransition.getDestination());
            specimen.addCost(chosenTransition.getWeight());
        }

        if (specimen.getPath().contains(graph.getExitVertexLabel())) {
            initialGeneration.add(specimen);
        }
    }
}
