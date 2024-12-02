package com.options.api;

class ExpressionParser {
    public static Expression parseExpression(String input) {
        if (!input.startsWith("{") || !input.endsWith("}")) {
            throw new IllegalArgumentException("Expression must be enclosed in curly braces: " + input);
        }

        // Remove outer braces and split into function name and arguments
        String content = input.substring(1, input.length() - 1);
        String[] parts = content.split(",", 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Expression is missing a function name: " + input);
        }

        String functionName = parts[0].trim();
        String argumentsString = parts.length > 1 ? parts[1] : "";

        // Get function type
        FunctionType functionType = FunctionRegistry.getFunctionType(functionName);

        // Parse arguments
        List<Object> arguments = parseArguments(argumentsString);

        // Validate arguments based on function type
        validateArguments(functionType, arguments);

        // Create an expression that uses the function handler
        return new Expression() {
            @Override
            public Object evaluate() {
                return FunctionRegistry.getFunctionHandler(functionType).execute(arguments.toArray());
            }
        };
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
                arguments.add(parseArgument(currentArg.toString().trim()));
                currentArg.setLength(0);
            } else {
                if (ch == '{') braceLevel++;
                if (ch == '}') braceLevel--;
                currentArg.append(ch);
            }
        }

        if (currentArg.length() > 0) {
            arguments.add(parseArgument(currentArg.toString().trim()));
        }

        return arguments;
    }

    private static Object parseArgument(String arg) {
        if (arg.startsWith("{") && arg.endsWith("}")) {
            return parseExpression(arg);
        }
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            return arg;
        }
    }

}
