public class Parser {

    private Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    private void eat(TokenType type) {
        if (currentToken.type == type) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException("Expected " + type + " but got " + currentToken.type);
        }
    }

    private AST factor() {
        Token token = currentToken;

        if (token.type == TokenType.INTEGER || token.type == TokenType.FLOAT) {
            eat(token.type);
            return new Num(token);
        }

        if (token.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN);
            AST node = expr();
            eat(TokenType.RPAREN);
            return node;
        }

        if (token.type == TokenType.SIN || token.type == TokenType.COS) {
            eat(token.type);
            eat(TokenType.LPAREN);
            AST node = expr();
            eat(TokenType.RPAREN);
            return new UnaryOp(token, node);
        }

        throw new RuntimeException("Invalid factor");
    }

    private AST term() {
        AST node = factor();

        while (currentToken.type == TokenType.MULTIPLY ||
               currentToken.type == TokenType.DIVIDE) {

            Token token = currentToken;

            if (token.type == TokenType.MULTIPLY) {
                eat(TokenType.MULTIPLY);
            } else {
                eat(TokenType.DIVIDE);
            }

            node = new BinOp(node, token, factor());
        }

        return node;
    }

    public AST expr() {
        AST node = term();

        while (currentToken.type == TokenType.PLUS ||
               currentToken.type == TokenType.MINUS) {

            Token token = currentToken;

            if (token.type == TokenType.PLUS) {
                eat(TokenType.PLUS);
            } else {
                eat(TokenType.MINUS);
            }

            node = new BinOp(node, token, term());
        }

        return node;
    }

    public AST parse() {
        return expr();
    }
}