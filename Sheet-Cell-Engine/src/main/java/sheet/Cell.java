package sheet;

import expression.Expression;
import expression.ExpressionParser;
import engine.CellDTO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Class that represents a single cell in the sheet
class Cell {
    private final String location; //The cell's name (A1, C15...)
    private Object value; //The object that represents the value in the cell, could be an expression and also just a simple value (numerical, string...)
    private Object effectiveValue; //The effective value in the cell, the calculated expression or the simple value
    private final Sheet owner; //A reference to the sheet that it belongs to

    public Cell(String location, String inputValue, Sheet owner) {
        this.owner = owner;
        this.location = location;
        setValue(inputValue);
    }
    public String getLocation() {
        return location;
    }

    public Object getEffectiveValue() {
        return effectiveValue;
    }

    //Function that sets the cell's value, with detection if it's legal and if not rejecting the change
    public void setValue(String inputValue) {
        // Save the current state for rollback
        Object oldValue = this.value;
        Object oldEffectiveValue = this.effectiveValue;

        // Save dependencies for rollback
        DependencyGraph graph = Sheet.getDependencyGraph();
        Set<Cell> oldDependencies = new HashSet<>(graph.getDependencies(this));
        Map<Cell, Object> oldDependentsState = new HashMap<>();

        for (Cell dependent : graph.getDependents(this)) {
            oldDependentsState.put(dependent, dependent.effectiveValue);
        }

        try {
            // Parse and set the new value
            this.value = parseValue(inputValue);

            // Rebuild dependencies
            oldDependencies.forEach(dependency -> graph.removeDependency(this, dependency));
            addDependencies(inputValue, graph);

            // Detect circular dependency
            List<Cell> circularPath = graph.hasCircularDependency(this);
            if (!circularPath.isEmpty()) {
                throw new IllegalStateException("Circular dependency detected: " +
                        circularPath.stream()
                                .map(Cell::getLocation)
                                .reduce((a, b) -> a + " -> " + b)
                                .orElse(""));
            }

            // Compute effective value
            computeEffectiveValue();

            // Notify dependents
            notifyDependents(graph);

        } catch (Exception e) {
            // Rollback: restore value and effective value
            this.value = oldValue;
            this.effectiveValue = oldEffectiveValue;

            // Rollback: restore dependencies
            graph.getDependencies(this).forEach(dependency -> graph.removeDependency(this, dependency));
            for (Cell dependency : oldDependencies) {
                graph.addDependency(this, dependency);
            }

            // Rollback: restore dependents' effective values
            for (Map.Entry<Cell, Object> entry : oldDependentsState.entrySet()) {
                entry.getKey().effectiveValue = entry.getValue();
            }

            throw e;
        }
    }


    private void addDependencies(String inputValue, DependencyGraph graph) {
        String refPattern = "\\{REF,\\s*([A-Z]\\d+)\\}";
        Pattern p = Pattern.compile(refPattern);
        Matcher m = p.matcher(inputValue);

        while (m.find()) {
            String ref = m.group(1);
            Cell referencedCell = owner.getCell(ref);
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

    CellDTO getCellData(){
        ArrayList<String> dependsOn = new ArrayList<>();
        for(Cell dependent : Sheet.getDependencyGraph().getDependencies(this)){
            dependsOn.add(dependent.getLocation());
        }

        ArrayList<String> dependents = new ArrayList<>();
        for(Cell dependent : Sheet.getDependencyGraph().getDependents(this)){
            dependents.add(dependent.getLocation());
        }

        return new CellDTO(this.location, this.value.toString(), this.effectiveValue.toString(), dependsOn, dependents);
    }


    private boolean isExpression(String inputValue) {
        return inputValue.startsWith("{") && inputValue.endsWith("}");
    }
}
