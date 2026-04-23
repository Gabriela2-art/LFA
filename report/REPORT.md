# Chomsky Normal Form

### Course: Formal Languages & Finite Automata
### Author: Botezatu Gabriela

----

## Theory
Chomsky Normal Form (CNF) is a simplified and highly structured way of representing context-free grammars (CFGs). It is a fundamental concept in formal language theory because any context-free grammar can be transformed into an equivalent CNF grammar without changing the language it defines.

A grammar is in CNF if all its production rules adhere to one of two strict formats. The first type is binary non-terminal rules, where a non-terminal symbol produces exactly two non-terminal symbols (e.g.,A→BC). The second type is terminal rules, where a non-terminal symbol produces exactly one terminal symbol (e.g.,A→a).

The transformation process involves a series of systematic steps designed to “clean” the grammar and standardize its rules. First, epsilon (ϵ) productions are eliminated by redistributing the rules of nullable symbols. Second, unit productions (renaming rules like A→B) are removed to ensure direct derivation.

Third, non-productive and inaccessible symbols are deleted to keep the grammar efficient and focused only on symbols that contribute to valid strings reachable from the start symbol.

The final stage of normalization ensures that terminal symbols do not mix with non-terminals in longer rules and that rules with more than two non-terminals are broken down into a binary chain. This is achieved by introducing auxiliary non-terminals (such as 𝑋𝑎 for terminals and 𝑌𝑛 for longer sequences).

This binary structure is particularly useful in computer science for parsing algorithms, such as the CYK (Cocke–Younger–Kasami) algorithm, which relies on the predictable nature of CNF to determine whether a string belongs to a language in polynomial time.

## Objectives:

The main objectives of this laboratory work are to understand the concept of Chomsky Normal Form (CNF) and its role in simplifying context-free grammars, as well as its connection to formal languages and finite automata. CNF provides a standardized way of representing grammars, making them easier to analyze and use in algorithms such as parsing.

Another important goal is to learn how to transform a context-free grammar dynamically, rather than treating it as a fixed structure. This involves applying a sequence of normalization steps that preserve the language while modifying the form of the productions.

The laboratory also focuses on understanding how a grammar can be broken down and simplified using formal techniques. This includes eliminating epsilon productions, removing unit productions, and filtering out non-productive and inaccessible symbols, resulting in a cleaner and more structured grammar.

A key objective is to implement a system that converts any given grammar into Chomsky Normal Form. This involves handling transformations such as replacing terminals in longer productions and decomposing complex rules into binary ones, ensuring that all resulting productions follow the strict CNF format.

Additionally, the work aims to demonstrate how step-by-step transformations can be applied systematically, ensuring correctness at each stage while avoiding redundant or invalid productions.

Finally, the laboratory highlights the practical application of theoretical concepts by implementing the entire normalization process in Java, showing how grammars can be processed, transformed, and validated in a structured and efficient way.


## Implementation description

The implementation is organized into several main components: Grammar, Production, CNFConverter, and Main. Each component has a specific role in representing the grammar and transforming it into Chomsky Normal Form (CNF). The program is written in Java and does not rely on external libraries.

### Grammar class

The Grammar class defines the structure of a context-free grammar. It stores non-terminals, terminals, productions, and the start symbol.

Example: grammar structure and methods

```java
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
```
This class allows easy construction and visualization of grammars.


### Production class

The Production class represents a rule of the grammar. It ensures proper comparison and avoids duplicates.

Example: production implementation

```java
public class Production {
    private String leftSide;
    private String rightSide;

    public Production(String leftSide, String rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public String getRightSide() {
        return rightSide;
    }

    @Override
    public String toString() {
        return leftSide + " -> " + rightSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Production)) return false;
        Production p = (Production) o;
        return leftSide.equals(p.leftSide) &&
               rightSide.equals(p.rightSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftSide, rightSide);
    }
}
```

### CNFConverter class

This is the core class that performs the transformation into CNF. The conversion is done step-by-step inside a single method.  
The converter prints the grammar after each transformation step (Step 0 to Step 5), allowing full traceability of the CNF conversion process.

Example: main conversion pipeline

```java
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
```

The conversion process is executed step by step, and the grammar is printed after each stage.
Unlike simpler implementations, no final unit elimination step is required.


1. Eliminate epsilon productions
```java
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
```

2. Eliminate unit productions
```java
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
```

3. Remove non-productive symbols
```java
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
```

4. Remove inaccessible symbols
```java
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
```

5. Replace terminals in long productions
```java
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
```

Terminals appearing in productions with length greater than one are replaced using dynamically generated non-terminals (Y1, Y2, …).
A mapping structure ensures that duplicates are not created.

6. Split long productions into binary form
```java
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
```

Long productions are transformed into binary form while reusing existing pairs to avoid duplicates.

7. Symbol parsing
```java
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
```

This method ensures correct handling of multi-character non-terminals such as X1 or Y2.

### Main class

The Main class demonstrates the entire process of transforming a grammar into Chomsky Normal Form.

Example:

```java
Grammar g = new Grammar(VN, VT, "S");

g.addProduction("S", "aB");
g.addProduction("C", "ε");

CNFConverter converter = new CNFConverter();
converter.convert(g);
```
It:
- defines the initial grammar (non-terminals, terminals, productions)
- prints the original grammar
- applies CNF conversion step by step
- prints the final normalized grammar


### Program Execution Output

<img width="186" height="383" alt="image" src="https://github.com/user-attachments/assets/38b60cf7-3256-4aba-9f68-34d20dbf82ab" />
<img width="200" height="306" alt="image" src="https://github.com/user-attachments/assets/c6b46003-4476-4a34-8442-5faf5cac7cd2" />
<img width="218" height="582" alt="image" src="https://github.com/user-attachments/assets/d7cb8c03-d9a6-42ef-b9d5-6fed2fcc50f1" />
<img width="182" height="333" alt="image" src="https://github.com/user-attachments/assets/ca510de9-1fe8-4343-82a1-3d89a108bc3d" />


These screenshots demonstrate the execution of the program for a given context-free grammar. The program prints the grammar after each transformation step (Step 0 to Step 5), allowing full traceability of the CNF conversion process.


The "Step 0: Initial Grammar" section contains the original set of production rules, including epsilon productions, unit productions, and longer rules that do not satisfy CNF constraints.

Each subsequent step shows the intermediate transformations:
- epsilon productions are removed by generating all valid combinations
- unit productions are eliminated
- non-productive and inaccessible symbols are removed
- terminals in longer rules are replaced with dynamically generated non-terminals (e.g., Y1, Y2)
- long productions are split into binary form using auxiliary symbols (e.g., X1, X2), while avoiding duplicate variables

The "Step 5: Chomsky Normal Form" section illustrates the result after applying all transformation steps.

The output confirms that all production rules now follow the CNF format:
- A → BC
- A → a

Duplicate productions are removed to ensure a clean and minimal grammar.

Overall, the result validates that the implemented system successfully converts an arbitrary grammar into an equivalent CNF grammar while preserving the language.

## Conclusions
In this laboratory work, a system for transforming context-free grammars into Chomsky Normal Form (CNF) was implemented in Java. Instead of working with grammars in their original, potentially complex form, the program systematically converts them into a simplified and standardized structure.

The implementation supports all essential steps required for CNF conversion, including the elimination of epsilon productions, removal of unit productions, and filtering of non-productive and inaccessible symbols. Additionally, it handles the restructuring of productions by replacing terminals in longer rules and decomposing complex productions into binary form.

The solution is organized into multiple stages, each corresponding to a specific transformation step. This modular approach improves code clarity and makes the implementation easier to understand, debug, and extend. Each stage preserves the language of the grammar while gradually enforcing the constraints of CNF.

Auxiliary non-terminals (such as X1, X2, Y1, Y2) are introduced dynamically to ensure that all productions comply with CNF rules. The implementation also avoids duplicate productions and redundant variables by using mapping structures and set-based filtering.

The execution results confirm that the program correctly transforms an arbitrary grammar into an equivalent CNF grammar. The final output demonstrates that all productions follow the required forms A→BC or A→a, validating the correctness of the implementation.

Overall, this laboratory work highlights the practical application of theoretical concepts from formal languages and finite automata. It demonstrates how grammars can be normalized programmatically and how abstract transformations can be implemented step by step in a real programming language. This provides a deeper understanding of grammar processing and its importance in areas such as parsing and compiler design.

## References

1. Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation

2. Peter Linz. Formal Languages and Automata

3. Aho A., Lam M., Sethi R., Ullman J. Compilers: Principles, Techniques, and Tools

4. Compilers: Principles, Techniques, and Tools (2nd edition). Alfred V. Aho, Monica S. Lam, Ravi Sethi and Jeff Ullman. Publisher: Addison Wesley, 2007
