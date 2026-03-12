import java.util.*;

public class Main {
    public static void main(String[] args) {

        Set<String> Q = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        Set<String> Sigma = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> F = new HashSet<>(Arrays.asList("q2"));

        Map<String, Map<String, Set<String>>> delta = new HashMap<>();

        addTransition(delta, "q0", "a", "q0");
        addTransition(delta, "q0", "a", "q1");
        addTransition(delta, "q1", "c", "q1");
        addTransition(delta, "q1", "b", "q2");
        addTransition(delta, "q2", "b", "q3");
        addTransition(delta, "q3", "a", "q1");

        FiniteAutomaton ndfa = new FiniteAutomaton(Q, Sigma, delta, "q0", F);

        System.out.println("=== Original Automaton (NDFA) ===");
        System.out.println(ndfa);
        System.out.println("Is deterministic? " + ndfa.isDeterministic());

        FiniteAutomaton dfa = ndfa.toDFA();
        System.out.println("\n=== Converted DFA ===");
        System.out.println(dfa);
        System.out.println("Is deterministic? " + dfa.isDeterministic());

        Grammar g = ndfa.toRegularGrammar();
        System.out.println("\n=== Regular Grammar from FA ===");
        System.out.println(g);
        System.out.println("Chomsky classification: " + g.classifyChomsky());

        System.out.println("\n=== Testing strings on DFA ===");
        String[] tests = {"a", "ab", "acb", "b", "aacb"};
        for (String t : tests) {
            System.out.println(t + " -> " + dfa.stringBelongToLanguage(t));
        }
    }

    private static void addTransition(Map<String, Map<String, Set<String>>> delta,
                                      String from, String sym, String to) {
        delta.putIfAbsent(from, new HashMap<>());
        delta.get(from).putIfAbsent(sym, new HashSet<>());
        delta.get(from).get(sym).add(to);
    }
}