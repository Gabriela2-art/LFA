public class Main {

    public static void main(String[] args) {

        String text = "sin(3 + 4.5) * cos(2)";

        Lexer lexer = new Lexer(text);

        Token token = lexer.getNextToken();

        while (token.type != TokenType.EOF) {
            System.out.println(token);
            token = lexer.getNextToken();
        }
    }
}