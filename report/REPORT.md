# Regular Expressions

### Course: Formal Languages & Finite Automata
### Author: Botezatu Gabriela

----

## Theory
Regular expressions (regex) are a formal way of describing patterns in strings and are widely used in computer science for text processing, validation, lexical analysis, and pattern matching. A regular expression defines a set of valid strings using symbols and operators such as concatenation, alternation (`|`), and repetition operators like `*`, `+`, `?`, or exact repetition `^n`. Concatenation means that symbols appear one after another, alternation allows choosing between multiple options (for example `(a|b)`), `*` represents repetition from zero to many times, `+` means at least one occurrence, `?` makes an element optional, while `^n` specifies an exact number of repetitions.

Regular expressions are closely related to finite automata, as both are used to describe regular languages and can be transformed into one another. In this laboratory work, instead of simply verifying whether a string matches a given regular expression, the goal is to dynamically interpret the expression and generate valid strings that satisfy it.

The process begins with lexical analysis, where the input regular expression is read character by character and transformed into tokens. Each token represents a meaningful unit such as a character, operator, or parenthesis. Next, during parsing, these tokens are grouped into structured elements called blocks. Each block contains a set of possible options, an operator that defines how those options behave, and additional information for repetition when necessary.

After parsing, the program generates all valid combinations of strings using a backtracking approach. Each block is expanded according to its operator, and combinations are formed step by step. To prevent excessively large outputs, repetition operators such as `*` and `+` are limited to a maximum of five iterations. Finally, the main program executes the entire process for several regular expressions, optionally displaying the sequence of processing steps, and outputs a subset of the generated results.

This implementation demonstrates how regular expressions can be interpreted programmatically to produce valid strings, highlighting the connection between regex processing, parsing techniques, and combinatorial generation.


## Objectives:

The main objectives of this laboratory work are to understand the concept of regular expressions and their role in describing patterns within strings, as well as their connection to formal languages and finite automata. Another important goal is to learn how to interpret regular expressions dynamically, rather than treating them as fixed patterns.

The laboratory also focuses on understanding how a regular expression can be broken down into smaller components using lexical analysis and parsing techniques. This includes transforming the input expression into tokens and organizing them into structured elements that can be processed programmatically.

A key objective is to implement a system that generates valid strings based on given regular expressions. This involves handling operators such as alternation, concatenation, and repetition (`*`, `+`, `?`, and exact repetition), and ensuring that all combinations are constructed correctly.

Additionally, the work aims to demonstrate how backtracking can be used to explore and generate all valid combinations while applying constraints to avoid excessively large outputs.

Finally, the laboratory highlights the practical application of theoretical concepts by implementing the entire process in Java, showing how regular expressions can be parsed and used to generate valid strings in a structured and efficient way.




## Implementation description

The implementation is organized into several main components: TokenType, Token, RegexLexer, RegexParser, Block, Generator, and Main. Each component has a specific role in interpreting regular expressions and generating valid strings. The program is written in Java and does not rely on external libraries.

### TokenType enum

The TokenType enumeration defines all possible types of tokens that can be recognized from a regular expression. These include characters, numbers, operators, parentheses, and special symbols.

Example: token type definitions

```java
public enum TokenType {
    CHAR,
    LPAREN,
    RPAREN,
    OR,
    STAR,
    PLUS,
    QUESTION,
    POWER,
    NUMBER,
    EOF
}
```
This structure allows easy extension if new regex features are added.


### Token class

The Token class represents a single lexical unit extracted from the input regex. Each token contains its type and value.

Example: token representation

```java
public class Token {
    public TokenType type;
    public String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
}
```

This helps in passing structured data from the lexer to the parser.


### RegexLexer class

The RegexLexer is responsible for lexical analysis. It reads the regex character by character and converts it into tokens.

Example: advancing through input

```java
private void advance() {
    pos++;
    if (pos >= text.length()) currentChar = '\0';
    else currentChar = text.charAt(pos);
}
```
Example: token generation

```java
public Token getNextToken() {

    while (currentChar != '\0') {

        if (Character.isLetter(currentChar)) {
            char ch = currentChar;
            advance();
            return new Token(TokenType.CHAR, String.valueOf(ch));
        }

        if (Character.isDigit(currentChar)) {
            char ch = currentChar;
            advance();
            return new Token(TokenType.NUMBER, String.valueOf(ch));
        }
```
The lexer identifies symbols like (, |, *, +, ?, ^ and converts them into tokens.

### RegexParser class

The parser transforms tokens into structured elements called blocks. Each block contains:

- possible options (e.g. a|b)
- an operator (*, +, etc.)
- repetition value (for ^)

Example: parsing logic

```java
if (currentToken.type == TokenType.LPAREN) {
    eat(TokenType.LPAREN);

    while (currentToken.type != TokenType.RPAREN) {
        StringBuilder sb = new StringBuilder();

        while (currentToken.type == TokenType.CHAR || currentToken.type == TokenType.NUMBER) {
            sb.append(currentToken.value);
            eat(currentToken.type);
        }

        options.add(sb.toString());

        if (currentToken.type == TokenType.OR) {
            eat(TokenType.OR);
        }
    }

    eat(TokenType.RPAREN);
}
```
The parser also supports operators like +, *, ?, and ^.


### Block class

The Block class represents a unit of the regex after parsing.

Example: identifier detection

```java
public class Block {
    List<String> options;
    String operator;
    int repeat;
}
```
Each block stores the possible values and how they should be repeated.

### Generator class

The Generator creates all valid strings using backtracking. It expands each block depending on its operator.

Example: generation logic

```java
private static void backtrack(List<Block> blocks, int index, String current, List<String> result) {
    if (index == blocks.size()) {
        result.add(current);
        return;
    }

    Block block = blocks.get(index);
    List<String> expanded = expand(block);

    for (String s : expanded) {
        backtrack(blocks, index + 1, current + s, result);
    }
}
```
Example: handling repetition
```java
if (b.operator.equals("+")) {
    for (int i = 1; i <= 5; i++)
        for (String s : b.options)
            result.add(s.repeat(i));
}
```
Repetition is limited to maximum 5 times to avoid very large outputs.

### Main class

The Main class runs the program and demonstrates how regex expressions are processed.

Example: 

```java
String r1 = "(a|b)(c|d)E+G?";
runRegex(r1);
```
It:
- parses the regex
- prints processing steps (bonus)
- generates valid strings


### Program Execution Output

<img width="386" height="1037" alt="image" src="https://github.com/user-attachments/assets/33179f18-1247-4e9b-8c10-8d337f5be0be" />
<img width="367" height="1137" alt="image" src="https://github.com/user-attachments/assets/1f9d0bd3-d2f5-4f69-b72d-e2e749d1a46a" />
<img width="331" height="1175" alt="image" src="https://github.com/user-attachments/assets/66cb37c3-37aa-4097-aa1a-8d2d687457c0" />

This screenshot demonstrates the execution of the program for a given regular expression. It shows how the input regex is processed step by step, including the parsing into blocks and the interpretation of operators.

The "Processing Steps" section illustrates how each part of the regular expression is analyzed, including available options and applied operators.

The "Generated" section confirms that the program correctly produces valid strings based on the given regular expression. It also demonstrates that repetition operators are handled properly and limited to a maximum of five iterations.

Overall, the output validates that the implemented system successfully interprets regular expressions and generates correct combinations dynamically.


## Conclusions

In this laboratory work, a system for interpreting regular expressions and generating valid strings was implemented in Java. Instead of simply matching input strings against a pattern, the program dynamically processes regular expressions and constructs all possible valid combinations based on the defined rules.

The implementation supports essential regex features such as concatenation, alternation (`|`), optional elements (`?`), repetition (`*`, `+`), and exact repetition (`^n`). By analyzing the input expression step by step, the program demonstrates how regular expressions can be transformed into structured components and then expanded into valid outputs.

The solution is divided into multiple stages, including lexical analysis, parsing, and generation. The lexer converts the input regex into tokens, the parser organizes these tokens into blocks with specific behaviors, and the generator uses backtracking to produce all valid strings. This modular structure improves code clarity and makes the implementation easier to extend or modify.

A limitation was introduced for repetition operators, restricting them to a maximum of five iterations. This prevents the generation of excessively large outputs while still demonstrating the correct functionality of the operators.

The execution results confirm that the program correctly interprets regular expressions and generates valid strings according to the specified patterns. Additionally, the optional explanation feature provides insight into how the regex is processed step by step, improving understanding of the internal logic.

Overall, this laboratory work highlights the practical application of theoretical concepts from formal languages and finite automata. It demonstrates how regular expressions can be parsed and interpreted programmatically, and how backtracking can be used to generate combinations. This provides a deeper understanding of how pattern-based systems work and how they can be implemented in real-world programming scenarios.


## References

1. Hopcroft E. and others. Introduction to Automata Theory, Languages and Computation

2. Peter Linz. Formal Languages and Automata

3. Aho A., Lam M., Sethi R., Ullman J. Compilers: Principles, Techniques, and Tools

4. Compilers: Principles, Techniques, and Tools (2nd edition). Alfred V. Aho, Monica S. Lam, Ravi Sethi and Jeff Ullman. Publisher: Addison Wesley, 2007
