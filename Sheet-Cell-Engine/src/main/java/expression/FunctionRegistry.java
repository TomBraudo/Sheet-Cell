package expression;


import sheet.Sheet;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private static final Map<String, FunctionType> STRING_TO_TYPE = new HashMap<>();
    private static final Map<FunctionType, FunctionHandler> TYPE_TO_FUNCTION = new HashMap<>();
    private static final Map<String, String> rangeNameToRange = new HashMap<>();
    private static Sheet sheet; // Reference to the current sheet

    static {
        STRING_TO_TYPE.put("PLUS", FunctionType.PLUS);
        STRING_TO_TYPE.put("MINUS", FunctionType.MINUS);
        STRING_TO_TYPE.put("TIMES", FunctionType.TIMES);
        STRING_TO_TYPE.put("DIVIDE", FunctionType.DIVIDE);
        STRING_TO_TYPE.put("MOD", FunctionType.MOD);
        STRING_TO_TYPE.put("CONCAT", FunctionType.CONCAT);
        STRING_TO_TYPE.put("SUB", FunctionType.SUB);
        STRING_TO_TYPE.put("REF", FunctionType.REF);
        STRING_TO_TYPE.put("SUM", FunctionType.SUM);
        STRING_TO_TYPE.put("AVERAGE", FunctionType.AVERAGE);
        STRING_TO_TYPE.put("PERCENT", FunctionType.PERCENT);

        TYPE_TO_FUNCTION.put(FunctionType.PLUS, (args) -> {
            double arg1 = toDouble(args[0]);
            double arg2 = toDouble(args[1]);
            return arg1 + arg2;
        });

        TYPE_TO_FUNCTION.put(FunctionType.MINUS, (args) -> {
            double arg1 = toDouble(args[0]);
            double arg2 = toDouble(args[1]);
            return arg1 - arg2;
        });

        TYPE_TO_FUNCTION.put(FunctionType.TIMES, (args) -> {
            double arg1 = toDouble(args[0]);
            double arg2 = toDouble(args[1]);
            return arg1 * arg2;
        });

        TYPE_TO_FUNCTION.put(FunctionType.DIVIDE, (args) -> {
            double arg1 = toDouble(args[0]);
            double arg2 = toDouble(args[1]);
            if (arg2 == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return arg1 / arg2;
        });

        TYPE_TO_FUNCTION.put(FunctionType.MOD, (args) -> {
            double arg1 = toDouble(args[0]);
            double arg2 = toDouble(args[1]);
            return arg1 % arg2;
        });

        TYPE_TO_FUNCTION.put(FunctionType.CONCAT, (args) -> {
            String str1 = args[0].toString();
            String str2 = args[1].toString();
            return str1 + str2;
        });

        TYPE_TO_FUNCTION.put(FunctionType.SUB, (args) -> {
            String str = args[0].toString();
            int start = toInt(args[1]);
            int end = toInt(args[2]);
            if (start < 0 || end > str.length() || start > end) {
                throw new IllegalArgumentException("Invalid substring indices");
            }
            return str.substring(start, end);
        });

        TYPE_TO_FUNCTION.put(FunctionType.REF, (args) -> {
            if (sheet == null) {
                throw new IllegalStateException("Sheet is not initialized");
            }
            String cellName = args[0].toString(); // Argument must be in "A1" format
            return sheet.getCellValue(cellName); // Fetch cell value using "A1" format
        });

        TYPE_TO_FUNCTION.put(FunctionType.SUM, (args) -> {
            if (args.length != 1 || !(args[0] instanceof String)) {
                throw new IllegalArgumentException("SUM requires a single range argument");
            }

            String range = args[0].toString();
            if(rangeNameToRange.containsKey(range)) {
                range = rangeNameToRange.get(range);
            }
            if (sheet == null) {
                throw new IllegalStateException("Sheet is not initialized");
            }

            return sheet.resolveRange(range).stream()
                    .mapToDouble(FunctionRegistry::toDouble) // Convert all values to double
                    .sum(); // Sum them up
        });

        TYPE_TO_FUNCTION.put(FunctionType.AVERAGE, (args) -> {
            if (args.length != 1 || !(args[0] instanceof String)) {
                throw new IllegalArgumentException("AVERAGE requires a single range argument");
            }

            String range = args[0].toString();
            if(rangeNameToRange.containsKey(range)) {
                range = rangeNameToRange.get(range);
            }
            if (sheet == null) {
                throw new IllegalStateException("Sheet is not initialized");
            }

            return sheet.resolveRange(range).stream()
                    .mapToDouble(FunctionRegistry::toDouble) // Convert all values to double
                    .average() // Compute the average
                    .orElse(0.0); // Return 0 if the range is empty
        });

        TYPE_TO_FUNCTION.put(FunctionType.PERCENT, (args) -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("PERCENT requires exactly 2 arguments");
            }
            double part = toDouble(args[0]);
            double whole = toDouble(args[1]);
            if (whole == 0) {
                throw new ArithmeticException("Division by zero in PERCENT function");
            }
            return (part / whole) * 100;
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

    private static double toDouble(Object arg) {
        if (arg instanceof Number) {
            return ((Number) arg).doubleValue();
        } else if (arg instanceof String) {
            try {
                return Double.parseDouble((String) arg);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Expected a numeric value but got: " + arg);
            }
        } else {
            throw new IllegalArgumentException("Cannot convert to double: " + arg);
        }
    }

    private static int toInt(Object arg) {
        double value = toDouble(arg);
        if (value % 1 != 0) {
            throw new IllegalArgumentException("Expected an integer value but got: " + value);
        }
        return (int) value;
    }

    public static void addRangeName(String rangeName, String from, String to) {
        String validRange = from + ":" + to;
        rangeNameToRange.put(rangeName, validRange);
    }

    @FunctionalInterface
    public interface FunctionHandler {
        Object execute(Object[] args);
    }
}
