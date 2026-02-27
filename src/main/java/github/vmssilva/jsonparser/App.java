package github.vmssilva.jsonparser;

import github.vmssilva.jsonparser.lexer.Lexer;

public class App {
  public static void main(String[] args) {

    var source = """
        {
            "name": "\\\"John\\\\n\"
        }
          """;
    source = "{ \"symbol\": \"\u0041\" }";
    var lexer = new Lexer(source);

    var tk = lexer.tokenize();

    while (tk.hasMoreTokens())
      System.out.println(tk.nextToken());
  }
}
