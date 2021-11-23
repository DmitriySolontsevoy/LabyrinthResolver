package converters;

import graph.Graph;

import java.util.function.Function;

public class ArrayToGraphConverter implements Function<char[][], Graph> {

    @Override
    public Graph apply(char[][] array) {
        var graph = new Graph();

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                var symbol = array[i][j];

                if (symbol != '□') {
                    if (symbol == '●' || symbol == 'A' || symbol == 'B') {
                        var label = String.format("%d_%d", i, j);
                        if (symbol == 'A') {
                            graph.setEntranceVertexLabel(label);
                        } else if (symbol == 'B') {
                            graph.setExitVertexLabel(label);
                        }
                        graph.addNewVertex(label);
                        searchForAdjacentVertices(graph, array, i, j);
                    }
                }
            }
        }

        return graph;
    }

    private void searchForAdjacentVertices(Graph graph, char[][] array, int rowIndex, int columnIndex) {
        if (rowIndex > 0
                && rowIndex < array.length - 1
                && (array[rowIndex + 1][columnIndex] == 'A'
                || array[rowIndex + 1][columnIndex] == 'B'
                || array[rowIndex + 1][columnIndex] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex + 1, columnIndex));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex, columnIndex),
                    String.format("%d_%d", rowIndex + 1, columnIndex),
                    1,
                    1
            );
        }

        if (rowIndex < array.length - 1
                && rowIndex > 0
                && (array[rowIndex - 1][columnIndex] == 'A'
                || array[rowIndex - 1][columnIndex] == 'B'
                || array[rowIndex - 1][columnIndex] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex - 1, columnIndex));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex, columnIndex),
                    String.format("%d_%d", rowIndex - 1, columnIndex),
                    1,
                    1
            );
        }

        if (columnIndex > 0
                && columnIndex < array[rowIndex].length - 1
                && (array[rowIndex][columnIndex + 1] == 'A'
                || array[rowIndex][columnIndex + 1] == 'B'
                || array[rowIndex][columnIndex + 1] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex, columnIndex + 1));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex, columnIndex),
                    String.format("%d_%d", rowIndex, columnIndex + 1),
                    1,
                    1
            );
        }

        if (columnIndex < array[rowIndex].length - 1
                && columnIndex > 0
                && (array[rowIndex][columnIndex - 1] == 'A'
                || array[rowIndex][columnIndex - 1] == 'B'
                || array[rowIndex][columnIndex - 1] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex, columnIndex - 1));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex, columnIndex),
                    String.format("%d_%d", rowIndex, columnIndex - 1),
                    1,
                    1
            );
        }
    }
}
