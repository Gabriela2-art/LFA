import java.util.*;

public class Grammar {
    public Set<String> VN;
    public Set<String> VT;
    public List<Production> P;
    public String S;

    public Grammar(Set<String> VN, Set<String> VT, String S) {
        this.VN = new HashSet<>(VN);
        this.VT = new HashSet<>(VT);
        this.S = S;
        this.P = new ArrayList<>();
    }

    public void addProduction(String left, String right) {
        P.add(new Production(left, right));
    }

    public void printGrammar(String message) {
        System.out.println(message);
        Set<String> printed = new LinkedHashSet<>();
        for (Production prod : P) {
            if (printed.add(prod.toString())) {
                System.out.println(prod);
            }
        }
    }
}