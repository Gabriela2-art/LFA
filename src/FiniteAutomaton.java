import java.util.*;

public class FiniteAutomaton {
    private Set<String> Q; 
    private Set<String> Sigma; 
    private Map<String, Map<String, Set<String>>> delta; 
    private String q0; 
    private Set<String> F; 

    public FiniteAutomaton(Set<String> Q, Set<String> Sigma,
                           Map<String, Map<String, Set<String>>> delta,
                           String q0, Set<String> F) {
        this.Q = Q;
        this.Sigma = Sigma;
        this.delta = delta;
        this.q0 = q0;
        this.F = F;
    }

    public Set<String> getQ() { return Q; }
    public Set<String> getSigma() { return Sigma; }
    public String getQ0() { return q0; }
    public Set<String> getF() { return F; }

    public boolean isDeterministic() {
        for (String state : Q) {
            Map<String, Set<String>> trans = delta.get(state);
            if (trans == null) continue;
            for (String sym : trans.keySet()) {
                if (trans.get(sym).size() > 1) {
                    return false; 
                }
            }
        }
        return true;
    }

    public boolean stringBelongToLanguage(String w) {
        Set<String> current = new HashSet<>();
        current.add(q0);

        for (int i = 0; i < w.length(); i++) {
            String sym = String.valueOf(w.charAt(i));
            Set<String> next = new HashSet<>();
            for (String st : current) {
                if (delta.containsKey(st) && delta.get(st).containsKey(sym)) {
                    next.addAll(delta.get(st).get(sym));
                }
            }
            current = next;
            if (current.isEmpty()) return false;
        }

        for (String st : current) {
            if (F.contains(st)) return true;
        }
        return false;
    }

    public Grammar toRegularGrammar() {
        Set<String> VN = new HashSet<>(Q);
        Set<String> VT = new HashSet<>(Sigma);
        Map<String, List<String>> P = new HashMap<>();

        for (String q : Q) {
            P.putIfAbsent(q, new ArrayList<>());
            if (delta.containsKey(q)) {
                for (String a : delta.get(q).keySet()) {
                    for (String q2 : delta.get(q).get(a)) {
                        P.get(q).add(a + q2);
                        if (F.contains(q2)) {
                            P.get(q).add(a);
                        }
                    }
                }
            }
        }

        return new Grammar(VN, VT, P, q0);
    }

    public FiniteAutomaton toDFA() {
        Set<Set<String>> dfaStates = new HashSet<>();
        Map<Set<String>, Map<String, Set<String>>> dfaDelta = new HashMap<>();
        Queue<Set<String>> queue = new LinkedList<>();

        Set<String> start = new HashSet<>();
        start.add(q0);

        queue.add(start);
        dfaStates.add(start);

        while (!queue.isEmpty()) {
            Set<String> current = queue.poll();
            dfaDelta.putIfAbsent(current, new HashMap<>());

            for (String sym : Sigma) {
                Set<String> next = new HashSet<>();
                for (String st : current) {
                    if (delta.containsKey(st) && delta.get(st).containsKey(sym)) {
                        next.addAll(delta.get(st).get(sym));
                    }
                }

                if (!next.isEmpty()) {
                    dfaDelta.get(current).put(sym, next);
                    if (!dfaStates.contains(next)) {
                        dfaStates.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        Set<String> newQ = new HashSet<>();
        Map<String, Map<String, Set<String>>> newDelta = new HashMap<>();
        Set<String> newF = new HashSet<>();

        Map<Set<String>, String> nameMap = new HashMap<>();
        int idx = 0;
        for (Set<String> s : dfaStates) {
            String name = "D" + idx++;
            nameMap.put(s, name);
            newQ.add(name);
        }

        for (Set<String> s : dfaStates) {
            String fromName = nameMap.get(s);
            newDelta.putIfAbsent(fromName, new HashMap<>());

            for (String f : F) {
                if (s.contains(f)) {
                    newF.add(fromName);
                    break;
                }
            }

            Map<String, Set<String>> trans = dfaDelta.get(s);
            if (trans != null) {
                for (String sym : trans.keySet()) {
                    Set<String> toSet = trans.get(sym);
                    String toName = nameMap.get(toSet);
                    newDelta.get(fromName).put(sym, new HashSet<>(Arrays.asList(toName)));
                }
            }
        }

        String newStart = nameMap.get(start);

        return new FiniteAutomaton(newQ, Sigma, newDelta, newStart, newF);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("States: ").append(Q).append("\n");
        sb.append("Alphabet: ").append(Sigma).append("\n");
        sb.append("Start: ").append(q0).append("\n");
        sb.append("Final: ").append(F).append("\n");
        sb.append("Transitions:\n");
        for (String s : delta.keySet()) {
            for (String a : delta.get(s).keySet()) {
                sb.append("  ").append(s).append(" --").append(a).append("--> ")
                  .append(delta.get(s).get(a)).append("\n");
            }
        }
        return sb.toString();
    }
}