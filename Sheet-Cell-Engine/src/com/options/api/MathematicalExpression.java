package com.options.api;

class MathematicalExpression extends Expression {
    private final String functionName;
    private final Object[] arguments;
    public MathematicalExpression(String functionName, Object... arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate() {
        double[] values = new double[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            values[i] = parseObject(arguments[i]);
        }
        switch (functionName) {
            case "PLUS":
                validateNumOfArguments(functionName, arguments.length, 2);
                return values[0] + values[1];
            case "MINUS":
                validateNumOfArguments(functionName, arguments.length, 2);
                return values[0] - values[1];
            case "TIMES":
                validateNumOfArguments(functionName, arguments.length, 2);
                return values[0] * values[1];
            case "DIVIDE":
                validateNumOfArguments(functionName, arguments.length, 2);
                if (values[1] == 0) throw new ArithmeticException("Division by zero");
                return values[0] / values[1];
            case "MOD":
                validateNumOfArguments(functionName, arguments.length, 2);
                return values[0] % values[1];
            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    private void validateNumOfArguments(String functionName, int numOfArguments, int numOfNeeded) {
        if(numOfArguments != numOfNeeded){
            throw new IllegalArgumentException("Function" + functionName + " requires exactly " + numOfNeeded + " arguments");
        }
    }

    private double parseObject(Object arg){
        if(arg instanceof Number){
            return ((Number)arg).doubleValue();
        }
        else if(arg instanceof MathematicalExpression){
            return (double)((MathematicalExpression)arg).evaluate();
        }
        else {
            throw new IllegalArgumentException("Type mismatch" + arg.getClass().getName());
        }
    }
}