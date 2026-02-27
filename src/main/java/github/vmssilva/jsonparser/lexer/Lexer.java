package github.vmssilva.jsonparser.lexer;

import java.util.ArrayList;
import java.util.List;
import github.vmssilva.jsonparser.lexer.exception.LexerException;

public final class Lexer {

  private final List<Token> tokens = new ArrayList<>();
  private final String source;
  private int start = 0;
  private int current = 0;
  private int line = 0;
  private int column = 0;

  public Lexer(String source) {
    this.source = source;
  }

  public Tokenizer tokenize() {

    while (!isAtEnd()) {
      start = current;
      scanTokens();
    }

    return new Tokenizer(tokens);
  }

  private void scanTokens() {
    char c = consume();

    switch (c) {
      case '{' -> addToken(TokenType.LBRACE, "{");
      case '}' -> addToken(TokenType.RBRACE, "}");
      case '[' -> addToken(TokenType.LBRACKET, "[");
      case ']' -> addToken(TokenType.RBRACKET, "]");
      case ':' -> addToken(TokenType.COLON, ":");
      case ',' -> addToken(TokenType.COMMA, ",");
      case '"' -> string();
      case ' ', '\r', '\t', '\n' -> {
        if (c == '\n') {
          this.line++;
          this.column = 0;
        }
      }
      default -> {
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          literal();
        } else {
          throw error("Unexpected character: " + c);
        }
      }
    }
  }

  private void string() {
    StringBuilder value = new StringBuilder();

    while (!isAtEnd() && peek() != '"') {

      if (peek() == '\\') {
        consume(); // Consuming escape backslash

        if (isAtEnd())
          throw error("Invalid escape sequence");

        char escaped = peek();

        switch (escaped) {
          case 'b' -> value.append('\b');
          case 'n' -> value.append('\n');
          case 'r' -> value.append('\r');
          case 't' -> value.append('\t');
          case 'f' -> value.append('\f');
          case '"' -> value.append('"');
          case '\\' -> value.append('\\');
          case '/' -> value.append('/');
          case 'u' -> {
            if (current + 4 > source.length()) {
              throw error("Invalid unicode escape sequence");
            }

            String hex = source.substring(current, current + 4);

            try {
              char unicodeChar = (char) Integer.parseInt(hex, 16);
              value.append(unicodeChar);
            } catch (NumberFormatException e) {
              throw error("Invalid unicode escape sequence");
            }

            current += 4; // consuming unicodes characteres
            column += 4; // Increment column for error handling control
          }
          default -> throw error("Invalid escape character");
        }
        if (escaped != 'u')
          consume();
      } else {
        value.append(consume());
      }
    }

    if (isAtEnd())
      throw error("Unterminated string");
    consume(); // consuming closing quotes
    addToken(TokenType.STRING, value.toString());
  }

  private void number() {

    String value = "";

    while (!isAtEnd() && isDigit(peek())) {
      consume();
    }

    if (!isAtEnd() && peek() == '.') {
      if (!isDigit(peekNext()))
        throw error("Unexpected character: " + peek());

      consume(); // consuming decimal point
      while (!isAtEnd() && isDigit(peek())) {
        consume();
      }
      value = this.source.substring(start, current);
      addToken(TokenType.NUMBER, value, Double.valueOf(value));
      return;
    }

    value = this.source.substring(start, current);
    addToken(TokenType.NUMBER, value, Integer.valueOf(value));
  }

  private void literal() {
    while (!isAtEnd() && isAlpha(peek())) {
      consume();
    }

    String value = this.source.substring(start, current);

    switch (value) {
      case "true" -> addToken(TokenType.TRUE, value, getBoolean(value));
      case "false" -> addToken(TokenType.FALSE, value, getBoolean(value));
      case "null" -> addToken(TokenType.NULL, value, null);
      default -> throw error("Unexpected literal value: " + value);
    }
  }

  private boolean getBoolean(String value) {
    return Boolean.valueOf(value);
  }

  private boolean isAlpha(char c) {
    return Character.isLetter(c);
  }

  private boolean isDigit(char ch) {
    return Character.isDigit(ch);
  }

  private boolean isAtEnd() {
    return this.current >= source.length();
  }

  private char consume() {
    if (isAtEnd())
      throw error("Character index out of bounds");
    this.column++;
    return this.source.charAt(this.current++);
  }

  private char peek() {
    return (isAtEnd()) ? '\0' : this.source.charAt(this.current);
  }

  private char peekNext() {
    if (this.current + 1 >= this.source.length())
      return '\0';

    return this.source.charAt(this.current + 1);
  }

  private void addToken(TokenType type, String lexeme) {
    addToken(type, lexeme, lexeme);
  }

  private void addToken(TokenType type, String lexeme, Object literal) {
    this.tokens.add(new Token(type, lexeme, literal));
  }

  private LexerException error(String message) {
    throw new LexerException(
        String.format(
            "%s at [%d, %d]", message, this.line, this.column),
        this.line, this.column);
  }

  public static class Tokenizer {
    private List<Token> tokens;
    private int pos;

    public Tokenizer(List<Token> tokens) {
      this.tokens = tokens;
      this.pos = 0;
    }

    public List<Token> toList() {
      return List.copyOf(this.tokens);
    }

    public boolean hasMoreTokens() {
      return this.pos < this.tokens.size();
    }

    public Token nextToken() {
      return (hasMoreTokens()) ? this.tokens.get(pos++) : null;
    }

    public Token getToken(int index) {
      return (index >= 0 && index < size()) ? this.tokens.get(index) : null;
    }

    public void setPos(int pos) {
      this.pos = pos;
    }

    public int getPos() {
      return this.pos;
    }

    public int size() {
      return this.tokens.size();
    }
  }
}
