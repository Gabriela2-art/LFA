import java.util.*;
import java.util.stream.Collectors;

public class CNFConverter {

    public void convert(Grammar g) {
        eliminateEpsilon(g);
        eliminateUnitProductions(g);
        eliminateNonProductive(g);
        eliminateInaccessible(g);
        replaceTerminalsInLongRules(g);
        splitLongProductions(g);
        g.P = new ArrayList<>(new LinkedHashSet<>(g.P));
    }

    private void eliminateEpsilon(Grammar g) {
        Set<String> nullable = g.P.stream()
                .filter(p -> p.getRightSide().equals("ε"))
                .map(Production::getLeftSide)
                .collect(Collectors.toSet());

        List<Production> newProds = new ArrayList<>();
        for (Production p : g.P) {
            if (p.getRightSide().equals("ε")) continue;
            newProds.add(p);
            for (String n : nullable) {
                if (p.getRightSide().contains(n)) {
                    String simplified = p.getRightSide().replace(n, "");
                    if (!simplified.isEmpty()) newProds.add(new Production(p.getLeftSide(), simplified));
                }
            }
        }
        g.P = new ArrayList<>(new LinkedHashSet<>(newProds));
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
                        if (!g.P.contains(np)) { g.P.add(np); changed = true; }
                    }
                }
            }
        }
    }

    private void eliminateNonProductive(Grammar g) {
        Set<String> productive = new HashSet<>(g.VT);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Production p : g.P) {
                boolean allProd = true;
                for (char c : p.getRightSide().toCharArray()) {
                    if (!productive.contains(String.valueOf(c)) && !g.VT.contains(String.valueOf(c))) {
                        allProd = false; break;
                    }
                }
                if (allProd && productive.add(p.getLeftSide())) changed = true;
            }
        }
        g.P.removeIf(p -> !productive.contains(p.getLeftSide()) || 
            p.getRightSide().chars().anyMatch(c -> !productive.contains(String.valueOf((char)c)) && !g.VT.contains(String.valueOf((char)c))));
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
                        if (g.VN.contains(String.valueOf(c)) && reach.add(String.valueOf(c))) changed = true;
                    }
                }
            }
        }
        g.P.removeIf(p -> !reach.contains(p.getLeftSide()));
    }

    private void replaceTerminalsInLongRules(Grammar g) {
        Map<String, String> tMap = new HashMap<>();
        List<Production> nps = new ArrayList<>();
        for (Production p : g.P) {
            String rhs = p.getRightSide();
            if (rhs.length() > 1) {
                StringBuilder sb = new StringBuilder();
                for (char c : rhs.toCharArray()) {
                    String s = String.valueOf(c);
                    if (g.VT.contains(s)) {
                        String nt = "X" + s;
                        tMap.put(nt, s);
                        sb.append(nt);
                    } else sb.append(s);
                }
                nps.add(new Production(p.getLeftSide(), sb.toString()));
            } else nps.add(p);
        }
        tMap.forEach((nt, t) -> { g.VN.add(nt); nps.add(new Production(nt, t)); });
        g.P = nps;
    }

    private void splitLongProductions(Grammar g) {
        List<Production> res = new ArrayList<>();
        int count = 1;
        for (Production p : g.P) {
            List<String> syms = parseSymbols(p.getRightSide(), g.VN);
            if (syms.size() > 2) {
                String cur = p.getLeftSide();
                for (int i = 0; i < syms.size() - 2; i++) {
                    String nnt = "Y" + count++;
                    g.VN.add(nnt);
                    res.add(new Production(cur, syms.get(i) + nnt));
                    cur = nnt;
                }
                res.add(new Production(cur, syms.get(syms.size()-2) + syms.get(syms.size()-1)));
            } else res.add(p);
        }
        g.P = res;
    }

    private List<String> parseSymbols(String rhs, Set<String> vn) {
        List<String> s = new ArrayList<>();
        int i = 0;
        while (i < rhs.length()) { 
            if (i + 1 < rhs.length() && vn.contains(rhs.substring(i, i + 2))) {
                s.add(rhs.substring(i, i + 2)); i += 2;
            } else { s.add(rhs.substring(i, i + 1)); i += 1; }
        }
        return s;
    }
}