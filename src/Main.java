public class Main {

    public static void main(String[] args) {

        String text = "sin(3 + 4.5) * cos(2)";

        Lexer lexer = new Lexer(text);
        Parser parser = new Parser(lexer);

        AST tree = parser.parse();

        ASTWindow.show(tree);
    }
}