package expression;

import java.io.Serializable;
import java.lang.StringBuilder;

/**
* Represents a generic expression that can be evaluated to produce a result.
* Supports different types of functions such as mathematical operations and string manipulations.
*/
public class Expression implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FunctionType functionType; // The function type
    private final Object[] arguments; //The arguments provided to the function

    public Expression(String functionName, Object... arguments) {
        this.functionType = FunctionRegistry.getFunctionType(functionName);
        this.arguments = arguments;
    }

    //Evaluates the expression while evaluating all the nested expressions as well
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
        sb.append(String.format("{%s", functionType.toString()));
        for (Object argument : arguments) {
            sb.append(String.format(",%s", argument));
        }
        sb.append("}");

        return sb.toString();
    }
}
