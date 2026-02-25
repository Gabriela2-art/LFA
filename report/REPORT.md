# NFA to DFA Conversion

### Course: Formal Languages & Finite Automata
### Author: Botezatu Gabriela

----

## Theory
A finite automaton is a mathematical model used to describe and recognize patterns in strings over a finite alphabet. It consists of a finite set of states, an input alphabet, a transition function, an initial state, and a set of final states. The automaton processes an input string symbol by symbol and changes its state according to the transition function. If after reading the entire string the automaton ends in a final state, the string is accepted; otherwise, it is rejected.

There are two main types of finite automata: deterministic (DFA) and non-deterministic (NDFA). In a deterministic finite automaton, for each state and each input symbol there is exactly one possible transition. In contrast, a non-deterministic finite automaton may have multiple possible transitions for the same state and input symbol. Although NDFA and DFA have different structures, they are equivalent in expressive power, meaning that for every NDFA there exists an equivalent DFA that recognizes the same language.

Another way to describe regular languages is by using regular grammars. There is a direct correspondence between finite automata and regular grammars: each transition in an automaton can be transformed into a production rule in a grammar. Regular grammars belong to Type 3 in the Chomsky hierarchy, which classifies grammars based on their generative power and structural restrictions.


## Objectives:

The main objectives of this laboratory work are:

- To understand the concept of finite automata and the difference between deterministic and non-deterministic automata.

- To determine whether a given finite automaton is deterministic or non-deterministic.

- To implement the conversion from an NDFA to an equivalent DFA using the subset construction method.

- To implement the conversion from a finite automaton to a regular grammar.

- To classify the obtained grammar according to the Chomsky hierarchy.

- To demonstrate the functionality of the implementation using a concrete automaton variant.


## Implementation description

The implementation is organized into three main classes: FiniteAutomaton, Grammar, and Main. Each class has a clear responsibility: modeling the automaton, modeling the grammar, and demonstrating the functionality of the application. The program is written in Java and does not use external libraries for automata or grammar processing.


### FiniteAutomaton class

The FiniteAutomaton class models both deterministic and non-deterministic finite automata by storing the set of states, the alphabet, the transition function, the initial state, and the set of final states. The transition function is implemented using a map that allows multiple destination states for the same state and symbol, which makes it suitable for representing an NDFA. This class also contains the logic for checking determinism, converting an NDFA to a DFA, converting an automaton to a regular grammar, and testing whether a string is accepted by the automaton.
Example: checking if the automaton is deterministic

```
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
```
This method iterates through all states and symbols and verifies that each transition has at most one destination state. If any transition has more than one possible destination, the automaton is classified as non-deterministic.



### NDFA to DFA conversion

The conversion from NDFA to DFA is implemented using the subset construction algorithm. In this approach, each state of the DFA represents a set of states of the original NDFA, and new states are generated iteratively by following transitions for each input symbol. The resulting automaton is guaranteed to be deterministic and equivalent to the original NDFA.
Example: creating new DFA states using subset construction

```
Set<String> start = new HashSet<>();
start.add(q0);

queue.add(start);
dfaStates.add(start);
```

The algorithm starts from the initial state set and uses a queue to explore and generate all reachable composite states.


### Conversion from Finite Automaton to Regular Grammar

The method toRegularGrammar() implements the standard transformation from a finite automaton to a regular grammar. For each transition of the form qi --a--> qj, a production rule Qi → aQj is created, and if qj is a final state, an additional production Qi → a is added. In this way, the obtained grammar generates the same language as the automaton.
Example: building productions from transitions

```
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
```


### Grammar class

The Grammar class stores the set of non-terminals, the set of terminals, the start symbol, and the production rules. It also provides a method for classifying the grammar according to the Chomsky hierarchy by checking whether all productions have the form of a regular grammar. Additionally, a simple string generation method is included to demonstrate derivations from the grammar.
Example: Chomsky classification

```
public String classifyChomsky() {
    boolean isRegular = true;

    for (Map.Entry<String, List<String>> e : P.entrySet()) {
        for (String rhs : e.getValue()) {
            if (rhs.length() == 1) {
                if (!VT.contains(rhs)) isRegular = false;
            } else if (rhs.length() == 2) {
                String a = String.valueOf(rhs.charAt(0));
                String B = String.valueOf(rhs.charAt(1));
                if (!VT.contains(a) || !VN.contains(B)) isRegular = false;
            } else {
                isRegular = false;
            }
        }
    }

    if (isRegular) return "Type 3 (Regular Grammar)";
    return "Type 2 or lower (Context-Free or more general)";
}
```
This method analyzes the right-hand side of each production and determines whether the grammar satisfies the constraints of a regular (Type 3) grammar.



### Main class

The Main class is used to demonstrate the functionality of the project. It constructs the finite automaton corresponding to the given variant, checks whether it is deterministic, converts it to a DFA, converts the automaton to a regular grammar, and classifies the grammar according to the Chomsky hierarchy. Finally, it tests several input strings on the obtained DFA to validate the correctness of the implementation.
Example: main method

```
public static void main(String[] args) {
    FiniteAutomaton ndfa = new FiniteAutomaton(Q, Sigma, delta, "q0", F);

    System.out.println("Is deterministic? " + ndfa.isDeterministic());

    FiniteAutomaton dfa = ndfa.toDFA();
    System.out.println("Is deterministic? " + dfa.isDeterministic());

    Grammar g = ndfa.toRegularGrammar();
    System.out.println("Chomsky classification: " + g.classifyChomsky());
}
```

This part of the code shows how the implemented methods are called and how the results are displayed to the user.


### Program Execution Output

<img width="410" height="1002" alt="image" src="https://github.com/user-attachments/assets/3615fa7d-bf5b-42ef-95fc-ddba1313261a" />
<img width="374" height="237" alt="image" src="https://github.com/user-attachments/assets/c83fcdfe-a403-45ac-afa8-2182faa1a84a" />

This screenshot shows the execution of the application, including the original NDFA, the converted DFA, the generated regular grammar, the Chomsky classification, and the results of testing several input strings.


## Conclusions

In this laboratory work, a non-deterministic finite automaton was analyzed and successfully transformed into an equivalent deterministic finite automaton using the subset construction method. The implementation demonstrated that, although the initial automaton contained non-deterministic transitions, it can be converted into a deterministic model that recognizes the same language.

Furthermore, the finite automaton was converted into a regular grammar, proving the theoretical equivalence between finite automata and regular grammars. The obtained grammar was analyzed and classified as a Type 3 grammar in the Chomsky hierarchy, which is consistent with the theory of formal languages.

The experimental results, including the determinism check and the testing of several input strings, confirm the correctness of the implemented algorithms. Overall, this work illustrates the practical application of theoretical concepts such as determinism, automata conversion, and grammar classification within a concrete software implementation.


## References

1. Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation

2. Peter Linz. Formal Languages and Automata

3. Pitts M. Andrew. Regular Languages and Finite Automata

4. Compilers: Principles, Techniques, and Tools (2nd edition). Alfred V. Aho, Monica S. Lam, Ravi Sethi and Jeff Ullman. Publisher: Addison Wesley, 2007
