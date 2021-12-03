package launcher;

import converters.ArrayToGraphConverter;
import converters.ArrayToWeightedGraphConverter;
import dto.ShortestPathDTO;
import graph.Graph;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import parser.InputMazeParser;
import pathfinders.computational.AnnealingBasic;
import pathfinders.computational.AntBasic;
import pathfinders.computational.GeneticBasic;
import pathfinders.greedy.AStarBasic;
import pathfinders.greedy.DijkstraBasic;
import pathfinders.greedy.GreedySearchBasic;
import pathfinders.greedy.LeeBasic;

import java.util.function.BiFunction;

@SpringBootApplication
public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        var parser = new InputMazeParser();
        var converter = new ArrayToGraphConverter();

        var array = parser.parseNotWeightedNotOrientedMaze();
        var graph = converter.apply(array);

        var dijkstraFunction = new DijkstraBasic();
        var aStarFunction = new AStarBasic();
        var greedySearchFunction = new GreedySearchBasic();
        var leeFunction = new LeeBasic();

        var optimalPathCost = 0;

        System.out.println("GREEDY METHODS\n");
        System.out.println("NO-WEIGHT LABYRINTH TRAVERSAL");

        executeAlgorithm(array, graph, "Dijkstra", dijkstraFunction, optimalPathCost);
        executeAlgorithm(array, graph, "A*", aStarFunction, optimalPathCost);
        executeAlgorithm(array, graph, "Greedy search", greedySearchFunction, optimalPathCost);
        executeAlgorithm(array, graph, "Lee", leeFunction, optimalPathCost);

        var weightedConverter = new ArrayToWeightedGraphConverter();

        array = parser.parseWeightedNotOrientedMaze();
        graph = weightedConverter.apply(array);

        System.out.println("\nWEIGHTED LABYRINTH TRAVERSAL");

        executeWeightedAlgorithm(array, graph, "Dijkstra", dijkstraFunction, optimalPathCost);
        executeWeightedAlgorithm(array, graph, "A*", aStarFunction, optimalPathCost);
        executeWeightedAlgorithm(array, graph, "Greedy search", greedySearchFunction, optimalPathCost);
        executeWeightedAlgorithm(array, graph, "Lee", leeFunction, optimalPathCost);

        System.out.println("\nCOMPUTATIONAL INTELLIGENCE METHODS");

        var geneticFunction = new GeneticBasic();
        var antFunction = new AntBasic();
        var annealingFunction = new AnnealingBasic();

        System.out.println("\nNO-WEIGHT LABYRINTH TRAVERSAL");

        array = parser.parseNotWeightedNotOrientedMaze();
        graph = converter.apply(array);

        optimalPathCost = dijkstraFunction.apply(graph, 0).getCost();

        System.out.println("\nOptimal: " + optimalPathCost + "\n");

        executeAlgorithm(array, graph, "Genetic", geneticFunction, optimalPathCost);
        executeAlgorithm(array, graph, "Ant", antFunction, optimalPathCost);
        executeAlgorithm(array, graph, "Annealing", annealingFunction, optimalPathCost);

        System.out.println("\nWEIGHTED LABYRINTH TRAVERSAL");

        array = parser.parseWeightedNotOrientedMaze();
        graph = weightedConverter.apply(array);

        optimalPathCost = dijkstraFunction.apply(graph, 0).getCost();

        System.out.println("\nOptimal: " + optimalPathCost + "\n");

        executeWeightedAlgorithm(array, graph, "Genetic", geneticFunction, optimalPathCost);
        executeWeightedAlgorithm(array, graph, "Ant", antFunction, optimalPathCost);
        executeWeightedAlgorithm(array, graph, "Annealing", annealingFunction, optimalPathCost);
    }

    private static void executeAlgorithm(char[][] array, Graph graph, String name, BiFunction<Graph, Integer, ShortestPathDTO> method, Integer optimalCost) {
        var startTime = System.nanoTime();
        var path = method.apply(graph, optimalCost).getPath();
        System.out.printf("\n%s algorithm invocation has finished.\nTime spent: %dns\n", name, System.nanoTime() - startTime);

        System.out.println("Shortest path size: " + path.size());

        var printArray = deepCopy(array);
        for (String vertexName : path) {
            var x = Integer.parseInt(vertexName.split("_")[0]);
            var y = Integer.parseInt(vertexName.split("_")[1]);

            printArray[x][y] = '✓';
        }

        System.out.println("Shortest path visually:");

        for (char[] item : printArray) {
            for (char c : item) {
                System.out.print(c);
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static void executeWeightedAlgorithm(char[][] array, Graph graph, String name, BiFunction<Graph, Integer, ShortestPathDTO> method, Integer optimalCost) {
        var startTime = System.nanoTime();
        var pathDTO = method.apply(graph, optimalCost);
        System.out.printf("\n%s algorithm invocation has finished.\nTime spent: %dns\n", name, System.nanoTime() - startTime);

        System.out.println("Shortest path size: " + pathDTO.getPath().size());
        System.out.println("Shortest path cost: " + pathDTO.getCost());

        var printArrayDijkstraWeighted = deepCopyWeighted(array);
        for (String vertexName : pathDTO.getPath()) {
            var x = Integer.parseInt(vertexName.split("_")[0]);
            var y = Integer.parseInt(vertexName.split("_")[1]);

            printArrayDijkstraWeighted[x][y] = '✓';
        }

        System.out.println("Shortest path visually:");

        for (char[] item : printArrayDijkstraWeighted) {
            for (char c : item) {
                System.out.print(c);
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static char[][] deepCopy(char[][] input) {
        var output = new char[input.length][];
        for (int i = 0; i < input.length; i++) {
            output[i] = new char[input[i].length];
            System.arraycopy(input[i], 0, output[i], 0, input[i].length);
        }

        return output;
    }

    private static char[][] deepCopyWeighted(char[][] input) {
        var output = new char[input.length/2 + 1][];
        for (int i = 0; i < input.length/2 + 1; i++) {
            output[i] = new char[input[i*2].length/2];
            for (int j = 0; j < output[i].length; j++) {
                output[i][j] = input[i*2][j*2];
            }
        }

        return output;
    }
}