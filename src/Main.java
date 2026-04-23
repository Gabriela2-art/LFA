import java.util.*;

public class Main {
    public static void main(String[] args) {
        Set<String> VN = new HashSet<>(Arrays.asList("S", "A", "B", "C", "D", "E"));
        Set<String> VT = new HashSet<>(Arrays.asList("a", "b"));
        Grammar g = new Grammar(VN, VT, "S");

        g.addProduction("S", "aB");
        g.addProduction("S", "AC");
        g.addProduction("A", "a");
        g.addProduction("A", "ASC");
        g.addProduction("A", "BC");
        g.addProduction("A", "aD");
        g.addProduction("B", "b");
        g.addProduction("B", "bS");
        g.addProduction("C", "ε");
        g.addProduction("C", "BA");
        g.addProduction("E", "aB");
        g.addProduction("D", "abC");

        CNFConverter converter = new CNFConverter();
        converter.convert(g);

    }
}