import java.lang.Math.*;

public class SupportedFunctions {

    public abstract class Expression {
        public abstract Object evaluate();
    }

    public class MathematicalExpression extends Expression {
        private String functionName;
        private Expression[] arguments;
        public MathematicalExpression(String functionName, Expression... arguments) {
            this.functionName = functionName;
            this.arguments = arguments;
        }

        @Override
        public Object evaluate() {
            Double[] values = new Double[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                values[i] = (Double)arguments[i].evaluate();
            }

            switch (functionName) {
                case "PLUS":
                    return values[0] + values[1];
                case "MINUS":
                    return values[0] - values[1];
                case "TIMES":
                    return values[0] * values[1];
                case "DIVIDE":
                    if (values[1] == 0) throw new ArithmeticException("Division by zero");
                    return values[0] / values[1];
                case "MOD":
                    return values[0] % values[1];
                default:
                    throw new IllegalArgumentException("Unknown function: " + functionName);
            }
        }
    }
    public class StringExpression extends Expression {
        private final String functionName;
        private final Expression[] arguments;

        public StringExpression(String functionName, Expression... arguments) {
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

        private String concat(Expression[] args) {
            if (args.length != 2) {
                throw new IllegalArgumentException("CONCAT requires exactly 2 arguments.");
            }
            String str1 = (String) args[0].evaluate();
            String str2 = (String) args[1].evaluate();
            return str1 + str2;
        }

        private String substring(Expression[] args) {
            if (args.length != 3) {
                throw new IllegalArgumentException("SUB requires exactly 3 arguments.");
            }
            String source = (String) args[0].evaluate();
            int startIndex = ((Number) args[1].evaluate()).intValue();
            int endIndex = ((Number) args[2].evaluate()).intValue();
            if (startIndex < 0 || endIndex > source.length() || startIndex > endIndex) {
                return "UNDEFINED!";
            }
            return source.substring(startIndex, endIndex);
        }
    }
}
