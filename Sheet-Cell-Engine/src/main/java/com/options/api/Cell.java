package com.options.api;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Cell {
    private final String location;
    private Object value;
    private Object effectiveValue;

    public Cell(String location, String inputValue) {
        this.location = location;
        setValue(inputValue);
    }

    public String getLocation() {
        return location;
    }

    public Object getValue() {
        return effectiveValue;
    }

    public void setValue(String inputValue) {
        // Save the current state for rollback
        Object oldValue = this.value;
        Object oldEffectiveValue = this.effectiveValue;

        try {
            // Parse and set the new value
            this.value = parseValue(inputValue);

            // Rebuild dependencies
            DependencyGraph graph = Sheet.getDependencyGraph();
            graph.getDependencies(this).forEach(dependency -> graph.removeDependency(this, dependency));
            addDependencies(inputValue, graph);

            // Detect circular dependency
            if (graph.hasCircularDependency(this, this, new HashSet<>())) {
                throw new IllegalStateException("Circular dependency detected for cell: " + location);
            }

            // Compute effective value
            computeEffectiveValue();

            // Notify dependents
            notifyDependents(graph);
        } catch (Exception e) {
            // Rollback
            this.value = oldValue;
            this.effectiveValue = oldEffectiveValue;
            throw e;
        }
    }

    private void addDependencies(String inputValue, DependencyGraph graph) {
        String refPattern = "\\{REF,\\s*([A-Z]\\d+)\\}";
        Pattern p = Pattern.compile(refPattern);
        Matcher m = p.matcher(inputValue);

        while (m.find()) {
            String ref = m.group(1);
            Cell referencedCell = Sheet.getCellStatic(ref);
            if (referencedCell != null) {
                graph.addDependency(this, referencedCell);
            } else {
                throw new IllegalArgumentException("Referenced cell " + ref + " does not exist.");
            }
        }
    }

    private void computeEffectiveValue() {
        if (value instanceof Expression) {
            this.effectiveValue = ((Expression) value).evaluate();
        } else {
            this.effectiveValue = value;
        }
    }

    private void notifyDependents(DependencyGraph graph) {
        for (Cell dependent : graph.getDependents(this)) {
            dependent.computeEffectiveValue();
        }
    }

    private Object parseValue(String inputValue) {
        try {
            if (isExpression(inputValue)) {
                return ExpressionParser.parseExpression(inputValue);
            }
            return Double.parseDouble(inputValue);
        } catch (NumberFormatException e) {
            return inputValue;
        }
    }

    private boolean isExpression(String inputValue) {
        return inputValue.startsWith("{") && inputValue.endsWith("}");
    }
}
