public class BinOp extends AST {

    public AST left;
    public Token op;
    public AST right;

    public BinOp(AST left, Token op, AST right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }
}