import java.util.*;
import java.util.stream.Collectors;

public class CNFConverter {

    private int varCount = 1;

    public void convert(Grammar g) {

        g.printGrammar("\nStep 0: Initial Grammar");

        eliminateEpsilon(g);
        g.printGrammar("\nStep 1: After ε-elimination");

        eliminateUnitProductions(g);
        g.printGrammar("\nStep 2: After Unit elimination");

        eliminateNonProductive(g);
        g.printGrammar("\nStep 3: After Non-productive removal");

        eliminateInaccessible(g);
        g.printGrammar("\nStep 4: After Inaccessible removal");

        replaceTerminals(g);
        g.printGrammar("\nStep 5: After Terminal replacement");

        splitLongRules(g);
        g.printGrammar("\nStep 6: After Splitting long rules");

        eliminateUnitProductions(g);
        g.printGrammar("\nStep 7: Final unit elimination");

        g.P = new ArrayList<>(new LinkedHashSet<>(g.P));

        g.printGrammar("\nFINAL CNF");
    }

    private void eliminateEpsilon(Grammar g) {
        Set<String> nullable = new HashSet<>();

        for (Production p : g.P) {
            if (p.getRightSide().equals("ε")) {
                nullable.add(p.getLeftSide());
            }
        }

        List<Production> newP = new ArrayList<>();

        for (Production p : g.P) {
            if (p.getRightSide().equals("ε")) continue;

            String rhs = p.getRightSide();
            List<String> results = generateCombinations(rhs, nullable);

            for (String r : results) {
                if (!r.isEmpty() && !r.equals("ε")) {
                    newP.add(new Production(p.getLeftSide(), r));
                }
            }
        }

        g.P = newP;
    }

    private List<String> generateCombinations(String rhs, Set<String> nullable) {
        List<String> res = new ArrayList<>();
        res.add("");

        for (char c : rhs.toCharArray()) {
            List<String> newRes = new ArrayList<>();
            for (String s : res) {
                newRes.add(s + c);
                if (nullable.contains(String.valueOf(c))) {
                    newRes.add(s);
                }
            }
            res = newRes;
        }

        return res.stream().distinct().collect(Collectors.toList());
    }

    private void eliminateUnitProductions(Grammar g) {
        boolean changed = true;

        while (changed) {
            changed = false;

            List<Production> units = g.P.stream()
                    .filter(p -> p.getRightSide().length() == 1 && g.VN.contains(p.getRightSide()))
                    .collect(Collectors.toList());

            for (Production u : units) {
                g.P.remove(u);

                for (Production p : new ArrayList<>(g.P)) {
                    if (p.getLeftSide().equals(u.getRightSide())) {
                        Production np = new Production(u.getLeftSide(), p.getRightSide());
                        if (!g.P.contains(np)) {
                            g.P.add(np);
                            changed = true;
                        }
                    }
                }
            }
        }
    }

    private void eliminateNonProductive(Grammar g) {
        Set<String> productive = new HashSet<>();

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Production p : g.P) {
                boolean ok = true;

                for (char c : p.getRightSide().toCharArray()) {
                    String s = String.valueOf(c);
                    if (!g.VT.contains(s) && !productive.contains(s)) {
                        ok = false;
                        break;
                    }
                }

                if (ok && productive.add(p.getLeftSide())) {
                    changed = true;
                }
            }
        }

        g.P.removeIf(p -> !productive.contains(p.getLeftSide()));
    }

    private void eliminateInaccessible(Grammar g) {
        Set<String> reach = new HashSet<>();
        reach.add(g.S);

        boolean changed = true;

        while (changed) {
            changed = false;

            for (Production p : g.P) {
                if (reach.contains(p.getLeftSide())) {
                    for (char c : p.getRightSide().toCharArray()) {
                        String s = String.valueOf(c);
                        if (g.VN.contains(s) && reach.add(s)) {
                            changed = true;
                        }
                    }
                }
            }
        }

        g.P.removeIf(p -> !reach.contains(p.getLeftSide()));
    }

    private void replaceTerminals(Grammar g) {
        Map<String, String> map = new HashMap<>();
        List<Production> newP = new ArrayList<>();

        for (Production p : g.P) {
            String rhs = p.getRightSide();

            if (rhs.length() > 1) {
                StringBuilder sb = new StringBuilder();

                for (char c : rhs.toCharArray()) {
                    String s = String.valueOf(c);

                    if (g.VT.contains(s)) {
                        String nt = "T" + s.toUpperCase(); // TA, TB
                        map.put(nt, s);
                        sb.append(nt);
                    } else {
                        sb.append(s);
                    }
                }

                newP.add(new Production(p.getLeftSide(), sb.toString()));
            } else {
                newP.add(p);
            }
        }

        for (var e : map.entrySet()) {
            g.VN.add(e.getKey());
            newP.add(new Production(e.getKey(), e.getValue()));
        }

        g.P = newP;
    }

    private void splitLongRules(Grammar g) {
        List<Production> res = new ArrayList<>();

        for (Production p : g.P) {
            String rhs = p.getRightSide();

            if (rhs.length() <= 2) {
                res.add(p);
                continue;
            }

            String current = p.getLeftSide();

            for (int i = 0; i < rhs.length() - 2; i++) {
                String newVar = "X" + varCount++;
                g.VN.add(newVar);

                res.add(new Production(current, rhs.charAt(i) + newVar));
                current = newVar;
            }

            res.add(new Production(current,
                    "" + rhs.charAt(rhs.length() - 2) + rhs.charAt(rhs.length() - 1)));
        }

        g.P = res;
    }
}