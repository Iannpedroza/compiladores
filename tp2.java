import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class tp2 {
   public static void main(String[] args) throws Exception {
      // Criação do analisador sintatico
      AnalisadorSintatico sintatico = new AnalisadorSintatico();
      sintatico.S();

   }

}

class AnalisadorSintatico {
   AnalisadorLexico lexico;
   TabelaDeSimbolos tabela;
   Simbolo simbolo, simboloParaAnalise;
   public static Scanner s;

   //Construtor do analisador sintatico, responsável por construir os objetos da tabela de simbolos e do analisador lexico e do simbolo
   AnalisadorSintatico() {
      try {
         s = new Scanner(System.in);
         s.useDelimiter(""); // Delimitador para que a classe scanner consiga ler char por char
         simbolo = new Simbolo();
         tabela = new TabelaDeSimbolos();
         lexico = new AnalisadorLexico(tabela);

         simbolo = lexico.analisaLex(s);
         if (simbolo == null) { 
            simbolo = lexico.analisaLex(s);
         }
      } catch (Exception e) {
         verificaEOF();
      }
   }

   void casaToken(byte token) {
      try {
         if (simbolo != null) {
            if (simbolo.getToken() == token) {
               simboloParaAnalise = simbolo;
               simbolo = lexico.analisaLex(s);
            } else {
               if (lexico.ehEOF) {
                  System.out.println((lexico.linha + 1) + ":Fim de Arquivo nao esperado.");
                  System.exit(0);
               } else {
                  tokenInesperado();
               }
            }
         } else {
            verificaEOF();
         }
      } catch (Exception e) {
         verificaEOF();
      }
   }

   // S -> {D}+{C}+
   void S() {
      try {
         if (simbolo != null) {
            boolean erro = true;
            if (ehDeclaracao()) {
               erro = false;
               while (ehDeclaracao()) {
                  verificaEOF();
                  D();
               }
            }
            if (ehComando()){
               erro = false;
               while (ehComando()) {
                  verificaEOF();
                  C();
               }
            } 

            if (erro) {
               tokenInesperado();
            }
         }
      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }
   }

   // D -> (int | byte | boolean | string) id [<- CONST] {,id [<- CONST]} ';' |
   // final id (<- CONST) ';'
   void D() {
      try {
         verificaEOF();
         if (simbolo.getToken() == tabela.INT || simbolo.getToken() == tabela.BYTE
               || simbolo.getToken() == tabela.BOOLEAN || simbolo.getToken() == tabela.STRING) {

            if (simbolo.getToken() == tabela.INT) {
               casaToken(tabela.INT);
            } else if (simbolo.getToken() == tabela.BYTE) {
               casaToken(tabela.BYTE);
            } else if (simbolo.getToken() == tabela.BOOLEAN) {
               casaToken(tabela.BOOLEAN);
            } else {
               casaToken(tabela.STRING);
            }

            casaToken(tabela.ID);
            if (simbolo.getToken() == tabela.ATT) {
               casaToken(tabela.ATT);
               if (simbolo.getToken() == tabela.CONSTANTE){
                  casaToken(tabela.CONSTANTE);
               } else if (simbolo.getToken() == tabela.TRUE) {
                  casaToken(tabela.TRUE);
               }else {
                  casaToken(tabela.FALSE);
               }

               while (simbolo.getToken() == tabela.VIRGULA) {
                  casaToken(tabela.VIRGULA);
                  casaToken(tabela.ID);
                  if (simbolo.getToken() == tabela.ATT) {
                     casaToken(tabela.ATT);
                     if (simbolo.getToken() == tabela.CONSTANTE){
                        casaToken(tabela.CONSTANTE);
                     } else if (simbolo.getToken() == tabela.TRUE) {
                        casaToken(tabela.TRUE);
                     }else {
                        casaToken(tabela.FALSE);
                     }
                  }
               }
               casaToken(tabela.PONTOVIRGULA);
            } else if (simbolo.getToken() == tabela.VIRGULA) {
               while (simbolo.getToken() == tabela.VIRGULA) {
                  casaToken(tabela.VIRGULA);
                  casaToken(tabela.ID);
                  if (simbolo.getToken() == tabela.ATT) {
                     casaToken(tabela.ATT);
                     if (simbolo.getToken() == tabela.CONSTANTE){
                        casaToken(tabela.CONSTANTE);
                     } else if (simbolo.getToken() == tabela.TRUE) {
                        casaToken(tabela.TRUE);
                     }else {
                        casaToken(tabela.FALSE);
                     }
                  }
               }
               casaToken(tabela.PONTOVIRGULA);

            } else {
               casaToken(tabela.PONTOVIRGULA);
            }

         } else {
            // se for 'final' ao invez de int,byte,boolean ou string
            casaToken(tabela.FINAL);
            casaToken(tabela.ID);
            casaToken(tabela.ATT);
            if (simbolo.getToken() == tabela.CONSTANTE){
               casaToken(tabela.CONSTANTE);
            } else if (simbolo.getToken() == tabela.TRUE) {
               casaToken(tabela.TRUE);
            }else {
               casaToken(tabela.FALSE);
            }
            
         }

      } catch (Exception e) {
      }

   }

   // C -> (id <- E)';' | while '(' E ')' (C) | while '(' E ')' begin {C} endwhile
   // | if '(' E ')' (C |C') | ';' | readln '('id')' ';'
   // | write '(' E {, E}')' ';'| writeln '(' E {, E} ')' ';'
   void C() {
      try {
         verificaEOF();
         if (simbolo.getToken() == tabela.ID) {
            casaToken(tabela.ID);
            casaToken(tabela.ATT);
            E();
            casaToken(tabela.PONTOVIRGULA);
         } else if (simbolo.getToken() == tabela.WHILE) {
            casaToken(tabela.WHILE);
            casaToken(tabela.ABREP);
            E();
            casaToken(tabela.FECHAP);
            if (simbolo.getToken() == tabela.BEGIN) {
               casaToken(tabela.BEGIN);
               while (ehComando()) {
                  C();
               }

               casaToken(tabela.ENDWHILE);
            } else {
               C();
            }

         } else if (simbolo.getToken() == tabela.IF) {

            // C => if '(' E ')' (C [else (begin {C} endelse | C)]| C')

            casaToken(tabela.IF);
            casaToken(tabela.ABREP);
            E();
            casaToken(tabela.FECHAP);

            if (ehComando()) {
               C();
               if (simbolo.getToken() == tabela.ELSE) {
                  casaToken(tabela.ELSE);
                  if (simbolo.getToken() == tabela.BEGIN) {
                     casaToken(tabela.BEGIN);
                     while (ehComando()) {
                        C();
                     }
                     casaToken(tabela.ENDELSE);

                  } else {
                     C();
                  }
               }
            } else {
               // se for C'
               C1();
            }

         } else if (simbolo.getToken() == tabela.PONTOVIRGULA) {
            casaToken(tabela.PONTOVIRGULA);
         } else if (simbolo.getToken() == tabela.READLN) {
            casaToken(tabela.READLN);
            casaToken(tabela.ABREP);
            casaToken(tabela.ID);
            casaToken(tabela.FECHAP);
            casaToken(tabela.PONTOVIRGULA);
         } else if (simbolo.getToken() == tabela.WRITE || simbolo.getToken() == tabela.WRITELN) {

            if (simbolo.getToken() == tabela.WRITE) {
               casaToken(tabela.WRITE);
            } else {
               casaToken(tabela.WRITELN);
            }
            casaToken(tabela.ABREP);
            E();
            while (simbolo.getToken() == tabela.VIRGULA) {
               casaToken(tabela.VIRGULA);
               E();
            }
            casaToken(tabela.FECHAP);
            casaToken(tabela.PONTOVIRGULA);

         } else {
            tokenInesperado();
         }

      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }
   }

   // C' => (begin {C} endif) [else (begin {C} endelse | C)]
   void C1() {
      try {
         verificaEOF();
         casaToken(tabela.BEGIN);
         while (ehComando()) {
            C();
         }
         casaToken(tabela.ENDIF);
         if (simbolo.getToken() == tabela.ELSE) {
            casaToken(tabela.ELSE);
            if (simbolo.getToken() == tabela.BEGIN) {
               casaToken(tabela.BEGIN);
               while (ehComando()) {
                  C();
               }
               casaToken(tabela.ENDELSE);
            } else {
               C();
            }
         }

      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }
   }

   // E -> E' {('<' | '>' | '<=' | '>=' | '<>' | '=') E'}
   void E() {
      try {
         verificaEOF();
         E1();
         while (simbolo.getToken() == tabela.MAIOR || simbolo.getToken() == tabela.MENOR
               || simbolo.getToken() == tabela.MAIORIG || simbolo.getToken() == tabela.MENORIG
               || simbolo.getToken() == tabela.DIFF || simbolo.getToken() == tabela.ATT
               || simbolo.getToken() == tabela.COMPARA) {

            if (simbolo.getToken() == tabela.MAIOR) {
               casaToken(tabela.MAIOR);
            } else if (simbolo.getToken() == tabela.MENOR) {
               casaToken(tabela.MENOR);
            } else if (simbolo.getToken() == tabela.MAIORIG) {
               casaToken(tabela.MAIORIG);
            } else if (simbolo.getToken() == tabela.MENORIG) {
               casaToken(tabela.MENORIG);
            } else if (simbolo.getToken() == tabela.DIFF) {
               casaToken(tabela.DIFF);
            } else if (simbolo.getToken() == tabela.ATT) {
               casaToken(tabela.ATT);
            } else {
               casaToken(tabela.COMPARA);
            }

            E1();
         }

      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }

   }

   // E' -> [+ | -] E'' {('+' | '-' | '||' ) E''}
   void E1() {
      try {
         verificaEOF();

         if (simbolo.getToken() == tabela.SOMA) {
            casaToken(tabela.SOMA);

         } else if (simbolo.getToken() == tabela.SUB) {
            casaToken(tabela.SUB);

         }

         E2();
         while (simbolo.getToken() == tabela.SOMA || simbolo.getToken() == tabela.SUB
               || simbolo.getToken() == tabela.OR) {

            if (simbolo.getToken() == tabela.SOMA) {
               casaToken(tabela.SOMA);
            } else if (simbolo.getToken() == tabela.SUB) {
               casaToken(tabela.SUB);
            } else {
               casaToken(tabela.OR);
            }

            E2();
         }

      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }

   }

   // E'' -> F {('*' | '/' | '&&') F}
   void E2() {

      try {
         verificaEOF();

         F();
         while (simbolo.getToken() == tabela.MULT || simbolo.getToken() == tabela.DIV
               || simbolo.getToken() == tabela.AND) {
            if (simbolo.getToken() == tabela.MULT) {
               casaToken(tabela.MULT);

            } else if (simbolo.getToken() == tabela.DIV) {
               casaToken(tabela.DIV);

            } else {
               casaToken(tabela.AND);

            }
            F();
         }

      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }

   }

   // F -> '(' E ')' | ! F | id | num
   void F() {
      try {

         verificaEOF();

         if (simbolo.getToken() == tabela.ABREP) {
            casaToken(tabela.ABREP);
            E();
            casaToken(tabela.FECHAP);
         } else if (simbolo.getToken() == tabela.NEG) {
            casaToken(tabela.NEG);
            F();
         } else if (simbolo.getToken() == tabela.CONSTANTE) {
            casaToken(tabela.CONSTANTE);

         } else if (simbolo.getToken() == tabela.TRUE){
            casaToken(tabela.TRUE);
         } else if (simbolo.getToken() == tabela.FALSE) {
            casaToken(tabela.FALSE);
         } else {
            casaToken(tabela.ID);
         }
      //*//
      } catch (Exception e) {
         verificaEOF();
         System.err.println(e.toString());
      }
   }

   void verificaEOF() {
      if (lexico.ehEOF) {
         System.err.println((lexico.linha + 1) + ":Fim de arquivo nao esperado.");
         System.exit(0);
      }
   }
   
   void tokenInesperado() {
      System.err.print((lexico.linha + 1) + "\ntoken nao esperado [" + simbolo.getLexema() + "].");
      System.exit(0);
   }

   boolean ehDeclaracao() {
      return (simbolo != null
            && (simbolo.getToken() == tabela.INT || simbolo.getToken() == tabela.BYTE
                  || simbolo.getToken() == tabela.BOOLEAN || simbolo.getToken() == tabela.STRING)
            || simbolo.getToken() == tabela.FINAL);
   }

   boolean ehComando() {
      return (simbolo != null
            && (simbolo.getToken() == tabela.ID || simbolo.getToken() == tabela.WHILE || simbolo.getToken() == tabela.IF
                  || simbolo.getToken() == tabela.PONTOVIRGULA || simbolo.getToken() == tabela.READLN
                  || simbolo.getToken() == tabela.WRITELN || simbolo.getToken() == tabela.WRITE));
   }

}

class AnalisadorLexico {
   public TabelaDeSimbolos tabela;

   public AnalisadorLexico(TabelaDeSimbolos tabela) {
      this.tabela = tabela;
   }

   char c;
   String lexema = "";
   Simbolo simb;
   public int linha = 0;
   public boolean devolucao = false;
   public boolean ehComentario = false;
   public boolean ehEOF = false;
   public static final int estadoInicial = 0;
   public static final char validos[] = { ' ', '_', '.', ',', ';', '&', ':', '(', ')', '[', ']', '{', '}', '+', '-',
         '"', '\'', '/', '!', '?', '>', '<', '=', '\n' };

   Simbolo analisaLex(Scanner s) throws Exception {

      int estadoAtual = estadoInicial;
      int estadoFinal = 1;
      lexema = "";
      while (estadoAtual != estadoFinal) {
         switch (estadoAtual) {
            case 0:
               // Se devolucao == True não le mais um caracter pois existe um caracter
               // devolvido a ser analisado
               if (devolucao == false) {
                  c = s.next().charAt(0);
               }
               devolucao = false;

               // Quebra de linha no arquivo
               if (c == '\n') {
                  linha++;
               } else if (ehLetra(c) || c == '_') {
                  // Inicio de identificador
                  lexema += c;
                  estadoAtual = 5;
               } else if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')' || c == ';' || c == ','
                     || c == '=') {
                  // tokens que possuem somente 1 caractere
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else if (c == 8 || c == 9 || c == 11 || c == 13 || c == 32) {
                  // lendo "lixo" espaço em branco, tabs vertical e horizontal
                  estadoAtual = estadoInicial;
               } else if (c == '/') {
                  // Pode ser inicio de comentário ou apenas um '/'
                  lexema += c;
                  estadoAtual = 14;
               } else if (c == '>' || c == '!') {
                  // Possui 4 variacoes: '>' e '>=' e '!' e '!=', vai para o proximo estado para
                  // decidir qual o
                  // token
                  lexema += c;
                  estadoAtual = 2;
               } else if (c == '<') {
                  // Possuo 3 variacoes: '<', '<=' e '<-'
                  lexema += c;
                  estadoAtual = 6;
               } else if (c == '&') {
                  // Validar token '&&'
                  lexema += c;
                  estadoAtual = 11;
               } else if (c == '"') {
                  // Inicia processo de validar string
                  lexema += c;
                  estadoAtual = 12;
               } else if (ehDigito(c)) {
                  // Inicia processo de validar constantes
                  if (c == '0') {
                     // Hexadecimal ou número iniciado de 0
                     lexema += c;
                     estadoAtual = 7;
                  } else {
                     // Numero que nao comeca com 0
                     lexema += c;
                     estadoAtual = 10;
                  }
               } else if (c == 65535) {
                  // Possivel eof
                  estadoAtual = estadoFinal;
                  lexema += c;
                  ehEOF = true;
               } else if (c == '|') {
                  // Validar OR '||'
                  lexema += c;
                  estadoAtual = 3;
               } else {
                  // Caracteres invalidos
                  erroCaractere();
               }
               break;
            case 2:
               // Valida '<=', '<', '!=', '!'
               c = s.next().charAt(0);

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

               if (c == '|') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }

               break;
            case 5:
               c = s.next().charAt(0);
               // Valida identificadores e palavras reservadas com apenas letras
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
               // Verifica as constantes HEXADECIMAIS
               if (c == 'x') {
                  lexema += c;
                  estadoAtual = 8;
               } else if (ehDigito(c)) {
                  // Numero iniciado de 0
                  lexema += c;
                  estadoAtual = 10;
               } else {
                  // Numero 0
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 8:
               c = s.next().charAt(0);

               // VALIDA SE É UM HEXA VALIDO
               if (ehHexa(c)) {
                  lexema += c;
                  estadoAtual = 9;
               } else {
                  lexema += c;
                  erroLexema();
               }
               break;
            case 9:
               c = s.next().charAt(0);

               // VALIDA SE É UM HEXA VALIDO
               if (ehHexa(c)) {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  lexema += c;
                  erroLexema();
               }
               break;
            case 10:
               // VALIDA CONSTANTE NUMERICA
               c = s.next().charAt(0);

               if (ehDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
               }
               break;
            case 11:
               // Token '&&'
               c = s.next().charAt(0);

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
               verificaEOF(c);
               if (c == '"') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else if (c == '\n' || c == '\r') {
                  erroLexema();
               } else if (ehValido(c)) {
                  lexema += c;
               } else {
                  lexema += c;
                  erroCaractere();
               }
               break;
            case 14:
               // Cases 14, 15 e 16 validam os comentários e o token '/'
               c = s.next().charAt(0);

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
               verificaEOF(c);
               if (c == '*') {
                  estadoAtual = 16;
               } else if (c == '\n') {
                  linha++;
               }
               break;
            case 16:
               c = s.next().charAt(0);
               verificaEOF(c);
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
         }
      }
      simb = null;
      if (!ehEOF) {
         // Seleciona o simbolo da tabela de tabela caso ele ja esteja na tabela
         if (tabela.hash.get(lexema) != null) {
            simb = tabela.hash.get(lexema);
         } else if (ehLetra(lexema.charAt(0)) || lexema.charAt(0) == '_') {
            // Insere um novo identificador na tabela de simbolos
            simb = tabela.insereID(lexema);
         } else if (ehDigito(lexema.charAt(0))) {

            if (lexema.charAt(0) == '0') {
               if (lexema.length() == 1) {
                  simb = tabela.insereConstante(lexema, "NUMERICA");
               } else {
                  // Constante hexadecimal
                  if (lexema.length() == 4 && lexema.charAt(1) == 'x') {
                     // Constantes hexa sao do tipo 0xFF -> 4 caracteres
                     // Insere a constante HEXA
                     simb = tabela.insereConstante(lexema, "HEXA");
                  } else {
                     //Numero comecado com 0 e com tamanho maior que 1
                     simb = tabela.insereConstante(lexema, "NUMERICA");
                  }

               }
            } else {
               // Verifica se possui algum caracter nao numerico
               for (int i = 0; i < lexema.length(); i++) {
                  if (!ehDigito(lexema.charAt(i))) {
                     erroLexema();
                  }
               }

               simb = tabela.insereConstante(lexema, );
            }
         } else if (lexema.charAt(0) == '"' && lexema.charAt(lexema.length() - 1) == '"') {
            String x = lexema.substring(0, lexema.length() - 1);
            x += "$";
            x += '"';
            lexema = x;
            simb = tabela.insereConstante(lexema, "STRING");
         } else {
            erroLexema();
         }
      }

      return simb;
   }

   void verificaEOF(char c) {
      if (this.ehEOF || c == 65535) {
         System.err.println(this.linha + "\nfim de arquivo nao esperado.");
         System.exit(0);
      }
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

   public void erroLexema() {
      System.out.println(linha + 1);
      System.out.println("Lexema nao identificado [" + lexema + "].");
      System.exit(1);
   }

   public void erroCaractere() {
      System.out.println(linha + 1);
      System.out.println("caractere invalido.");
      System.exit(1);
   }

}

class Simbolo {
   public String lexema = "";
   public byte token;
   public String tipo = "";
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

   public byte getToken() {
      return token;
   }

   public int getEnd() {
      return endereco;
   }

   public String getLexema() {
      return lexema;
   }

   public void setToken(byte token) {
      this.token = token;
   }

}

class TabelaDeSimbolos {
   public HashMap<String, Simbolo> hash = new HashMap<>();
   public static int endereco = -1;

   public final byte IF = 0;
   public final byte INT = 1;
   public final byte BYTE = 2;
   public final byte WRITELN = 3;
   public final byte WRITE = 4;
   public final byte READLN = 5;
   public final byte ELSE = 6;
   public final byte ABREP = 7; // (
   public final byte FECHAP = 8; // )
   public final byte PONTOVIRGULA = 9;
   public final byte ATT = 10;
   public final byte OR = 11;
   public final byte MAIOR = 12;
   public final byte MENOR = 13;
   public final byte MAIORIG = 14;
   public final byte MENORIG = 15;
   public final byte DIFF = 16;
   public final byte VIRGULA = 17;
   public final byte SOMA = 18;
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
   public final byte CONSTANTE = 37;

   public TabelaDeSimbolos() {
      hash.put("if", new Simbolo(IF, "if", ++endereco));
      hash.put("int", new Simbolo(INT, "int", ++endereco));
      hash.put("byte", new Simbolo(BYTE, "byte", ++endereco));
      hash.put("writeln", new Simbolo(WRITELN, "writeln", ++endereco));
      hash.put("write", new Simbolo(WRITE, "write", ++endereco));
      hash.put("readln", new Simbolo(READLN, "readln", ++endereco));
      hash.put("else", new Simbolo(ELSE, "else", ++endereco));
      hash.put("(", new Simbolo(ABREP, "(", ++endereco));
      hash.put(")", new Simbolo(FECHAP, ")", ++endereco));
      hash.put(";", new Simbolo(PONTOVIRGULA, ";", ++endereco));
      hash.put("<-", new Simbolo(ATT, "<-", ++endereco));
      hash.put("||", new Simbolo(OR, "||", ++endereco));
      hash.put(">", new Simbolo(MAIOR, ">", ++endereco));
      hash.put("<", new Simbolo(MENOR, "<", ++endereco));
      hash.put(">=", new Simbolo(MAIORIG, ">=", ++endereco));
      hash.put("<=", new Simbolo(MENORIG, "<=", ++endereco));
      hash.put("!=", new Simbolo(DIFF, "!=", ++endereco));
      hash.put(",", new Simbolo(VIRGULA, ",", ++endereco));
      hash.put("+", new Simbolo(SOMA, "+", ++endereco));
      hash.put("-", new Simbolo(SUB, "-", ++endereco));
      hash.put("/", new Simbolo(DIV, "/", ++endereco));
      hash.put("*", new Simbolo(MULT, "*", ++endereco));
      hash.put("TRUE", new Simbolo(TRUE, "TRUE", ++endereco));
      hash.put("FALSE", new Simbolo(FALSE, "FALSE", ++endereco));
      hash.put("boolean", new Simbolo(BOOLEAN, "boolean", ++endereco));
      hash.put("final", new Simbolo(FINAL, "final", ++endereco));
      hash.put("string", new Simbolo(STRING, "string", ++endereco));
      hash.put("while", new Simbolo(WHILE, "while", ++endereco));
      hash.put("&&", new Simbolo(AND, "&&", ++endereco));
      hash.put("!", new Simbolo(NEG, "!", ++endereco));
      hash.put("begin", new Simbolo(BEGIN, "begin", ++endereco));
      hash.put("endwhile", new Simbolo(ENDWHILE, "endwhile", ++endereco));
      hash.put("endif", new Simbolo(ENDIF, "endif", ++endereco));
      hash.put("endelse", new Simbolo(ENDELSE, "endelse", ++endereco));
      hash.put("=", new Simbolo(COMPARA, "=", ++endereco));
   }

   public String pesquisa(String lexema) {
      Simbolo aux = hash.get(lexema);
      return ((aux == null) ? "NULL" : "" + aux.getEnd());
   }

   public Simbolo insereID(String lexema) {
      Simbolo simbolo = new Simbolo(ID, lexema, ++endereco);
      hash.put(lexema, simbolo);
      return hash.get(lexema);
   }

   public Simbolo insereConstante(String lexema, String tipo) {
      Simbolo simbolo = new Simbolo(CONSTANTE, lexema, ++endereco, tipo);
      hash.put(lexema, simbolo);
      return hash.get(lexema);
   }
}
