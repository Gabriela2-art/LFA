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
The converter also prints the grammar after each transformation step, allowing full traceability of the CNF conversion process.

Example: main conversion pipeline

```java
public void convert(Grammar g) {
    eliminateEpsilon(g);
    eliminateUnitProductions(g);
    eliminateNonProductive(g);
    eliminateInaccessible(g);
    replaceTerminals(g);
    splitLongRules(g);
    eliminateUnitProductions(g); // final cleanup

    g.P = new ArrayList<>(new LinkedHashSet<>(g.P));
}
```
1. Eliminate epsilon productions

```java
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
```
2. Eliminate unit productions

```java
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
```
3. Remove non-productive symbols

```java
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
```

5. Replace terminals in long productions

```java
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
```

6. Split long productions into binary form

```java
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
```

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

<img width="175" height="564" alt="image" src="https://github.com/user-attachments/assets/4c9ae0e5-ed09-4a4a-8c55-097db68d9b9e" /><img width="211" height="572" alt="image" src="https://github.com/user-attachments/assets/cc9034b7-7e1a-42b2-881a-f1ffe2332484" />
<img width="210" height="577" alt="image" src="https://github.com/user-attachments/assets/96a28dc3-b11f-46eb-ae0c-52a2cdaf75dc" />
<img width="179" height="575" alt="image" src="https://github.com/user-attachments/assets/2948f8e1-c218-46ce-8f52-3611da40a0d1" />
<img width="90" height="249" alt="image" src="https://github.com/user-attachments/assets/9d960ef4-4458-4f11-85da-5eaf4df4e003" />
<img width="145" height="462" alt="image" src="https://github.com/user-attachments/assets/13aaa274-c8fa-492f-962d-1fe24f2c3541" />

This screenshots demonstrate the execution of the program for a given context-free grammar. The program prints the grammar after each transformation step (Step 0 to Step 7), allowing full traceability of the CNF conversion process.

The "Step 0: Initial Grammar" section contains the original set of production rules, including epsilon productions, unit productions, and longer rules that do not satisfy CNF constraints.

Each subsequent step shows the intermediate transformations:
- epsilon productions are removed by generating all valid combinations
- unit productions are eliminated (both initially and in the final step)
- non-productive and inaccessible symbols are removed
- terminals in longer rules are replaced with new non-terminals (e.g., TA, TB)
- long productions are split into binary form using auxiliary symbols (e.g., X1, X2)

The "FINAL CNF" section illustrates the result after applying all transformation steps.

The output confirms that all production rules now follow the CNF format:
- A → BC
- A → a

Duplicate productions are removed to ensure a clean and minimal grammar.

Overall, the result validates that the implemented system successfully converts an arbitrary grammar into an equivalent CNF grammar while preserving the language.

## Conclusions

In this laboratory work, a system for transforming context-free grammars into Chomsky Normal Form (CNF) was implemented in Java. Instead of working with grammars in their original, potentially complex form, the program systematically converts them into a simplified and standardized structure.

The implementation supports all essential steps required for CNF conversion, including the elimination of epsilon productions, removal of unit productions, and filtering of non-productive and inaccessible symbols. Additionally, it handles the restructuring of productions by replacing terminals in longer rules and decomposing complex productions into binary form.

The solution is organized into multiple stages, each corresponding to a specific transformation step. This modular approach improves code clarity and makes the implementation easier to understand, debug, and extend. Each stage preserves the language of the grammar while gradually enforcing the constraints of CNF.

Auxiliary non-terminals (such as 𝑋𝑎, 𝑋𝑏, 𝑌1, 𝑌2) are introduced dynamically to ensure that all productions comply with CNF rules. Duplicate productions are also removed to maintain a clean and efficient grammar representation.

The execution results confirm that the program correctly transforms an arbitrary grammar into an equivalent CNF grammar. The final output demonstrates that all productions follow the required forms A→BC or A→a, validating the correctness of the implementation.

Overall, this laboratory work highlights the practical application of theoretical concepts from formal languages and finite automata. It demonstrates how grammars can be normalized programmatically and how abstract transformations can be implemented step by step in a real programming language. This provides a deeper understanding of grammar processing and its importance in areas such as parsing and compiler design.

## References

1. Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation

2. Peter Linz. Formal Languages and Automata

3. Aho A., Lam M., Sethi R., Ullman J. Compilers: Principles, Techniques, and Tools

4. Compilers: Principles, Techniques, and Tools (2nd edition). Alfred V. Aho, Monica S. Lam, Ravi Sethi and Jeff Ullman. Publisher: Addison Wesley, 2007
