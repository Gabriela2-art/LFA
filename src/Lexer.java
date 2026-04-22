public class Lexer {

    private String text;
    private int pos;
    private char currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
        this.currentChar = text.charAt(pos);
    }

    private void advance() {
        pos++;

        if (pos >= text.length()) {
            currentChar = '\0';
        } else {
            currentChar = text.charAt(pos);
        }
    }

    private void skipWhitespace() {
        while (currentChar != '\0' &&
                String.valueOf(currentChar).matches("\\s")) {
            advance();
        }
    }

    private Token number() {
        StringBuilder result = new StringBuilder();

        while (currentChar != '\0' &&
                String.valueOf(currentChar).matches("[0-9\\.]")) {

            result.append(currentChar);
            advance();
        }

        if (result.toString().contains(".")) {
            return new Token(TokenType.FLOAT, result.toString());
        }

        return new Token(TokenType.INTEGER, result.toString());
    }

    private Token identifier() {
        StringBuilder result = new StringBuilder();

        while (currentChar != '\0' &&
                String.valueOf(currentChar).matches("[a-zA-Z]")) {

            result.append(currentChar);
            advance();
        }

        String value = result.toString();

        if (value.equals("sin"))
            return new Token(TokenType.SIN, value);

        if (value.equals("cos"))
            return new Token(TokenType.COS, value);

        throw new RuntimeException("Unknown identifier: " + value);
    }

    public Token getNextToken() {

        while (currentChar != '\0') {

            if (String.valueOf(currentChar).matches("\\s")) {
                skipWhitespace();
                continue;
            }

            if (String.valueOf(currentChar).matches("[0-9]"))
                return number();

            if (String.valueOf(currentChar).matches("[a-zA-Z]"))
                return identifier();

            if (currentChar == '+') {
                advance();
                return new Token(TokenType.PLUS, "+");
            }

            if (currentChar == '-') {
                advance();
                return new Token(TokenType.MINUS, "-");
            }

            if (currentChar == '*') {
                advance();
                return new Token(TokenType.MULTIPLY, "*");
            }

            if (currentChar == '/') {
                advance();
                return new Token(TokenType.DIVIDE, "/");
            }

            if (currentChar == '(') {
                advance();
                return new Token(TokenType.LPAREN, "(");
            }

            if (currentChar == ')') {
                advance();
                return new Token(TokenType.RPAREN, ")");
            }

            throw new RuntimeException("Invalid character: " + currentChar);
        }

        return new Token(TokenType.EOF, null);
    }
}