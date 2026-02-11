import java.util.*;

public class Grammar {
    private Set<String> VN;  
    private Set<String> VT;   
    private Map<String, List<String>> P; 
    private String S; 
    private Random random = new Random();

    public Grammar(Set<String> VN, Set<String> VT, Map<String, List<String>> P, String S) {
        this.VN = VN;
        this.VT = VT;
        this.P = P;
        this.S = S;
    }

    public String generateString() {
        String current = S;
        boolean done = false;

        while (!done) {
            done = true;
            for (String nt : VN) {
                if (current.contains(nt)) {
                    done = false;
                    List<String> productions = P.get(nt);
                    String chosen = productions.get(random.nextInt(productions.size()));
                    current = current.replaceFirst(nt, chosen);
                    break;
                }
            }
        }
        return current;
    }

    public FiniteAutomaton toFiniteAutomaton() {
        return FiniteAutomaton.fromGrammar(this);
    }

    public Set<String> getVN() {
        return VN;
    }

    public Set<String> getVT() {
        return VT;
    }

    public Map<String, List<String>> getP() {
        return P;
    }

    public String getS() {
        return S;
    }
}
