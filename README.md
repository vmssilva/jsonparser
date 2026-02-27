# JSON Lexer in Java

A simple **JSON lexer/tokenizer** implemented in Java for **study purposes**. This project is designed to help understand **lexer logic, tokenization, and escape sequence handling** in JSON strings.

> ⚠️ This project is intended for learning and experimentation, not for production use.

---

## Purpose

* Practice Java programming concepts like `enum`, `record`, and exception handling.
* Learn how lexers tokenize strings and maintain line/column tracking.
* Understand parsing of escape sequences, including Unicode (`\uXXXX`) and other standard JSON escapes.
* Experiment with error handling for invalid inputs in a controlled way.

---

## Features

* Tokenizes JSON objects and arrays (`{}`, `[]`)
* Handles strings with escapes:

  * `\"`, `\\`, `\/`, `\b`, `\f`, `\n`, `\r`, `\t`
  * Unicode escapes: `\uXXXX`
* Parses numbers (integers and floating-point)
* Parses literals: `true`, `false`, `null`
* Maintains line and column information for error reporting
* Provides a `Tokenizer` class for sequential access to tokens
* Throws descriptive errors for:

  * Unterminated strings
  * Invalid escape sequences
  * Invalid Unicode sequences
  * Unexpected characters

---

## Usage

```java
import github.vmssilva.jsonparser.lexer.Lexer;
import github.vmssilva.jsonparser.lexer.Lexer.Tokenizer;
import github.vmssilva.jsonparser.lexer.Token;

public class Main {
    public static void main(String[] args) {
        String json = "{ \"name\": \"Jane\", \"age\": 34, \"active\": true }";
        Lexer lexer = new Lexer(json);
        Tokenizer tokenizer = lexer.tokenize();

        while (tokenizer.hasMoreTokens()) {
            Token token = tokenizer.nextToken();
            System.out.println(token.type() + " -> " + token.lexeme());
        }
    }
}
```

**Example Output:**

```
LBRACE -> {
STRING -> name
COLON -> :
STRING -> Jane
COMMA -> ,
STRING -> age
COLON -> :
NUMBER -> 34
COMMA -> ,
STRING -> active
COLON -> :
TRUE -> true
RBRACE -> }
```

---

## Tests

JUnit 5 tests cover:

* Token counting
* Handling invalid token indices
* Escaped characters in strings
* Literal types (Integer, Double, Boolean, null)
* Unicode characters
* Invalid and incomplete Unicode sequences
* Mixed escape sequences

> These tests help verify lexer behavior while experimenting with JSON parsing.

---

## Error Handling

The lexer throws a `LexerException` for:

* Unexpected characters
* Invalid or incomplete escape sequences
* Unterminated strings

Example:

```java
try {
    Lexer lexer = new Lexer("{ \"bad\": \"\\u12G4\" }");
    lexer.tokenize();
} catch (LexerException e) {
    System.out.println(e.getMessage()); // prints error with line/column info
}
```

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/vmssilva/jsonparser.git
```

2. Add the source files to your Java project:

```
github/vmssilva/jsonparser/lexer/
```

3. Run the tests with JUnit 5.

---

## License

MIT License – free to use and modify for learning or personal projects.
