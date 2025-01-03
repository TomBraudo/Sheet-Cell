package expression;


import sheet.Sheet;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private static final Map<String, FunctionType> STRING_TO_TYPE = new HashMap<>();
    private static final Map<FunctionType, FunctionHandler> TYPE_TO_FUNCTION = new HashMap<>();
    private static final Map<String, String> rangeNameToRange = new HashMap<>();
    private static Sheet sheet; // Reference to the current sheet

    private static boolean toBoolean(Object arg) {
        if (arg instanceof Boolean) {
            return (Boolean) arg;
        } else if (arg instanceof String) {
            String value = ((String) arg).toLowerCase();
            if (value.equals("true")) return true;
            if (value.equals("false")) return false;
            throw new IllegalArgumentException("Expected a boolean value but got: " + arg);
        } else {
            throw new IllegalArgumentException("Cannot convert to boolean: " + arg);
        }
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
        STRING_TO_TYPE.put("EQUAL", FunctionType.EQUAL);
        STRING_TO_TYPE.put("NOT", FunctionType.NOT);
        STRING_TO_TYPE.put("AND", FunctionType.AND);
        STRING_TO_TYPE.put("OR", FunctionType.OR);
        STRING_TO_TYPE.put("BIGGER", FunctionType.BIGGER);
        STRING_TO_TYPE.put("LESS", FunctionType.LESS);
        STRING_TO_TYPE.put("IF", FunctionType.IF);

        TYPE_TO_FUNCTION.put(FunctionType.PLUS, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                return arg1 + arg2;
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.MINUS, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                return arg1 - arg2;
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.TIMES, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                return arg1 * arg2;
            } catch (Exception e) {
                return ErrorType.NaN;
            }
        });

        TYPE_TO_FUNCTION.put(FunctionType.DIVIDE, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                if (arg2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return arg1 / arg2;
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.MOD, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                return arg1 % arg2;
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.CONCAT, (args) -> {
            try {
                String str1 = args[0].toString();
                String str2 = args[1].toString();
                return str1 + str2;
            } catch (Exception e) {
                return ErrorType.UNDEFINED;
            }
        });

        TYPE_TO_FUNCTION.put(FunctionType.SUB, (args) -> {
            try {
                String str = args[0].toString();
                int start = toInt(args[1]);
                int end = toInt(args[2]);
                if (start < 0 || end > str.length() || start > end) {
                    throw new IllegalArgumentException("Invalid substring indices");
                }
                return str.substring(start, end);
            } catch (Exception e) {
                return ErrorType.UNDEFINED;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.REF, (args) -> {
            try {
                String cellName = args[0].toString(); // Argument must be in "A1" format
                return sheet.getCellValue(cellName); // Fetch cell value using "A1" format
            } catch (Exception e) {
                return ErrorType.UNDEFINED;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.SUM, (args) -> {
            try {
                String range = args[0].toString();
                if(rangeNameToRange.containsKey(range)) {
                    range = rangeNameToRange.get(range);
                }

                return sheet.resolveRange(range).stream()
                        .mapToDouble(FunctionRegistry::toDouble) // Convert all values to double
                        .sum(); // Sum them up
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.AVERAGE, (args) -> {
            try {
                String range = args[0].toString();
                if(rangeNameToRange.containsKey(range)) {
                    range = rangeNameToRange.get(range);
                }

                return sheet.resolveRange(range).stream()
                        .mapToDouble(FunctionRegistry::toDouble) // Convert all values to double
                        .average() // Compute the average
                        .orElse(0.0); // Return 0 if the range is empty
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.PERCENT, (args) -> {
            try {
                double part = toDouble(args[0]);
                double whole = toDouble(args[1]);
                if (whole == 0) {
                    throw new ArithmeticException("Division by zero in PERCENT function");
                }
                return (part / whole) * 100;
            } catch (Exception e) {
                return ErrorType.NaN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.EQUAL, (args)->{
           Object arg1 = args[0];
           Object arg2 = args[1];
           return arg1.equals(arg2);
        });

        TYPE_TO_FUNCTION.put(FunctionType.NOT, (args) -> {
            try {
                boolean arg = toBoolean(args[0]);
                return !arg;
            } catch (Exception e) {
                return ErrorType.UNKNOWN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.BIGGER, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                return arg1 >= arg2;
            }catch (Exception e) {
                return ErrorType.UNKNOWN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.LESS, (args) -> {
            try {
                double arg1 = toDouble(args[0]);
                double arg2 = toDouble(args[1]);
                return arg1 <= arg2;
            } catch (Exception e) {
                return ErrorType.UNKNOWN;
            }

        });

        TYPE_TO_FUNCTION.put(FunctionType.OR, (args) -> {
            try {
                boolean arg1 = toBoolean(args[0]);
                boolean arg2 = toBoolean(args[1]);
                return arg1 || arg2;
            }catch (Exception e) {
                return ErrorType.UNKNOWN;
            }
        });

        TYPE_TO_FUNCTION.put(FunctionType.AND, (args) -> {
            try {
                boolean arg1 = toBoolean(args[0]);
                boolean arg2 = toBoolean(args[1]);
                return arg1 && arg2;
            } catch (Exception e) {
                return ErrorType.UNKNOWN;
            }
        });

        TYPE_TO_FUNCTION.put(FunctionType.IF, (args) -> {
            try {
                boolean condition = toBoolean(args[0]);
                return condition ? args[1] : args[2];
            } catch (Exception e) {
                return ErrorType.UNKNOWN;
            }
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


    public static void addRangeName(String rangeName, String from, String to) {
        String validRange = from + ":" + to;
        rangeNameToRange.put(rangeName, validRange);
    }

    @FunctionalInterface
    public interface FunctionHandler {
        Object execute(Object[] args);
    }
}
