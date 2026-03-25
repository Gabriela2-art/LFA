public class RegexLexer {

    private String text;
    private int pos;
    private char currentChar;

    public RegexLexer(String text) {
        this.text = text;
        this.pos = 0;
        this.currentChar = text.charAt(pos);
    }

    private void advance() {
        pos++;
        if (pos >= text.length()) currentChar = '\0';
        else currentChar = text.charAt(pos);
    }

    public Token getNextToken() {

        while (currentChar != '\0') {

            if (Character.isLetter(currentChar)) {
                char ch = currentChar;
                advance();
                return new Token(TokenType.CHAR, String.valueOf(ch));
            }

            if (Character.isDigit(currentChar)) {
                char ch = currentChar;
                advance();
                return new Token(TokenType.NUMBER, String.valueOf(ch));
            }

            if (currentChar == '(') { advance(); return new Token(TokenType.LPAREN, "("); }
            if (currentChar == ')') { advance(); return new Token(TokenType.RPAREN, ")"); }
            if (currentChar == '|') { advance(); return new Token(TokenType.OR, "|"); }
            if (currentChar == '*') { advance(); return new Token(TokenType.STAR, "*"); }
            if (currentChar == '+') { advance(); return new Token(TokenType.PLUS, "+"); }
            if (currentChar == '?') { advance(); return new Token(TokenType.QUESTION, "?"); }
            if (currentChar == '^') { advance(); return new Token(TokenType.POWER, "^"); }

            throw new RuntimeException("Invalid char: " + currentChar);
        }

        return new Token(TokenType.EOF, "");
    }
}