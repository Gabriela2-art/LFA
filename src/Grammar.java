import java.util.*;

public class Grammar {
    private Set<String> VN; // Non-terminals
    private Set<String> VT; // Terminals
    private Map<String, List<String>> P; // Productions
    private String S; // Start symbol

    public Grammar(Set<String> VN, Set<String> VT, Map<String, List<String>> P, String S) {
        this.VN = VN;
        this.VT = VT;
        this.P = P;
        this.S = S;
    }

    public Set<String> getVN() { return VN; }
    public Set<String> getVT() { return VT; }
    public Map<String, List<String>> getP() { return P; }
    public String getS() { return S; }

    // Generate a random string (simple derivation)
    public String generateString() {
        Random rnd = new Random();
        String current = S;

        for (int steps = 0; steps < 10; steps++) {
            boolean replaced = false;
            for (String nt : VN) {
                if (current.contains(nt)) {
                    List<String> prods = P.get(nt);
                    if (prods == null || prods.isEmpty()) continue;
                    String chosen = prods.get(rnd.nextInt(prods.size()));
                    current = current.replaceFirst(nt, chosen);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) break;
        }

        // remove non-terminals if any left
        for (String nt : VN) {
            current = current.replace(nt, "");
        }
        return current;
    }

    // Classify grammar in Chomsky hierarchy (simplified)
    public String classifyChomsky() {
        // We assume productions are of form A -> aB or A -> a or A -> ε
        boolean isRegular = true;

        for (Map.Entry<String, List<String>> e : P.entrySet()) {
            for (String rhs : e.getValue()) {
                if (rhs.length() == 0) continue; // epsilon
                if (rhs.length() == 1) {
                    // must be terminal
                    if (!VT.contains(rhs)) {
                        isRegular = false;
                    }
                } else if (rhs.length() == 2) {
                    String a = String.valueOf(rhs.charAt(0));
                    String B = String.valueOf(rhs.charAt(1));
                    if (!VT.contains(a) || !VN.contains(B)) {
                        isRegular = false;
                    }
                } else {
                    isRegular = false;
                }
            }
        }

        if (isRegular) return "Type 3 (Regular Grammar)";
        return "Type 2 or lower (Context-Free or more general)";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VN = ").append(VN).append("\n");
        sb.append("VT = ").append(VT).append("\n");
        sb.append("S = ").append(S).append("\n");
        sb.append("Productions:\n");
        for (Map.Entry<String, List<String>> e : P.entrySet()) {
            sb.append(e.getKey()).append(" -> ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }
}