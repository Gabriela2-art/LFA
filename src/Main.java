import java.util.*;

public class Main {
    public static void main(String[] args) {
        Set<String> VN = new HashSet<>(Arrays.asList("S", "P", "Q"));
        Set<String> VT = new HashSet<>(Arrays.asList("a", "b", "c", "d", "e", "f"));

        Map<String, List<String>> P = new HashMap<>();
        P.put("S", Arrays.asList("aP", "bQ"));
        P.put("P", Arrays.asList("bP", "cP", "dQ", "e"));
        P.put("Q", Arrays.asList("eQ", "fQ", "a"));

        Grammar grammar = new Grammar(VN, VT, P, "S");

        System.out.println("Generated strings:");
        for (int i = 0; i < 5; i++) {
            String w = grammar.generateString();
            System.out.println(w);
        }

        FiniteAutomaton fa = grammar.toFiniteAutomaton();

        System.out.println("\nTesting strings:");
        String[] tests = {"ae", "be", "abbe", "df", "a"};
        for (String t : tests) {
            System.out.println(t + " -> " + fa.stringBelongToLanguage(t));
        }
    }
}
