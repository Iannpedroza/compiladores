import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class tp2 {
   public static void main(String[] args) throws Exception {
      // Criação dos objetos Tabela de Simbolos, simbolo e analisador lexico

      Scanner s = new Scanner(System.in);
      simbolo.useDelimiter(""); // Delimitador para que a classe scanner consiga ler char por char
      TabelaDeSimbolos tabela = new TabelaDeSimbolos();
      AnalisadorLexico lexico = new AnalisadorLexico(tabela);
      Simbolo simbolo = new Simbolo();
      AnalisadorSintatico sintatico = new AnalisadorSintatico(s);
      sintatico.S();

   }

}

class AnalisadorSintatico {
   AnalisadorLexico lexico;
   TabelaDeSimbolos tabela;
   Simbolo simbolo, simboloParaAnalise;

   AnalisadorSintatico(Scanner s) {
      try {
         this.arquivo = arquivo;
         tabela = new TabelaDeSimbolos();
         lexico = new AnalisadorLexico(tabela);
         rotuloPJ = new Rotulo();

         simbolo = lexico.analisarLexema(lexico.devolve, arquivo);
         if (simbolo == null) { // comentario
            simbolo = lexico.analisarLexema(lexico.devolve, arquivo);
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
               simbolo = lexico.analisarLexema(lexico.devolve, arquivo);
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

            while() {
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
      Simbolo simboloEconst = new Simbolo();
      Simbolo simboloString = new Simbolo();
      Simbolo simboloId = new Simbolo();
      Simbolo simboloConst = new Simbolo();
      Simbolo c = new Simbolo();
      boolean condicao;
      boolean condGC;
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

   // D' -> [= CONSTV]{,id[ = CONSTV | '['num']']} | '['num']'{,id[ = CONSTV |
   // '['num']']}
   Simbolo D1(Simbolo id) {
      Simbolo D1 = new Simbolo();
      Simbolo temp;
      Simbolo simboloEvet = new Simbolo();
      Simbolo simboloId = new Simbolo();
      boolean condGC = false;
      try {
         checkEOF();
         if (simbolo.getToken() == tabela.ATT || simbolo.getToken() == tabela.ACOL
               || simbolo.getToken() == tabela.VIR) {
            if (simbolo.getToken() == tabela.ATT) {
               casaToken(tabela.ATT);
               temp = CONSTV();
               D1 = temp;
               if (simbolo.getToken() == tabela.VIR) {
                  while (simbolo.getToken() != tabela.PV) {
                     casaToken(tabela.VIR);
                     casaToken(tabela.ID);
                     simboloId = simboloParaAnalise;
                     if (simbolo.getToken() == tabela.ACOL || simbolo.getToken() == tabela.ATT) {
                        if (simbolo.getToken() == tabela.ACOL) {
                           casaToken(tabela.ACOL);
                           casaToken(tabela.VALORCONST);
                           simboloEvet = simboloParaAnalise;
                           casaToken(tabela.FCOL);
                        } else {
                           casaToken(tabela.ATT);
                           temp = CONSTV();
                        }
                     }
                  }
               }
               /*
                * if(condGC == false){ geracaoCodigo4(retornoD1, id); }
                */
            } else if (simbolo.getToken() == tabela.VIR) {
               D1.setToken((byte) 999); // pois assim numa concatenacao retorna algo <> de -1
               while (simbolo.getToken() != tabela.PV) {
                  casaToken(tabela.VIR);
                  casaToken(tabela.ID);
                  simboloId = simboloParaAnalise;
                  if (simbolo.getToken() == tabela.ACOL || simbolo.getToken() == tabela.ATT) {
                     if (simbolo.getToken() == tabela.ACOL) {
                        casaToken(tabela.ACOL);
                        casaToken(tabela.VALORCONST);
                        simboloEvet = simboloParaAnalise;

                        casaToken(tabela.FCOL);

                     } else {
                        casaToken(tabela.ATT);
                        temp = CONSTV();

                     }
                  }

               }
            } else if (simbolo.getToken() == tabela.ACOL) {
               casaToken(tabela.ACOL);
               casaToken(tabela.VALORCONST);
               simboloEvet = simboloParaAnalise;
               D1 = simboloEvet;

               casaToken(tabela.FCOL);

               if (simbolo.getToken() == tabela.VIR) {
                  while (simbolo.getToken() != tabela.PV) {
                     casaToken(tabela.VIR);
                     casaToken(tabela.ID);

                     simboloId = simboloParaAnalise;

                     if (simbolo.getToken() == tabela.ACOL || simbolo.getToken() == tabela.ATT) {
                        condGC = acaoSemantica9();
                        if (simbolo.getToken() == tabela.ACOL) {
                           casaToken(tabela.ACOL);
                           casaToken(tabela.VALORCONST);
                           simboloEvet = simboloParaAnalise;
                           // simboloEvet = E();

                           casaToken(tabela.FCOL);

                        } else {
                           casaToken(tabela.ATT);
                           temp = CONSTV();

                        }
                     }

                  }
               }
            }
         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
      return D1;
   }

   // CONSTV -> 0x(hexa)(hexa) | char | E
   Simbolo CONSTV() {
      Simbolo constvSimbolo = new Simbolo();
      try {
         checkEOF();
         if (simbolo.getToken() == tabela.VALORCONST) {
            casaToken(tabela.VALORCONST);
            constvSimbolo = simboloParaAnalise; // NOVA acaoSemantica44 e //acaoSemantica45 sera que da certo?
            // constvSimbolo.setTipo(simboloParaAnalise.getTipo()); // acaoSemantica44 e
            // //acaoSemantica45
         } else {
            constvSimbolo = E(); // acaoSemantica43
         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
      return constvSimbolo;
   }

   // CONSTV' -> 0x(hexa)(hexa) | char | [-] num
   void CONSTV1() {
      try {
         checkEOF();
         if (simbolo.getToken() == tabela.VALORCONST) { // @TODO Como pegar o 0 ?
            casaToken(tabela.VALORCONST); // HEXA
            // casaToken(tabela.X); // @TODO Como pegar o X ?
            // casaToken(tabela.HEXA); // @TODO Como pegar os hexa ?
            // casaToken(tabela.HEXA); // @TODO Como pegar os hexa ?
         } else if (simbolo.getToken() == tabela.CHAR) {
            casaToken(tabela.CHAR);
         } else if (simbolo.getToken() == tabela.SUB || simbolo.getToken() == tabela.VALORCONST) {
            if (simbolo.getToken() == tabela.SUB) {
               casaToken(tabela.SUB);
            }
            casaToken(tabela.VALORCONST);
         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   // C -> (id <- E)';' | while '(' E ')'  (C) | while '(' E ')' begin {C} endwhile | if '(' E ')' (C |C')  | ';' | readln '('id')'  ';'
   // | write '(' E {, E}')' ';'| writeln '(' E {, E} ')' ';'
   void C() {
      Simbolo simboloEfor = new Simbolo();
      Simbolo simboloE2for = new Simbolo();
      Simbolo simboloid2 = new Simbolo();
      Simbolo simboloId = new Simbolo();
      Simbolo simboloEif = new Simbolo();
      Simbolo simboloEvet = new Simbolo();
      Simbolo simboloEwr = new Simbolo();
      boolean condicao;
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
               while (ehComando(){
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
   Simbolo E() {
      Simbolo simboloE = new Simbolo();
      Simbolo simboloE2 = new Simbolo();
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

      return simboloE;
   }

   // E' -> [+ | -] E'' {('+' | '-' | '||' ) E''}
   Simbolo E1() {
      Simbolo simboloE1 = new Simbolo();
      Simbolo simboloE1_2 = new Simbolo();

      try {
         checkEOF();

         if (simbolo.getToken() == tabela.SOMA) {
            casaToken(tabela.SOMA);

         } else if (simbolo.getToken() == tabela.SUB) {
            casaToken(tabela.SUB);

         }
         simboloE1 = E2();

         while (simbolo.getToken() == tabela.SOMA || simbolo.getToken() == tabela.SUB
               || simbolo.getToken() == tabela.OR) { 

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
   Simbolo E2() {
      Simbolo simboloE2 = new Simbolo();
      Simbolo simboloE2_1 = new Simbolo();

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
            simboloE2_1 = F();
         }

      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }

      return simboloE2;

   }

   // F -> '(' E ')' | ! F | id | num
   Simbolo F() {
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

            return simboloF1;
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

      return simboloF;

   }
/**/ 
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
      return (s != null && (s.getToken() == tabela.INT || s.getToken() == tabela.BYTE || s.getToken() == tabela.BOOLEAN
            || s.getToken() == tabela.STRING) || s.getToken() == tabela.FINAL  );
   }

   boolean ehComando() {
      return (simbolo != null && (simbolo.getToken() == tabela.ID || simbolo.getToken() == tabela.FOR
            || simbolo.getToken() == tabela.IF || simbolo.getToken() == tabela.PV || simbolo.getToken() == tabela.READLN
            || simbolo.getToken() == tabela.WRITELN || simbolo.getToken() == tabela.WRITE));
   }

}