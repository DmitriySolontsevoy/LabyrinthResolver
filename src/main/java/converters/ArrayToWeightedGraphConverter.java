package converters;

import graph.Graph;

import java.util.function.Function;

public class ArrayToWeightedGraphConverter implements Function<char[][], Graph> {

    @Override
    public Graph apply(char[][] array) {
        var graph = new Graph();

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                var symbol = array[i][j];

                if (symbol != '□') {
                    if (symbol == '●' || symbol == 'A' || symbol == 'B') {
                        var label = String.format("%d_%d", i/2, j/2);
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
        if (rowIndex > 1
                && rowIndex < array.length - 2
                && (array[rowIndex + 2][columnIndex] == 'A'
                || array[rowIndex + 2][columnIndex] == 'B'
                || array[rowIndex + 2][columnIndex] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex/2 + 1, columnIndex/2));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex/2, columnIndex/2),
                    String.format("%d_%d", rowIndex/2 + 1, columnIndex/2),
                    Character.getNumericValue(array[rowIndex + 1][columnIndex]),
                    Character.getNumericValue(array[rowIndex + 1][columnIndex])
            );
        }

        if (rowIndex < array.length - 2
                && rowIndex > 1
                && (array[rowIndex - 2][columnIndex] == 'A'
                || array[rowIndex - 2][columnIndex] == 'B'
                || array[rowIndex - 2][columnIndex] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex/2 - 1, columnIndex/2));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex/2, columnIndex/2),
                    String.format("%d_%d", rowIndex/2 - 1, columnIndex/2),
                    Character.getNumericValue(array[rowIndex - 1][columnIndex]),
                    Character.getNumericValue(array[rowIndex - 1][columnIndex])
            );
        }

        if (columnIndex > 1
                && columnIndex < array[rowIndex].length - 2
                && (array[rowIndex][columnIndex + 2] == 'A'
                || array[rowIndex][columnIndex + 2] == 'B'
                || array[rowIndex][columnIndex + 2] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex/2, columnIndex/2 + 1));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex/2, columnIndex/2),
                    String.format("%d_%d", rowIndex/2, columnIndex/2 + 1),
                    Character.getNumericValue(array[rowIndex][columnIndex + 1]),
                    Character.getNumericValue(array[rowIndex][columnIndex + 1])
            );
        }

        if (columnIndex < array[rowIndex].length - 2
                && columnIndex > 1
                && (array[rowIndex][columnIndex - 2] == 'A'
                || array[rowIndex][columnIndex - 2] == 'B'
                || array[rowIndex][columnIndex - 2] == '●')) {

            graph.addNewVertex(String.format("%d_%d", rowIndex/2, columnIndex/2 - 1));
            graph.addTwoWayTransition(
                    String.format("%d_%d", rowIndex/2, columnIndex/2),
                    String.format("%d_%d", rowIndex/2, columnIndex/2 - 1),
                    Character.getNumericValue(array[rowIndex][columnIndex - 1]),
                    Character.getNumericValue(array[rowIndex][columnIndex - 1])
            );
        }
    }
}
