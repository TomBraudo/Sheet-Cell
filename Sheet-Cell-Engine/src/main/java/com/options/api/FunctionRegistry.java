package com.options.api;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private static final Map<String, FunctionType> STRING_TO_TYPE = new HashMap<>();
    private static final Map<FunctionType, FunctionHandler> TYPE_TO_FUNCTION = new HashMap<>();
    private static Sheet sheet; // Reference to the current sheet

    static {
        STRING_TO_TYPE.put("PLUS", FunctionType.PLUS);
        STRING_TO_TYPE.put("MINUS", FunctionType.MINUS);
        STRING_TO_TYPE.put("TIMES", FunctionType.TIMES);
        STRING_TO_TYPE.put("DIVIDE", FunctionType.DIVIDE);
        STRING_TO_TYPE.put("MOD", FunctionType.MOD);
        STRING_TO_TYPE.put("CONCAT", FunctionType.CONCAT);
        STRING_TO_TYPE.put("SUB", FunctionType.SUB);
        STRING_TO_TYPE.put("REF", FunctionType.REF); // Add REF function

        TYPE_TO_FUNCTION.put(FunctionType.PLUS, (args) -> (double) args[0] + (double) args[1]);
        TYPE_TO_FUNCTION.put(FunctionType.MINUS, (args) -> (double) args[0] - (double) args[1]);
        TYPE_TO_FUNCTION.put(FunctionType.TIMES, (args) -> (double) args[0] * (double) args[1]);
        TYPE_TO_FUNCTION.put(FunctionType.DIVIDE, (args) -> {
            if ((double) args[1] == 0) throw new ArithmeticException("Division by zero");
            return (double) args[0] / (double) args[1];
        });
        TYPE_TO_FUNCTION.put(FunctionType.MOD, (args) -> (double) args[0] % (double) args[1]);
        TYPE_TO_FUNCTION.put(FunctionType.CONCAT, (args) -> args[0].toString() + args[1].toString());
        TYPE_TO_FUNCTION.put(FunctionType.SUB, (args) -> {
            String str = (String) args[0];
            int start = (int) args[1];
            int end = (int) args[2];
            if (start < 0 || end > str.length() || start > end) {
                throw new IllegalArgumentException("Invalid substring indices");
            }
            return str.substring(start, end);
        });
        TYPE_TO_FUNCTION.put(FunctionType.REF, (args) -> {
            if (sheet == null) {
                throw new IllegalStateException("Sheet is not initialized");
            }
            String cellName = (String) args[0];
            return sheet.getCellValue(cellName); // Use the "letter-row, number-column" format
        });


    }

    public static FunctionType getFunctionType(String functionName) {
        if (!STRING_TO_TYPE.containsKey(functionName)) {
            throw new IllegalArgumentException("Unsupported function: " + functionName);
        }
        return STRING_TO_TYPE.get(functionName);
    }

    public static FunctionHandler getFunctionHandler(FunctionType type) {
        if (!TYPE_TO_FUNCTION.containsKey(type)) {
            throw new IllegalArgumentException("No handler defined for function type: " + type);
        }
        return TYPE_TO_FUNCTION.get(type);
    }

    public static void setSheet(Sheet s) {
        sheet = s;
    }

    @FunctionalInterface
    public interface FunctionHandler {
        Object execute(Object[] args);
    }
}
