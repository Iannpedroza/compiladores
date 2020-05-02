import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class tp2 {
   public static void main(String[] args) throws Exception {
      // Criação dos objetos Tabela de Simbolos, simbolo e analisador lexico
      AnalisadorSintatico sintatico = new AnalisadorSintatico();
      sintatico.S();

   }

}

class AnalisadorSintatico {
   AnalisadorLexico lexico;
   TabelaDeSimbolos tabela;
   Simbolo simbolo, simboloParaAnalise;
   public static Scanner s;
   AnalisadorSintatico() {
      try {
         s = new Scanner(System.in);
         s.useDelimiter(""); // Delimitador para que a classe scanner consiga ler char por char
         
         tabela = new TabelaDeSimbolos();
         lexico = new AnalisadorLexico(tabela);

         simbolo = lexico.analisarLexema(s);
         if (simbolo == null) { // comentario
            simbolo = lexico.analisarLexema(s);
         }
      } catch (Exception e) {
         checkEOF();
         System.out.print(e.getMessage());
      }
   }

   void casaToken(byte token) {
      try {
         if (simbolo != null) {
            if (simbolo.getToken() == token) {
               simboloParaAnalise = simbolo;
               simbolo = lexico.analisarLexema(s);
            } else {
               if (lexico.ehEOF) {
                  System.err.println((lexico.linha + 1) + ":Fim de Arquivo nao esperado.");
                  System.exit(0);
               } else {
                  tokenInesperado();
               }
            }
         } else {
            checkEOF();
         }
      } catch (Exception e) {
         checkEOF();
         System.err.println("casaT" + e.toString());
      }
   }

   // S -> {D}+{C}+
   void S() {
      try {
         if (simbolo != null) {
            while (ehDeclaracao()) {
               checkEOF();
               D();
            }

            while(ehComando()) {
               checkEOF();
               C();
            } 

         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   // D -> (int | byte | boolean | string) id [<- CONST]  {,id [<- CONST]}  ';' |  final id (<- CONST) ';'
   void D() {
      try {
         checkEOF();
         if (simbolo.getToken() == tabela.INT || simbolo.getToken() == tabela.BYTE || simbolo.getToken() == tabela.BOOLEAN || simbolo.getToken() == tabela.STRING) {
            
            if(simbolo.getToken() == tabela.INT){
               casaToken(tabela.INT); 
            } else if(simbolo.getToken() == tabela.BYTE){
               casaToken(tabela.BYTE);
            } else if(simbolo.getToken() == tabela.BOOLEAN){
               casaToken(tabela.BOOLEAN);
            } else {
               casaToken(tabela.STRING);
            }
            
            casaToken(tabela.ID);
            if (simbolo.getToken() == tabela.ATT){
               casaToken(tabela.ATT);
               casaToken(tabela.CONSTANTE);

               while (simbolo.getToken() == tabela.VIRGULA) {
                  casaToken(tabela.VIRGULA);
                  casaToken(tabela.ID);
                  if (simbolo.getToken() == tabela.ATT) {
                     casaToken(tabela.ATT);
                     casaToken(tabela.CONSTANTE);
                  }
               }
               casaToken(tabela.PONTOVIRGULA);
            } else if (simbolo.getToken() == tabela.VIRGULA){
               while(simbolo.getToken() == tabela.VIRGULA) {
                  casaToken(tabela.VIRGULA);
                  casaToken(tabela.ID);
                  if (simbolo.getToken() == tabela.ATT) {
                     casaToken(tabela.ATT);
                     casaToken(tabela.CONSTANTE);
                  }
               }
               casaToken(tabela.PONTOVIRGULA);
                  

            } else {
               casaToken(tabela.PONTOVIRGULA);
            }

         }else {
            // se for 'final' ao invez de int,byte,boolean ou string
            casaToken(tabela.FINAL);
            casaToken(tabela.ID);
            casaToken(tabela.ATT);
            casaToken(tabela.CONSTANTE);
         }  

      }catch(Exception e){} // FIM TRY

   }//FIM D   

   // C -> (id <- E)';' | while '(' E ')'  (C) | while '(' E ')' begin {C} endwhile | if '(' E ')' (C |C')  | ';' | readln '('id')'  ';'
   // | write '(' E {, E}')' ';'| writeln '(' E {, E} ')' ';'
   void C() {
      try {
         checkEOF();
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
            if (simbolo.getToken() == tabela.BEGIN){
               casaToken(tabela.BEGIN);
               while (ehComando()){
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
               if(simbolo.getToken() == tabela.ELSE){
                  casaToken(tabela.ELSE);
                  if(simbolo.getToken() == tabela.BEGIN){
                     casaToken(tabela.BEGIN);
                     while(ehComando()){
                        C();
                     }
                     casaToken(tabela.ENDELSE);

                  }else{
                     C();
                  }
               }
            } else{
               //se for C'
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
         checkEOF();
         System.err.println(e.toString());
      }
   }

   //C' => (begin {C} endif) [else (begin {C} endelse | C)]
   void C1() {
      try {
         checkEOF();
         casaToken(tabela.BEGIN);
         while(ehComando()){
            C();
         }
         casaToken(tabela.ENDIF);
         if(simbolo.getToken() == tabela.ELSE){
            casaToken(tabela.ELSE);
            if(simbolo.getToken() == tabela.BEGIN){
               casaToken(tabela.BEGIN);
               while(ehComando()){
                  C();
               }
               casaToken(tabela.ENDELSE);   
            }else{
               C();
            }
         }


      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   // E -> E' {('<' | '>' | '<=' | '>=' | '<>' | '=') E'}
   void E() {
      try {
         checkEOF();
         simboloE = E1();
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

            simboloE2 = E1();
         }

      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }

   }

   // E' -> [+ | -] E'' {('+' | '-' | '||' ) E''}
   Simbolo E1() {
      Simbolo simboloE1 = new Simbolo();
      Simbolo simboloE1_2 = new Simbolo();
      Simbolo simboloCloneE1 = new Simbolo();
      Simbolo simboloCloneE1_2 = new Simbolo();
      boolean condicao;
      try {
         checkEOF();

         if (simbolo.getToken() == tabela.SOMA) {
            casaToken(tabela.SOMA);

         } else if (simbolo.getToken() == tabela.SUB) {
            casaToken(tabela.SUB);

         }
         simboloE1 = E2();

         while (simbolo.getToken() == tabela.SOMA || simbolo.getToken() == tabela.SUB
               || simbolo.getToken() == tabela.OR) { // POSSIVEL ERRO IANN

            if (simbolo.getToken() == tabela.SOMA) {
               casaToken(tabela.SOMA);
            } else if (simbolo.getToken() == tabela.SUB) {
               casaToken(tabela.SUB);
            } else {
               casaToken(tabela.OR);
            }
            simboloE1_2 = E2();

         }

      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }

      return simboloE1;

   }

   // E'' -> F {('*' | '/' | '&&') F}
   void E2() {

      try {
         checkEOF();

         simboloE2 = F();
         while (simbolo.getToken() == tabela.MULT || simbolo.getToken() == tabela.DIV
               || simbolo.getToken() == tabela.AND) {
            if (simbolo.getToken() == tabela.MULT) {
               casaToken(tabela.MULT);

            } else if (simbolo.getToken() == tabela.DIV) {
               casaToken(tabela.DIV);

            } else {
               casaToken(tabela.AND);

            }
         }

      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }


   }

   // F -> '(' E ')' | ! F | id | num
   void F() {
      Simbolo simboloF = new Simbolo(); // simbolo que vai ser retornado
      Simbolo simboloF1 = new Simbolo();
      try {

         checkEOF();

         if (simbolo.getToken() == tabela.APAR) {
            casaToken(tabela.ABREP);
            simboloF = E();
            casaToken(tabela.FECHAP);
         } else if (simbolo.getToken() == tabela.NEG) {
            casaToken(tabela.NEG);
            simboloF1 = F();

         } else if (simbolo.getToken() == tabela.CONSTANTE) {
            casaToken(tabela.CONSTANTE);
            simboloF = new Simbolo(simboloParaAnalise.getToken(), simboloParaAnalise.getLexema(), // duvida iann
                  simboloParaAnalise.getEndereco(), simboloParaAnalise.getTipo(), "classe_variavel", 0);

         } else {
            casaToken(tabela.ID);
            simboloF = lexico.simbolos.buscaSimbolo(simboloParaAnalise.getLexema());
         }

      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   void checkEOF() {
      if (lexico.ehEOF) {
         System.err.println((lexico.linha + 1) + ":Fim de arquivo nao esperado.");
         System.exit(0);
      }
   }

   void tokenInesperado() {
      System.err.println((lexico.linha + 1) + ":Token nao esperado: " + simbolo.getLexema());
      System.exit(0);
   }

   boolean ehDeclaracao() {
      return (simbolo != null && (simbolo.getToken() == tabela.INT || simbolo.getToken() == tabela.BYTE || simbolo.getToken() == tabela.BOOLEAN
            || simbolo.getToken() == tabela.STRING) || simbolo.getToken() == tabela.FINAL  );
   }

   boolean ehComando() {
      return (simbolo != null && (simbolo.getToken() == tabela.ID || simbolo.getToken() == tabela.WHILE
            || simbolo.getToken() == tabela.IF || simbolo.getToken() == tabela.PONTOVIRGULA || simbolo.getToken() == tabela.READLN
            || simbolo.getToken() == tabela.WRITELN || simbolo.getToken() == tabela.WRITE));
   }

}