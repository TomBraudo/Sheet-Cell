package com.options.api;
import java.lang.StringBuilder;

public class Expression {
    private final FunctionType functionType;
    private final Object[] arguments;
    private final String functionName;

    public Expression(String functionName, Object... arguments) {
        this.functionType = FunctionRegistry.getFunctionType(functionName);
        this.arguments = arguments;
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Object evaluate() {
        Object[] evaluatedArgs = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            evaluatedArgs[i] = parseArgument(arguments[i]);
        }
        return FunctionRegistry.getFunctionHandler(functionType).execute(evaluatedArgs);
    }

    private Object parseArgument(Object arg) {
        if (arg instanceof Number || arg instanceof String) {
            return arg;
        } else if (arg instanceof Expression) {
            return ((Expression) arg).evaluate();
        } else if (arg instanceof String) {
            return ExpressionParser.parseExpression((String) arg).evaluate();
        } else {
            throw new IllegalArgumentException("Invalid argument type: " + arg.getClass().getName());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{%s", functionName));
        for (Object argument : arguments) {
            sb.append(String.format(",%s", argument));
        }
        sb.append("}");

        return sb.toString();
    }
}
