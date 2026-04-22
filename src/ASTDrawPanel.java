import javax.swing.*;
import java.awt.*;

public class ASTDrawPanel extends JPanel {

    private AST root;

    public ASTDrawPanel(AST root) {
        this.root = root;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        drawNode(g2, root, getWidth() / 2, 60, getWidth() / 4);
    }

    private void drawNode(Graphics2D g, AST node, int x, int y, int offset) {

        String text = getNodeText(node);
        int radius = 22;

        if (node instanceof BinOp) {
            g.setColor(new Color(70, 130, 180)); 
        }
        else if (node instanceof UnaryOp) {
            g.setColor(new Color(255, 140, 0)); 
        }
        else if (node instanceof Num) {
            g.setColor(new Color(34, 139, 34)); 
        }
        else {
            g.setColor(Color.GRAY);
        }

        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        g.setColor(Color.BLACK);
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        g.drawString(text, x - textWidth / 2, y + textHeight / 4);

        if (node instanceof BinOp) {
            BinOp bin = (BinOp) node;

            g.setColor(Color.BLACK);
            g.drawLine(x, y + radius, x - offset, y + 70 - radius);
            drawNode(g, bin.left, x - offset, y + 70, offset / 2);

            g.setColor(Color.BLACK);
            g.drawLine(x, y + radius, x + offset, y + 70 - radius);
            drawNode(g, bin.right, x + offset, y + 70, offset / 2);
        }

        else if (node instanceof UnaryOp) {
            UnaryOp un = (UnaryOp) node;

            g.setColor(Color.BLACK);
            g.drawLine(x, y + radius, x, y + 70 - radius);
            drawNode(g, un.expr, x, y + 70, offset / 2);
        }
    }

    private String getNodeText(AST node) {

        if (node instanceof BinOp) {
            return ((BinOp) node).op.value;
        }

        if (node instanceof UnaryOp) {
            return ((UnaryOp) node).op.value;
        }

        if (node instanceof Num) {
            return String.valueOf(((Num) node).value);
        }

        return "?";
    }
}