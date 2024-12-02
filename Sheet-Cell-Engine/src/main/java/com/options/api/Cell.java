package com.options.api;

class Cell {
    private final String location;
    private Object value; // Can store either an Expression or a raw value (String, Number)

    public Cell(String location, String inputValue) {
        this.location = location;
        this.value = parseValue(inputValue);
    }

    public String getLocation() {
        return location;
    }

    public Object getValue() {
        if (value instanceof Expression) {
            return ((Expression) value).evaluate(); // Evaluate expressions
        }
        return value; // Return simple values as-is
    }

    public void setValue(String inputValue) {
        this.value = parseValue(inputValue);
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
}
