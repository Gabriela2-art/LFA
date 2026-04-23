import java.util.*;
import java.util.stream.Collectors;

public class CNFConverter {

    private int varCount = 1;

    private Map<String, String> terminalMap = new HashMap<>();
    private Map<String, String> pairMap = new HashMap<>();

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
        splitLongRules(g);

        g.P = new ArrayList<>(new LinkedHashSet<>(g.P));
        g.printGrammar("\nStep 5: Chomsky Normal Form");
    }

    private void eliminateEpsilon(Grammar g) {
        Set<String> nullable = new HashSet<>();

        for (Production p : g.P) {
            if (p.getRightSide().equals("ε")) {
                nullable.add(p.getLeftSide());
            }
        }

        Set<Production> newP = new LinkedHashSet<>();

        for (Production p : g.P) {
            if (p.getRightSide().equals("ε")) continue;

            List<String> symbols = splitSymbols(p.getRightSide());
            List<List<String>> combos = generate(symbols, nullable);

            for (List<String> c : combos) {
                if (!c.isEmpty()) {
                    newP.add(new Production(p.getLeftSide(), String.join("", c)));
                }
            }
        }

        g.P = new ArrayList<>(newP);
    }

    private List<List<String>> generate(List<String> symbols, Set<String> nullable) {
        List<List<String>> res = new ArrayList<>();
        res.add(new ArrayList<>());

        for (String s : symbols) {
            List<List<String>> newRes = new ArrayList<>();

            for (List<String> curr : res) {
                List<String> with = new ArrayList<>(curr);
                with.add(s);
                newRes.add(with);

                if (nullable.contains(s)) {
                    newRes.add(new ArrayList<>(curr));
                }
            }

            res = newRes;
        }

        return res.stream().distinct().collect(Collectors.toList());
    }

    private void eliminateUnitProductions(Grammar g) {

        Map<String, Set<String>> unitGraph = new HashMap<>();

        for (String v : g.VN) {
            unitGraph.put(v, new HashSet<>());
            unitGraph.get(v).add(v);
        }

        boolean changed = true;

        while (changed) {
            changed = false;

            for (Production p : g.P) {
                List<String> rhs = splitSymbols(p.getRightSide());

                if (rhs.size() == 1 && g.VN.contains(rhs.get(0))) {
                    String A = p.getLeftSide();
                    String B = rhs.get(0);

                    if (unitGraph.get(A).addAll(unitGraph.get(B))) {
                        changed = true;
                    }
                }
            }
        }

        Set<Production> newP = new LinkedHashSet<>();

        for (String A : g.VN) {
            for (String B : unitGraph.get(A)) {
                for (Production p : g.P) {
                    List<String> rhs = splitSymbols(p.getRightSide());

                    if (p.getLeftSide().equals(B)) {
                        if (!(rhs.size() == 1 && g.VN.contains(rhs.get(0)))) {
                            newP.add(new Production(A, p.getRightSide()));
                        }
                    }
                }
            }
        }

        g.P = new ArrayList<>(newP);
    }

    private void eliminateNonProductive(Grammar g) {
        Set<String> productive = new HashSet<>();

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Production p : g.P) {
                List<String> rhs = splitSymbols(p.getRightSide());

                boolean ok = true;
                for (String s : rhs) {
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
                    for (String s : splitSymbols(p.getRightSide())) {
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
        List<Production> newP = new ArrayList<>();

        for (Production p : g.P) {
            List<String> rhs = splitSymbols(p.getRightSide());

            if (rhs.size() > 1) {
                List<String> newRhs = new ArrayList<>();

                for (String s : rhs) {
                    if (g.VT.contains(s)) {

                        String var = terminalMap.get(s);

                        if (var == null) {
                            var = "Y" + varCount++;
                            terminalMap.put(s, var);
                            g.VN.add(var);

                            newP.add(new Production(var, s));
                        }

                        newRhs.add(var);
                    } else {
                        newRhs.add(s);
                    }
                }

                newP.add(new Production(p.getLeftSide(), String.join("", newRhs)));
            } else {
                newP.add(p);
            }
        }

        g.P = new ArrayList<>(new LinkedHashSet<>(newP));
    }

    private String getOrCreatePair(String pair, Grammar g, List<Production> newProductions) {
        if (pairMap.containsKey(pair)) {
            return pairMap.get(pair);
        }

        String var = "X" + varCount++;
        pairMap.put(pair, var);
        g.VN.add(var);

        Production newP = new Production(var, pair);

        if (!newProductions.contains(newP)) {
            newProductions.add(newP);
        }

        return var;
    }

    private void splitLongRules(Grammar g) {
        List<Production> newProductions = new ArrayList<>();

        for (Production p : g.P) {
            List<String> rhs = splitSymbols(p.getRightSide());

            if (rhs.size() <= 2) {
                newProductions.add(p);
                continue;
            }

            List<String> symbols = new ArrayList<>(rhs);

            while (symbols.size() > 2) {

                String last = symbols.get(symbols.size() - 1);
                String secondLast = symbols.get(symbols.size() - 2);

                String pair = secondLast + last;
                String newVar = getOrCreatePair(pair, g, newProductions);

                symbols.remove(symbols.size() - 1);
                symbols.remove(symbols.size() - 1);
                symbols.add(newVar);
            }

            newProductions.add(
                new Production(p.getLeftSide(), String.join("", symbols))
            );
        }

        g.P = newProductions;
    }

    private List<String> splitSymbols(String rhs) {
    List<String> symbols = new ArrayList<>();

    for (int i = 0; i < rhs.length(); i++) {
        char c = rhs.charAt(i);

        if (Character.isUpperCase(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            i++;

            while (i < rhs.length() && Character.isDigit(rhs.charAt(i))) {
                sb.append(rhs.charAt(i));
                i++;
            }

            i--; 
            symbols.add(sb.toString());
        }
        else {
            symbols.add(String.valueOf(c));
        }
    }

    return symbols;
}
}