import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
public class tp1 {
   public static void main(String[] args) throws Exception {
      try {
         TabelaDeSimbolos tS = new TabelaDeSimbolos();
         AnalisadorLexico aL = new AnalisadorLexico(tS);
         // FileReader reader2 = new FileReader("exemplo1.l");
         // BufferedReader br2 = new BufferedReader(reader2);
         // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         Scanner s = new Scanner(System.in);
         s.useDelimiter(""); // delimiter ""
         Simbolo simbol = new Simbolo();
         while (s.hasNext()) {
            // simb = aL.analisarLexema(aL.devolve, br);
            simbol = aL.analisarLexema(aL.devolucao, s);
            if (simbol != null) {
               // System.out.println(simbol.getToken() + " " + simbol.getLexema());
            }
         }
      } catch (IOException e) {
         System.err.format("IOException: %s%n", e);
      }
   }

}

class AnalisadorLexico {
   public TabelaDeSimbolos simbolos;

   public AnalisadorLexico(TabelaDeSimbolos tabela) {
      this.simbolos = tabela;
   }

   char c;
   String lexema = "";
   Simbolo simb;
   public static int linha = 0;
   public boolean devolucao = false;
   public boolean ehComentario = false;
   public boolean ehEOF = false;
   public static final int estadoInicial = 0;
   public static char validos[] = { ' ', '_', '.', ',', ';', '&', ':', '(', ')', '[', ']', '{', '}', '+', '-', '"',
         '\'', '/', '!', '?', '>', '<', '=', '\n' };

   Simbolo analisarLexema(boolean devolucao, Scanner s) throws Exception {
      // BufferedReader arquivo2 = null;
      int estadoAtual = estadoInicial;
      int estadoFinal = 1;
      lexema = "";
      // int pos = 0;
      // c = (char)arquivo.read();
      // while(c != '\n' || c != '\r'){
      while (estadoAtual != estadoFinal) {
         switch (estadoAtual) {
            case 0:
               if (devolucao == false) {
                  c = s.next().charAt(0);

               }
               devolucao = false;
               // checkEOF(c);
               // Quebra de linha no arquivo
               if (c == '\n') { // @TODO entender o char 11
                  linha++;
               } else if (ehLetra(c) || c == '_') {
                  // lendo identificador
                  lexema += c;
                  estadoAtual = 5;
               } else if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')' || c == ';' || c == ','
                     || c == '=') {
                  // LÃƒÂª os tokens que possuem somente 1 caractere e vao para o estado final
                  // Nada ÃƒÂ© devolvido
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else if (c == 8 || c == 9 || c == 11 || c == 13 || c == 32) {
                  // lendo "lixo" espaÃƒÂ§o em branco, enter tabs vertical e horizontal
                  estadoAtual = estadoInicial;
               } else if (c == '/') {
                  lexema += c;
                  estadoAtual = 14;
               } else if (c == '>' || c == '!') {
                  // Possui 4 variacoes: '>' e '>=' e '!' e '!=', vai para o proximo estado para
                  // decidir qual o
                  // token
                  lexema += c;
                  estadoAtual = 2;
               } else if (c == '<') {
                  lexema += c;
                  estadoAtual = 6;
               } else if (c == '&') {
                  lexema += c;
                  estadoAtual = 11;
               } else if (c == '=') { // = apenas para comparacao
                  lexema += c;

               } else if (c == '"') {
                  // String
                  lexema += c;
                  estadoAtual = 12;
               } else if (ehDigito(c)) {
                  // Validação de constantes
                  if (c == '0') {
                     // Hexadecimal ou número iniciado de 0
                     lexema += c;
                     estadoAtual = 7;
                  } else {
                     // Numero nao comecado por 0
                     lexema += c;
                     estadoAtual = 10;
                  }
               } else if (c == 65535) {
                  // verificar com luigi
                  estadoAtual = estadoFinal;
                  lexema += c;
                  ehEOF = true;
                  // arquivo.close();
               } else if (c == '|') {
                  lexema += c;
                  estadoAtual = 17;

               } else {
                  printErrorCaracter();
               }
               break;
            case 2:
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == '=') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 3:
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == '=') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else if (c == '>') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 4:
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == '\'') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 5:
               c = s.next().charAt(0);
               // checkEOF(c);

               // Continua no mesmo estado caso letra digito ou sublinhado
               if (ehLetra(c) || c == '_' || ehDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 6:
               // Verifica as 3 variações de '<': '<-' , '<', '<='
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == '=' || c == '-') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 7:
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == 'x') {
                  lexema += c;
                  estadoAtual = 8;
               } else if (ehDigito(c)) {
                  lexema += c;
                  estadoAtual = 10;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 8:
               c = s.next().charAt(0);
               // checkEOF(c);

               // VALIDA SE É UM HEXA VALIDO
               if (ehHexa(c)) {
                  lexema += c;
                  estadoAtual = 9;
               } else {// VALIDAR ISSO
                  lexema += c;
                  printError();
               }
               break;
            case 9:
               c = s.next().charAt(0);
               // checkEOF(c);

               // VALIDA SE É UM HEXA VALIDO
               if (ehHexa(c)) {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  lexema += c;
                  printError();
               }
               break;
            case 10:
               // VALIDA CONSTANTE
               c = s.next().charAt(0);
               // checkEOF(c);

               if (ehDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 11:
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == '&') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 12:
               // Validação de strings
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c == '"') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else if (c == '\n' || c == '\r') {
                  printError();
               } else if (ehValido(c)) {
                  lexema += c;
               } else {
                  lexema += c;
                  printErrorCaracter();
               }
               break;
            case 13:
               c = s.next().charAt(0);
               // checkEOF(c);

               break;
            case 14:
               // Cases 14, 15 e 16 validam os comentários e o token '/'
               c = s.next().charAt(0);
               // checkEOF(c);

               if (c != '*') {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               } else {
                  estadoAtual = 15;
                  ehComentario = true;
               }
               break;
            case 15:
               c = s.next().charAt(0);
               checkEOF(c);
               // System.out.println(c);
               if (c == '*') {
                  estadoAtual = 16;
               }else if(c == '\n'){
                  linha++;  
               }
               break;
            case 16:
               c = s.next().charAt(0);
               // checkEOF(c);
               if (c == '*') {
                  estadoAtual = 16;
               } else if (c == '/') {
                  estadoAtual = estadoInicial;
                  lexema = "";
                  ehComentario = false;
               } else {
                  estadoAtual = 15;
               }
               break;

            case 17:
               c = s.next().charAt(0);

               if (c == '|') {
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }

         }
      }
      simb = null;
      if (!ehEOF) {
         // Seleciona o simbolo da tabela de simbolos caso ele ja esteja na tabela
         if (simbolos.tabela.get(lexema) != null) {// TRROOOCAAAARRRR
            simb = simbolos.tabela.get(lexema);
         } else if (ehLetra(lexema.charAt(0)) || lexema.charAt(0) == '_') {
            // Insere um novo ID na tabela de simbolos
            simb = simbolos.inserirID(lexema);
         } else if (ehDigito(lexema.charAt(0))) {

            if (lexema.charAt(0) == '0') {
               if (lexema.length() == 1) {
                  simb = simbolos.inserirConst(lexema);
               } else {
                  // Constante hexadecimal
                  if (lexema.length() > 2 && (lexema.charAt(1) == 'X' || lexema.charAt(1) == 'x')) {
                     // Constantes hexa sao do tipo 0xFF -> 4 caracteres
                     if (lexema.length() == 4) {
                        // Verifica se os 2 ultimos digitos sao hexadecimais
                        if (Character.digit(lexema.charAt(2), 16) >= 0 && Character.digit(lexema.charAt(3), 16) >= 0) {
                           simb = simbolos.inserirConst(lexema);
                        } else {
                           printError();
                        }
                     }
                  } else {
                     // Verifica se possui algum caracter nao numerico
                     for (int i = 0; i < lexema.length(); i++) {
                        if (!ehDigito(lexema.charAt(i))) {
                           printError();
                           System.out.println("é aqui");
                        }
                     }

                     simb = simbolos.inserirConst(lexema);
                  }

               }
            } else {
               // Verifica se possui algum caracter nao numerico
               for (int i = 0; i < lexema.length(); i++) {
                  if (!ehDigito(lexema.charAt(i))) {
                     printError();
                  }
               }

               simb = simbolos.inserirConst(lexema);
            }
         } else if (lexema.charAt(0) == '\'' && lexema.charAt(lexema.length() - 1) == '\'') {
            simb = simbolos.inserirConst(lexema);
         } else if (lexema.charAt(0) == '"' && lexema.charAt(lexema.length() - 1) == '"') {
            String x = lexema.substring(0, lexema.length() - 1);
            x += "$";
            x += '"';
            lexema = x;
            simb = simbolos.inserirConst(lexema);
         } else {
            printError();
         }
      }

      return simb;
   }

   public static boolean ehHexa(char c) {
      if (c >= 'A' && c <= 'F' || ehDigito(c)) {
         return true;
      }
      return false;
   }

   public static boolean ehLetra(char c) {
      if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
         return true;
      }
      return false;
   }

   public static boolean ehDigito(char c) {
      if (c >= '0' && c <= '9') {
         return true;
      }
      return false;
   }

   public static boolean ehValido(char c) {
      return (ehLetra(c) || ehDigito(c) || new String(validos).indexOf(c) >= 0);
   }

   public void printError() {
      System.out.println(linha + 1);
      System.out.println("Lexema nao identificado [" + lexema + "].");
      System.exit(1);
   }

   public void printErrorCaracter() {
      System.out.println(linha + 1);
      System.out.println("caractere invalido.");
      System.exit(1);
   }

   /*
    * public void printErrorHexa() { System.out.println("Erro na linha: " +
    * (linha+1) + ". Lexema nao reconhecido: [" + lexema +""+ c +"]");
    * System.exit(1); }
    */ // RETIRAR DO ANALIZADOR LEXICOOOOOOOOOOOOOOO

   void checkEOF(char c) {
      if (this.ehEOF || c == 65535) {
         System.out.println(linha);
         System.err.println("fim de arquivo nao esperado.");
         System.exit(0);
      }
   }
}

class Simbolo {
   public String lexema = "";
   public byte token;
   public String classe = "";
   public String tipo = "";
   public int tamanho = 0;
   public int endereco;

   public Simbolo() {
      this.token = -1;
   }

   public Simbolo(byte token, String lexema, int endereco) {
      this.lexema = lexema;
      this.token = token;
      this.endereco = endereco;
   }

   public Simbolo(byte token, String lexema, int endereco, String tipo) {
      this.token = token;
      this.lexema = lexema;
      this.tipo = tipo;
      this.endereco = endereco;
   }

   public Simbolo(byte token, String lexema, int endereco, String tipo, String classe, int tamanho) {
      this.token = token;
      this.lexema = lexema;
      this.classe = classe;
      this.tipo = tipo;
      this.endereco = endereco;
      this.tamanho = tamanho;
   }

   public byte getToken() {
      return token;
   }

   public int getEnd() {
      return endereco;
   }

   public String getLexema() {
      return lexema;
   }

   public String getTipo() {
      return tipo;
   }

   public void setTipo(String tipo) {
      this.tipo = tipo;
   }

   public void setToken(byte token) {
      this.token = token;
   }

   public String getClasse() {
      return classe;
   }

   public void setClasse(String classe) {
      this.classe = classe;
   }

   public int getEndereco() {
      return endereco;
   }

   public void setEndereco(int endereco) {
      this.endereco = endereco;
   }

   public int getTamanho() {
      return tamanho;
   }

   public void setTamanho(int tamanho) {
      this.tamanho = tamanho;
   }

}

class TabelaDeSimbolos {
   public HashMap<String, Simbolo> tabela = new HashMap<>();
   public static int endereco = -1;

   public final byte IF = 0;
   public final byte INT = 1;
   public final byte BYTE = 2;
   public final byte WRITELN = 3;
   public final byte WRITE = 4;
   public final byte READLN = 5;
   public final byte ELSE = 6;
   public final byte APAR = 7; // (
   public final byte FPAR = 8; // )
   public final byte PONTOVIRGULA = 9;
   public final byte ATT = 10;
   public final byte OR = 11;
   public final byte MAIOR = 12;
   public final byte MENOR = 13;
   public final byte MAIORIG = 14;
   public final byte MENORIG = 15;
   public final byte DIFF = 16;
   public final byte VIRGULA = 17;
   public final byte SUM = 18;
   public final byte SUB = 19;
   public final byte DIV = 20;
   public final byte MULT = 21;
   public final byte TRUE = 22;
   public final byte FALSE = 23;
   public final byte BOOLEAN = 24;
   public final byte FINAL = 26;
   public final byte STRING = 27;
   public final byte WHILE = 28;
   public final byte AND = 29;
   public final byte NEG = 30;
   public final byte BEGIN = 31;
   public final byte ENDWHILE = 32;
   public final byte ENDIF = 33;
   public final byte ENDELSE = 34;
   public final byte COMPARA = 35;
   public final byte ID = 36;
   public final byte VALORCONST = 37;

   public TabelaDeSimbolos() {
      tabela.put("if", new Simbolo(IF, "if", ++endereco));
      tabela.put("int", new Simbolo(INT, "int", ++endereco));
      tabela.put("byte", new Simbolo(BYTE, "byte", ++endereco));
      tabela.put("writeln", new Simbolo(WRITELN, "writeln", ++endereco));
      tabela.put("write", new Simbolo(WRITE, "write", ++endereco));
      tabela.put("readln", new Simbolo(READLN, "readln", ++endereco));
      tabela.put("else", new Simbolo(ELSE, "else", ++endereco));
      tabela.put("(", new Simbolo(APAR, "(", ++endereco));
      tabela.put(")", new Simbolo(FPAR, ")", ++endereco));
      tabela.put(";", new Simbolo(PONTOVIRGULA, ";", ++endereco));
      tabela.put("<-", new Simbolo(ATT, "<-", ++endereco));
      tabela.put("||", new Simbolo(OR, "||", ++endereco));
      tabela.put(">", new Simbolo(MAIOR, ">", ++endereco));
      tabela.put("<", new Simbolo(MENOR, "<", ++endereco));
      tabela.put(">=", new Simbolo(MAIORIG, ">=", ++endereco));
      tabela.put("<=", new Simbolo(MENORIG, "<=", ++endereco));
      tabela.put("!=", new Simbolo(DIFF, "!=", ++endereco));
      tabela.put(",", new Simbolo(VIRGULA, ",", ++endereco));
      tabela.put("+", new Simbolo(SUM, "+", ++endereco));
      tabela.put("-", new Simbolo(SUB, "-", ++endereco));
      tabela.put("/", new Simbolo(DIV, "/", ++endereco));
      tabela.put("*", new Simbolo(MULT, "*", ++endereco));
      tabela.put("TRUE", new Simbolo(TRUE, "TRUE", ++endereco));
      tabela.put("FALSE", new Simbolo(FALSE, "FALSE", ++endereco));
      tabela.put("boolean", new Simbolo(BOOLEAN, "boolean", ++endereco));
      tabela.put("final", new Simbolo(FINAL, "final", ++endereco));
      tabela.put("string", new Simbolo(STRING, "string", ++endereco));
      tabela.put("while", new Simbolo(WHILE, "while", ++endereco));
      tabela.put("&&", new Simbolo(AND, "&&", ++endereco));
      tabela.put("!", new Simbolo(NEG, "!", ++endereco));
      tabela.put("begin", new Simbolo(BEGIN, "begin", ++endereco));
      tabela.put("endwhile", new Simbolo(ENDWHILE, "endwhile", ++endereco));
      tabela.put("endif", new Simbolo(ENDIF, "endif", ++endereco));
      tabela.put("endelse", new Simbolo(ENDELSE, "endelse", ++endereco));
      tabela.put("=", new Simbolo(COMPARA, "=", ++endereco));
   }

   /**/
   public String pesquisa(String lexema) {
      Simbolo aux = tabela.get(lexema);
      return ((aux == null) ? "NULL" : "" + aux.getEnd());
   }

   public Simbolo inserirID(String lexema) {
      Simbolo simbolo = new Simbolo(ID, lexema, ++endereco);
      tabela.put(lexema, simbolo);
      return tabela.get(lexema);
   }

   public Simbolo buscaSimbolo(String lexema) {
      Simbolo aux = tabela.get(lexema);
      return ((aux == null) ? null : tabela.get(lexema));

   }

   public Simbolo inserirConst(String lexema) {
      Simbolo simbolo = new Simbolo(VALORCONST, lexema, ++endereco);
      tabela.put(lexema, simbolo);
      return tabela.get(lexema);
   }

   /*public static void main(String[] args) {
      TabelaDeSimbolos tbl = new TabelaDeSimbolos();

      System.out.println(tbl.pesquisa("a"));
      System.out.println(tbl.pesquisa("FI"));
      System.out.println(tbl.pesquisa("iF"));
      System.out.println(tbl.buscaSimbolo("&&").getLexema());
      System.out.println(tbl.buscaSimbolo("&&").getToken());
      System.out.println(tbl.pesquisa("And"));
      System.out.println(tbl.buscaSimbolo("for"));
      System.out.println(tbl.buscaSimbolo("<"));
      tbl.inserirID("teste");
      System.out.println(tbl.pesquisa("boolean"));
      System.out.println(tbl.pesquisa("teste"));
      tbl.inserirID("TEstE");
      System.out.println(tbl.pesquisa("teste"));
   }*/
}
