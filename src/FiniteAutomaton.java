import java.util.*;

public class FiniteAutomaton {
    private Set<String> Q; 
    private Set<String> Sigma; 
    private Map<String, Map<String, String>> delta; 
    private String q0; 
    private Set<String> F; 

    public FiniteAutomaton(Set<String> Q, Set<String> Sigma, Map<String, Map<String, String>> delta, String q0, Set<String> F) {
        this.Q = Q;
        this.Sigma = Sigma;
        this.delta = delta;
        this.q0 = q0;
        this.F = F;
    }

    public boolean stringBelongToLanguage(String input) {
        String currentState = q0;

        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            if (!delta.containsKey(currentState)) return false;
            if (!delta.get(currentState).containsKey(symbol)) return false;

            currentState = delta.get(currentState).get(symbol);
        }

        return F.contains(currentState);
    }

    public static FiniteAutomaton fromGrammar(Grammar g) {
        Set<String> Q = new HashSet<>(g.getVN());
        String finalState = "FINAL";
        Q.add(finalState);

        Set<String> Sigma = new HashSet<>(g.getVT());
        Map<String, Map<String, String>> delta = new HashMap<>();

        Set<String> F = new HashSet<>();
        F.add(finalState);

        for (String state : Q) {
            delta.put(state, new HashMap<>());
        }

        for (Map.Entry<String, List<String>> entry : g.getP().entrySet()) {
            String left = entry.getKey();
            for (String right : entry.getValue()) {
                if (right.length() == 1) {
                    String terminal = right;
                    delta.get(left).put(terminal, finalState);
                } else if (right.length() == 2) {
                    String terminal = String.valueOf(right.charAt(0));
                    String nextState = String.valueOf(right.charAt(1));
                    delta.get(left).put(terminal, nextState);
                }
            }
        }
        return new FiniteAutomaton(Q, Sigma, delta, g.getS(), F);
    }
}
