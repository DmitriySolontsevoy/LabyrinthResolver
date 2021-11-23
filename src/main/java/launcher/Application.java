package launcher;

import converters.ArrayToGraphConverter;
import converters.ArrayToWeightedGraphConverter;
import dto.ShortestPathDTO;
import graph.Graph;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import parser.InputMazeParser;
import pathfinders.computational.GeneticBasic;
import pathfinders.greedy.AStarBasic;
import pathfinders.greedy.DijkstraBasic;
import pathfinders.greedy.GreedySearchBasic;
import pathfinders.greedy.LeeBasic;

import java.util.ArrayList;
import java.util.function.Function;

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

        System.out.println("NO-WEIGHT LABYRINTH TRAVERSAL");

        executeAlgorithm(array, graph, "Dijkstra", dijkstraFunction);
        executeAlgorithm(array, graph, "A*", aStarFunction);
        executeAlgorithm(array, graph, "Greedy search", greedySearchFunction);
        executeAlgorithm(array, graph, "Lee", leeFunction);

        var weightedConverter = new ArrayToWeightedGraphConverter();

        array = parser.parseWeightedNotOrientedMaze();
        graph = weightedConverter.apply(array);

        System.out.println("\nWEIGHTED LABYRINTH TRAVERSAL");

        executeWeightedAlgorithm(array, graph, "Dijkstra", dijkstraFunction);
        executeWeightedAlgorithm(array, graph, "A*", aStarFunction);
        executeWeightedAlgorithm(array, graph, "Greedy search", greedySearchFunction);
        executeWeightedAlgorithm(array, graph, "Lee", leeFunction);

        System.out.println("\nCOMPUTATIONAL INTELLIGENCE METHODS");

        var geneticFunction = new GeneticBasic();

        array = parser.parseNotWeightedNotOrientedMaze();
        graph = converter.apply(array);

        executeAlgorithm(array, graph, "Genetic", geneticFunction);
    }

    private static void executeAlgorithm(char[][] array, Graph graph, String name, Function<Graph, ShortestPathDTO> method) {
        var startTime = System.nanoTime();
        var path = method.apply(graph).getPath();
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

    private static void executeWeightedAlgorithm(char[][] array, Graph graph, String name, Function<Graph, ShortestPathDTO> method) {
        var startTime = System.nanoTime();
        var pathDTO = method.apply(graph);
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