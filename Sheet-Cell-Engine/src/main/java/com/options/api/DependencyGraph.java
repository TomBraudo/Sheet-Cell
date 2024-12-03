package com.options.api;

import java.util.*;

class DependencyGraph {
    private final Map<Cell, Set<Cell>> dependencies = new HashMap<>();

    public void addDependency(Cell dependent, Cell dependency){
        dependencies.computeIfAbsent(dependent, k -> new HashSet<>()).add(dependency);
    }

    public void removeDependency(Cell dependent, Cell dependency){
        if(dependencies.containsKey(dependent)){
            dependencies.get(dependent).remove(dependency);
            if(dependencies.get(dependent).isEmpty()){
                dependencies.remove(dependent);
            }
        }
    }

    public Set<Cell> getDependents(Cell dependent){
        Set<Cell> dependents = new HashSet<>();

        for(Map.Entry<Cell, Set<Cell>> entry : dependencies.entrySet()){
            if(entry.getValue().contains(dependent)){
                dependents.add(entry.getKey());
            }
        }

        return dependents;
    }

    public Set<Cell> getDependencies(Cell cell) {
        return dependencies.getOrDefault(cell, new HashSet<>());
    }

    public boolean hasCircularDependency(Cell start, Cell current, Set<Cell> visited) {
        if (!visited.add(current)) {
            return true; // Circular dependency detected
        }
        for (Cell dependency : getDependencies(current)) {
            if (dependency == start || hasCircularDependency(start, dependency, visited)) {
                return true;
            }
        }
        visited.remove(current); // Backtrack
        return false;
    }
}
