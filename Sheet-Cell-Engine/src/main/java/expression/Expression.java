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
    private final Object[] arguments; // The arguments provided to the function

    private boolean isValid = true;
    private ErrorType errorType; // NaN, UNDEFINED, UNKNOWN

    // Getter and setter for error state
    public void setError(ErrorType errorType) {
        this.isValid = false;
        this.errorType = errorType;
    }

    public boolean isValid() {
        return isValid;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Expression(String functionName, Object... arguments) {
        this.functionType = FunctionRegistry.getFunctionType(functionName);
        this.arguments = arguments;
    }

    /**
     * Evaluates the expression while evaluating all the nested expressions as well.
     *
     * @return The result of the evaluation or an error type if invalid.
     */
    public Object evaluate() {
        Object[] evaluatedArgs = new Object[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            evaluatedArgs[i] = evaluateArgument(arguments[i]);

            // Check if the argument evaluation resulted in an error
            if (evaluatedArgs[i] instanceof ErrorType) {
                setError((ErrorType) evaluatedArgs[i]);
                return errorType;
            }
        }

        Object result = FunctionRegistry.getFunctionHandler(functionType).execute(evaluatedArgs);
        if (result instanceof ErrorType) {
            setError((ErrorType) result);
            return errorType;
        } else {
            return result;
        }
    }

    private Object evaluateArgument(Object arg) {
        if (arg instanceof Number || arg instanceof Boolean || arg instanceof String) {
            return arg;
        } else if (arg instanceof Expression) {
            return ((Expression) arg).evaluate();
        } else {
            return ErrorType.UNDEFINED;
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
