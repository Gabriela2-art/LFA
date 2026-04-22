public class Num extends AST {

    public Token token;
    public double value;

    public Num(Token token) {
        this.token = token;
        this.value = Double.parseDouble(token.value);
    }
}