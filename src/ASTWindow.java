import javax.swing.*;

public class ASTWindow {

    public static void show(AST tree) {

        JFrame frame = new JFrame("AST Visualization");

        ASTDrawPanel panel = new ASTDrawPanel(tree);

        frame.add(panel);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}