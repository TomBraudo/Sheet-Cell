package com.options.api;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Cell {
    private final String location;
    private Object value; // Can store either an Expression or a raw value (String, Number)
    private Object effectiveValue; // Precomputed value of the cell
    private final ArrayList<Cell> dependentCells;
    private final ArrayList<Cell> dependentOnMe;


    public Cell(String location, String inputValue) {
        this.location = location;
        dependentCells = new ArrayList<Cell>();
        dependentOnMe = new ArrayList<Cell>();
        setValue(inputValue); // Initialize value and compute effectiveValue
    }

    public String getLocation() {
        return location;
    }

    public Object getValue() {
        return effectiveValue; // Simply return the precomputed value
    }

    public void addDependentCell(Cell dependentCell) {
        dependentOnMe.add(dependentCell);
    }
    public void removeDependentCell(Cell dependentCell) {
        dependentOnMe.remove(dependentCell);
    }

    public void addDependentCells(String inputValue) {
        // Regular expression to find {REF, XI} patterns
        String refPattern = "\\{REF,\\s*([A-Z]\\d+)\\}";
        Pattern p = Pattern.compile(refPattern);
        Matcher m = p.matcher(inputValue);

        while (m.find()) {
            String ref = m.group(1); // Extract referenced cell name (e.g., "C4")
            Cell referencedCell = Sheet.sheetRef.getCell(ref); // Access the referenced cell
            if (referencedCell != null) {
                dependentCells.add(referencedCell);
                referencedCell.addDependentCell(this);
            } else {
                throw new IllegalArgumentException("Referenced cell " + ref + " does not exist.");
            }
        }

        // Check for nested expressions and recursively process them
        String nestedPattern = "\\{(.*?)\\}"; // General pattern to find any nested expression
        Pattern nestedP = Pattern.compile(nestedPattern);
        Matcher nestedM = nestedP.matcher(inputValue);

        while (nestedM.find()) {
            String nestedExpression = nestedM.group(1); // Extract the nested expression (e.g., "PLUS, {REF, C4}, {REF, D4}")
            if (!nestedExpression.startsWith("REF")) { // Skip direct REF patterns (already handled)
                addDependentCells(nestedExpression); // Recursively process the nested expression
            }
        }
    }


    public void setValue(String inputValue) {
        // Save the current value and effective value to allow rollback
        Object oldValue = this.value;
        Object oldEffectiveValue = this.effectiveValue;

        // Clear existing dependencies and prepare for the new value
        for (Cell cell : dependentCells) {
            cell.removeDependentCell(this);
        }
        dependentCells.clear();

        try {
            // Parse and set the new value
            this.value = parseValue(inputValue);

            addDependentCells(inputValue);
            // Validate dependents recursively
            validateDependents();

            // If validation passes, compute and update effective values
            computeEffectiveValue();
            updateDependentsEffectiveValues();
        } catch (Exception e) {
            // Rollback to the old state if validation fails
            this.value = oldValue;
            this.effectiveValue = oldEffectiveValue;

            // Rebuild old dependencies
            addDependentCells(inputValue);

            throw e; // Forward the exception
        }
    }


    private Object parseValue(String inputValue) {
        try {
            if (isExpression(inputValue)) {
                return ExpressionParser.parseExpression(inputValue); // Parse as an Expression
            }
            // Attempt to parse as a number
            return Double.parseDouble(inputValue);
        } catch (NumberFormatException e) {
            // If not a number, treat as a raw string value
            return inputValue;
        }
    }

    private boolean isExpression(String inputValue) {
        return inputValue.startsWith("{") && inputValue.endsWith("}");
    }

    private void computeEffectiveValue() {
        if (value instanceof Expression) {
            // Evaluate the expression and store the result in effectiveValue
            this.effectiveValue = ((Expression) value).evaluate();
        } else {
            // If it's a simple value, just assign it directly
            this.effectiveValue = value;
        }
    }

    private void notifyDependents() {
        for (Cell dependent : dependentOnMe) {
            dependent.computeEffectiveValue(); // Recompute effective value for dependents
        }
    }

    private void validateExpression() {
        if (value instanceof Expression) {
            // Attempt to evaluate the expression to check legality
            ((Expression) value).evaluate();
        }
    }

    private void validateDependents() {
        for (Cell dependent : dependentOnMe) {
            // Validate this dependent
            dependent.validateExpression();

            // Recursively validate its dependents
            dependent.validateDependents();
        }
    }

    private void updateDependentsEffectiveValues() {
        for (Cell dependent : dependentOnMe) {
            dependent.computeEffectiveValue();
            dependent.updateDependentsEffectiveValues();
        }
    }
}
