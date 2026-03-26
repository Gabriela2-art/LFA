import java.util.*;

public class Main {

    public static void main(String[] args) {

        String r1 = "(a|b)(c|d)E+G?";
        String r2 = "P(Q|R|S)T(UV|W|X)*Z+";
        String r3 = "1(0|1)*2(3|4)^536";

        runRegex(r1);
        runRegex(r2);
        runRegex(r3);
    }

    public static void runRegex(String regex) {

        System.out.println("\nRegex: " + regex);

        RegexParser parser = new RegexParser(regex);
        List<Block> blocks = parser.parse();
        parser.explain(blocks);

        List<String> result = Generator.generate(blocks);

        System.out.println("Generated:");
        result.stream().limit(20).forEach(System.out::println);
    }
}