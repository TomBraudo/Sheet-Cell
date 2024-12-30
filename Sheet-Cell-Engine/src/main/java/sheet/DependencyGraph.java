package sheet;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a directed graph of dependencies between cells.
 * Provides methods for managing dependencies and detecting circular references.
 */
class DependencyGraph implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<Cell, Set<Cell>> dependencies = new HashMap<>();

    public void addDependency(Cell dependent, Cell dependency) {
        dependencies.computeIfAbsent(dependent, k -> new HashSet<>()).add(dependency);
    }

    public void removeDependency(Cell dependent, Cell dependency) {
        if (dependencies.containsKey(dependent)) {
            dependencies.get(dependent).remove(dependency);
            if (dependencies.get(dependent).isEmpty()) {
                dependencies.remove(dependent);
            }
        }
    }

    public Set<Cell> getDependents(Cell dependent) {
        Set<Cell> dependents = new HashSet<>();
        for (Map.Entry<Cell, Set<Cell>> entry : dependencies.entrySet()) {
            if (entry.getValue().contains(dependent)) {
                dependents.add(entry.getKey());
            }
        }
        return dependents;
    }

    public Set<Cell> getDependencies(Cell cell) {
        return dependencies.getOrDefault(cell, new HashSet<>());
    }


    public List<Cell> hasCircularDependency(Cell cell) {
        // Use a stack to track the current path of visited cells
        List<Cell> path = new ArrayList<>();
        Set<Cell> visited = new HashSet<>();

        if (hasCircularDependencyDFS(cell, visited, path)) {
            // If a cycle is detected, return the path
            return path;
        }

        // Return an empty list if no cycle is detected
        return Collections.emptyList();
    }

    private boolean hasCircularDependencyDFS(Cell current, Set<Cell> visited, List<Cell> path) {
        // If the current cell is already in the path, a cycle is detected
        if (path.contains(current)) {
            // Add the current cell to close the loop in the path
            path.add(current);
            return true;
        }

        // If the current cell is already visited and not in the path, no cycle here
        if (visited.contains(current)) {
            return false;
        }

        // Mark the current cell as visited and add it to the path
        visited.add(current);
        path.add(current);

        // Traverse all dependencies of the current cell
        for (Cell dependency : getDependencies(current)) {
            if (hasCircularDependencyDFS(dependency, visited, path)) {
                return true;
            }
        }

        // Backtrack: remove the current cell from the path
        path.remove(current);
        return false;
    }

}
