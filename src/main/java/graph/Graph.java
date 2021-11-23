package graph;

import org.springframework.util.StringUtils;

import java.util.*;

public class Graph {

    private String entranceVertexLabel;

    private String exitVertexLabel;

    private final Map<Vertex, List<Transition>> adjacentVerticesMap = new HashMap<>();

    public Set<Vertex> getAllVertices() {
        return adjacentVerticesMap.keySet();
    }

    public Vertex getVertexByLabel(String label) {
        var optional = adjacentVerticesMap.keySet()
                .stream()
                .filter(vertex -> vertex.getLabel().equals(label))
                .findFirst();

        return optional.orElse(null);
    }

    public void addNewVertex(String label) {
        var checkLabel = getVertexByLabel(label);
        if (!StringUtils.isEmpty(checkLabel)) {
            return;
        }

        adjacentVerticesMap.put(new Vertex(label), new ArrayList<>());
    }

    public void addOneWayTransition(String labelOfA, String labelOfB, Integer weight) {
        if (adjacentVerticesMap.get(getVertexByLabel(labelOfA)).stream().noneMatch(transition -> transition.getDestination().equals(labelOfB))) {
            adjacentVerticesMap.get(getVertexByLabel(labelOfA))
                    .add(new Transition(labelOfB, weight));
        }
    }

    public void addTwoWayTransition(String labelOfA, String labelOfB, Integer weightToA, Integer weightToB) {
        if (adjacentVerticesMap.get(getVertexByLabel(labelOfA)).stream().noneMatch(transition -> transition.getDestination().equals(labelOfB))) {
            adjacentVerticesMap.get(getVertexByLabel(labelOfA))
                    .add(new Transition(labelOfB, weightToB));
        }
        if (adjacentVerticesMap.get(getVertexByLabel(labelOfB)).stream().noneMatch(transition -> transition.getDestination().equals(labelOfA))) {
            adjacentVerticesMap.get(getVertexByLabel(labelOfB))
                    .add(new Transition(labelOfA, weightToA));
        }
    }

    public List<Transition> getTransitionsByVertexLabel(String vertexLabel) {
        var vertex = getVertexByLabel(vertexLabel);
        return adjacentVerticesMap.get(vertex);
    }

    public String getEntranceVertexLabel() {
        return entranceVertexLabel;
    }

    public void setEntranceVertexLabel(String label) {
        entranceVertexLabel = label;
    }

    public String getExitVertexLabel() {
        return exitVertexLabel;
    }

    public void setExitVertexLabel(String label) {
        exitVertexLabel = label;
    }
}
