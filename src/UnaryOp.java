public class UnaryOp extends AST {

    public Token op;
    public AST expr;

    public UnaryOp(Token op, AST expr) {
        this.op = op;
        this.expr = expr;
    }
}