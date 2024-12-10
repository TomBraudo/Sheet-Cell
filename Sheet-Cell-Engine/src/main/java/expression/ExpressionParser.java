package expression;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {
    public static Expression parseExpression(String input) {
        // Ensure the input starts and ends with curly braces
        if (!input.startsWith("{") || !input.endsWith("}")) {
            throw new IllegalArgumentException("Expression must be enclosed in curly braces: " + input);
        }

        // Remove the outer braces and split into function name and arguments
        String content = input.substring(1, input.length() - 1);
        String[] parts = content.split(",", 2);

        if (parts.length < 1) {
            throw new IllegalArgumentException("Expression is missing a function name: " + input);
        }

        String functionName = parts[0].trim();
        String argumentsString = parts.length > 1 ? parts[1] : "";

        // Get the function type from the registry
        FunctionType functionType = FunctionRegistry.getFunctionType(functionName);

        // Parse the arguments
        List<Object> arguments = parseArguments(argumentsString);

        // Validate the arguments for the specific function type
        validateArguments(functionType, arguments);

        // Create and return the unified Expression object
        return new Expression(functionName, arguments.toArray());
    }

    private static void validateArguments(FunctionType type, List<Object> arguments) {
        switch (type) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case CONCAT:
                if (arguments.size() != 2) {
                    throw new IllegalArgumentException(type + " requires exactly 2 arguments.");
                }
                break;
            case SUB:
                if (arguments.size() != 3) {
                    throw new IllegalArgumentException("SUB requires exactly 3 arguments.");
                }
                break;
            case REF:
                if (arguments.size() != 1) {
                    throw new IllegalArgumentException("REF requires exactly 1 argument.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported function type: " + type);
        }
    }

    private static List<Object> parseArguments(String argumentsString) {
        List<Object> arguments = new ArrayList<>();
        int braceLevel = 0;
        StringBuilder currentArg = new StringBuilder();

        for (char ch : argumentsString.toCharArray()) {
            if (ch == ',' && braceLevel == 0) {
                // Argument separator: process the current argument
                arguments.add(parseArgument(currentArg.toString().trim()));
                currentArg.setLength(0); // Clear the buffer
            } else {
                // Adjust brace level and append the character
                if (ch == '{') braceLevel++;
                if (ch == '}') braceLevel--;
                currentArg.append(ch);
            }
        }

        // Add the last argument if it exists
        if (currentArg.length() > 0) {
            arguments.add(parseArgument(currentArg.toString().trim()));
        }

        return arguments;
    }

    private static Object parseArgument(String arg) {
        if (arg.startsWith("{") && arg.endsWith("}")) {
            return parseExpression(arg); // Nested expression
        }

        // Check if the argument is a cell reference in "A1" format
        if (arg.matches("[A-Z]\\d+")) {
            return arg; // Recognize column as letter and row as number
        }

        try {
            return Double.parseDouble(arg); // Numeric argument
        } catch (NumberFormatException e) {
            return arg; // Raw string
        }
    }



}
