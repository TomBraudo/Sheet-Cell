package com.options.api;

class StringExpression extends Expression {
    private final String functionName;
    private final Object[] arguments;

    public StringExpression(String functionName, Object... arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate() {
        switch (functionName) {
            case "CONCAT":
                return concat(arguments);
            case "SUB":
                return substring(arguments);
            default:
                throw new IllegalArgumentException("Unknown string function: " + functionName);
        }
    }

    private String concat(Object[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("CONCAT requires exactly 2 arguments.");
        }
        String str1, str2;
        str1 = evaluateString(args[0]);
        str2 = evaluateString(args[1]);
        return str1 + str2;
    }

    private String substring(Object[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("SUB requires exactly 3 arguments.");
        }
        String str;
        int startIndex, endIndex;
        str = evaluateString(args[0]);
        startIndex = evaluateNumeric(args[1]);
        endIndex = evaluateNumeric(args[2]);
        return str.substring(startIndex, endIndex);
    }

    private String evaluateString(Object arg) {
        if (arg instanceof String) {
            return (String) arg;
        } else if (arg instanceof StringExpression) {
            return (String) ((StringExpression) arg).evaluate();
        } else {
            throw new IllegalArgumentException("Expected a string argument, but got: " + arg.getClass().getName());
        }
    }

    private int evaluateNumeric(Object arg) {
        double value;
        if (arg instanceof Number) {
            value = ((Number) arg).doubleValue();
        } else if (arg instanceof MathematicalExpression) {
            value = (double) ((MathematicalExpression) arg).evaluate();
        } else {
            throw new IllegalArgumentException("Expected a numeric argument, but got: " + arg.getClass().getName());
        }

        // Check if the number is a whole number
        if (value % 1 != 0) {
            throw new IllegalArgumentException("Expected a whole number, but got: " + value);
        }

        // Convert to integer and return
        return (int) value;
    }
}