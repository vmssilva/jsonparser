package github.vmssilva.jsonparser.lexer;

public record Token(TokenType type, String lexeme, Object literal) {
}
