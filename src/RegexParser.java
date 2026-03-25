import java.util.*;

public class RegexParser {

    private RegexLexer lexer;
    private Token currentToken;

    public RegexParser(String text) {
        lexer = new RegexLexer(text);
        currentToken = lexer.getNextToken();
    }

    private void eat(TokenType type) {
        if (currentToken.type == type) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken.type);
        }
    }

    public List<Block> parse() {
        List<Block> blocks = new ArrayList<>();

        while (currentToken.type != TokenType.EOF) {

            List<String> options = new ArrayList<>();
            String operator = "";
            int repeat = 0;

            if (currentToken.type == TokenType.LPAREN) {
                eat(TokenType.LPAREN);

                while (currentToken.type != TokenType.RPAREN) {

                    StringBuilder sb = new StringBuilder();

                    while (currentToken.type == TokenType.CHAR || currentToken.type == TokenType.NUMBER) {
                        sb.append(currentToken.value);
                        eat(currentToken.type);
                    }

                    options.add(sb.toString());

                    if (currentToken.type == TokenType.OR) {
                        eat(TokenType.OR);
                    }
                }

                eat(TokenType.RPAREN);
            }

            else {
                StringBuilder sb = new StringBuilder();

                while (currentToken.type == TokenType.CHAR || currentToken.type == TokenType.NUMBER) {
                    sb.append(currentToken.value);
                    eat(currentToken.type);
                }

                options.add(sb.toString());
            }

            if (currentToken.type == TokenType.PLUS) {
                operator = "+";
                eat(TokenType.PLUS);
            }
            else if (currentToken.type == TokenType.STAR) {
                operator = "*";
                eat(TokenType.STAR);
            }
            else if (currentToken.type == TokenType.QUESTION) {
                operator = "?";
                eat(TokenType.QUESTION);
            }
            else if (currentToken.type == TokenType.POWER) {
                eat(TokenType.POWER);
                operator = "^";
                repeat = Integer.parseInt(currentToken.value);
                eat(TokenType.NUMBER);
            }

            blocks.add(new Block(options, operator, repeat));
        }

        return blocks;
    }

    public void explain(List<Block> blocks) {
        System.out.println("Processing Steps");
        int step = 1;

        for (Block b : blocks) {
            System.out.println("Step " + step++);
            System.out.println("Options: " + b.options);
            System.out.println("Operator: " + b.operator);

            if (b.operator.equals("^")) {
                System.out.println("Repeat exactly: " + b.repeat);
            }

            System.out.println();
        }
    }
}