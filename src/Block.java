import java.util.List;

public class Block {
    List<String> options;
    String operator;
    int repeat;

    public Block(List<String> options, String operator, int repeat) {
        this.options = options;
        this.operator = operator;
        this.repeat = repeat;
    }

    @Override
    public String toString() {
        return options + " " + operator + " " + repeat;
    }
}