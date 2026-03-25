import java.util.*;

public class Generator {

    public static List<String> generate(List<Block> blocks) {
        List<String> result = new ArrayList<>();
        backtrack(blocks, 0, "", result);
        return result;
    }

    private static void backtrack(List<Block> blocks, int index, String current, List<String> result) {
        if (index == blocks.size()) {
            result.add(current);
            return;
        }

        Block block = blocks.get(index);
        List<String> expanded = expand(block);

        for (String s : expanded) {
            backtrack(blocks, index + 1, current + s, result);
        }
    }

    private static List<String> expand(Block b) {
        List<String> result = new ArrayList<>();

        if (b.operator.equals("")) return b.options;

        if (b.operator.equals("?")) {
            result.add("");
            result.addAll(b.options);
        }

        if (b.operator.equals("+")) {
            for (int i = 1; i <= 5; i++)
                for (String s : b.options)
                    result.add(s.repeat(i));
        }

        if (b.operator.equals("*")) {
            result.add("");
            for (int i = 1; i <= 5; i++)
                for (String s : b.options)
                    result.add(s.repeat(i));
        }

        if (b.operator.equals("^")) {
            for (String s : b.options)
                result.add(s.repeat(b.repeat));
        }

        return result;
    }
}