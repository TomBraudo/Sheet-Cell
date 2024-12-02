public class Main {
    public static void main(String[] args) {
        Expression ex1 = new MathematicalExpression("PLUS", 1, 2);
        Expression ex2 = new MathematicalExpression("MINUS", ex1, 2);
        System.out.println(ex2.evaluate());
    }
}
