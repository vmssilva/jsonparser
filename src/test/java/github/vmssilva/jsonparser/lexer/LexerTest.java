package github.vmssilva.jsonparser.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import github.vmssilva.jsonparser.lexer.Lexer.Tokenizer;
import github.vmssilva.jsonparser.lexer.exception.LexerException;

public class LexerTest {

  Lexer lexer;
  Tokenizer tokenizer;
  String json = """
      {
        "movies": [
          {
            "id": 1,
            "title": "The Godfather",
            "year": 1972,
            "genre": ["Crime", "Drama"],
            "director": "Francis Ford Coppola",
            "duration_minutes": 175
          },
          {
            "id": 2,
            "title": "Interstellar",
            "year": 2014,
            "genre": ["Science Fiction", "Drama"],
            "director": "Christopher Nolan",
            "duration_minutes": 169
          }
        ]
      }""";

  @BeforeEach
  void init() {
    lexer = new Lexer(json);
    tokenizer = lexer.tokenize();
  }

  @Test
  void shouldAddAllTokensCorrectly() {
    assertEquals(65, tokenizer.size());
  }

  @Test
  void shouldReturnNullForInvalidIndices() {
    assertEquals(null, tokenizer.getToken(65));
    assertEquals(null, tokenizer.getToken(-1));
  }

  @Test
  void shouldWorkWithEscapedCharacters() {
    var data = "{ \"message\" : \"\\\"Hello\\\"\" }";
    assertEquals("\"Hello\"", new Lexer(data).tokenize().getToken(3).lexeme());
  }

  @Test
  void shouldStoreLiteralTypesCorrectly() {
    var data = "{\"name\" : \"Jane\", \"age\": 34, \"height\": 1.65, \"active\": true, \"expired\": false, \"site\": null}";
    var lexer = new Lexer(data);
    var tokenizer = lexer.tokenize();

    assertEquals(true, (tokenizer.getToken(7).literal() instanceof Integer));
    assertEquals(true, (tokenizer.getToken(11).literal() instanceof Double));
    assertEquals(true, (tokenizer.getToken(4).literal() instanceof String));
    assertEquals(true, tokenizer.getToken(15).literal());
    assertEquals(false, tokenizer.getToken(19).literal());
    assertEquals(null, tokenizer.getToken(23).literal());
  }

  @Test
  void shouldParseSingleUnicodeCharacter() {
    var data = "{ \"char\": \"\u0041\" }"; // Unicode para 'A'
    var lexer = new Lexer(data);
    Tokenizer tokenizer = lexer.tokenize();

    // O token da string deve ter lexeme "A"
    assertEquals("A", tokenizer.getToken(3).lexeme());
  }

  @Test
  void shouldParseMultipleUnicodeCharacters() {
    var data = "{ \"text\": \"\u0048\u0065\u006C\u006C\u006F\" }"; // "Hello"
    var lexer = new Lexer(data);
    Tokenizer tokenizer = lexer.tokenize();

    assertEquals("Hello", tokenizer.getToken(3).lexeme());
  }

  @Test
  void shouldThrowErrorForInvalidUnicodeSequence() {
    var data = "{ \"bad\": \"\\u12G4\" }"; // 'G' não é hexadecimal
    var lexer = new Lexer(data);

    assertThrows(LexerException.class, lexer::tokenize);
  }

  @Test
  void shouldThrowErrorForIncompleteUnicodeSequence() {
    var data = "{ \"short\": \"\\u123\" }"; // só 3 dígitos
    var lexer = new Lexer(data);

    assertThrows(LexerException.class, lexer::tokenize);
  }

  @Test
  void shouldParseUnicodeWithOtherEscapeSequences() {
    var data = "{ \"mixed\": \"Line1\\n\u0042\" }"; // "\nB"
    var lexer = new Lexer(data);
    Tokenizer tokenizer = lexer.tokenize();

    assertEquals("Line1\nB", tokenizer.getToken(3).lexeme());
  }

}
