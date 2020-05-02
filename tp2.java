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
            do {
               checkEOF();
               D();
            } while (ehDeclaracao());

            do {
               checkEOF();
               C();
            } while (ehComando());

         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   // D -> VAR {(integer | char) id [D'] ';'}+ | CONST id( = CONSTV' | '['num']' =
   // '"' string '"') ';'
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
         if (s.getToken() == tabela.INT || s.getToken() == tabela.BYTE || s.getToken() == tabela.BOOLEAN || s.getToken() == tabela.STRING) {
            
            if(s.getToken() == tabela.INT){
               casaToken(tabela.INT); 
            }
            if(s.getToken() == tabela.BYTE){
               casaToken(tabela.BYTE);
            }
            if(s.getToken() == tabela.BOOLEAN){
               casaToken(tabela.BOOLEAN);
            }
            if(s.getToken() == tabela.STRING){
               casaToken(tabela.STRING);
            }
            

            if(s.getToken() == tabela.ID ){
               casaToken(tabela.ID);
               if(s.getToken() == tabela.PONTOVIRGULA ){
                  casaToken(tabela.PONTOVIRGULA);
                  //JOAO FAZER OQUE? ACABOU? ACHOU O PONTO E VIRGULA
               } 
               else if(s.getToken() == tabela.ATT ||s.getToken() == tabela.VIRGULA  ) {
                  while(s.getToken() == tabela.ATT ||s.getToken() == tabela.VIRGULA ){
                     if(s.getToken() == tabela.ATT){
                        casaToken(tabela.ATT);
                        if(s.getToken() == tabela.CONSTANTE){
                           CONST();
                           casaToken(tabela.CONSTANTE);
                        }else{
                           //ERRO NAO ATRIBUI NENHUMA CONSTANTE
                        }
                        if(s.getToken() == tabela.ATT){
                           //ERRO ,DEPOIS DE CONSTANTE VEIO ATRIBUIÇÃO
                        }

                     }else if(s.getToken() == tabela.VIRGULA){
                        casaToken(tabela.VIRGULA);
                        if(s.getToken() == tabela.ID){
                           casaToken(tabela.ID);
                        }else{
                           //ERRO , DEPOIS DA VIRGULA SÓ PODE VIR ID
                        }
                     }
                  }
                  if(s.getToken() == tabela.PONTOVIRGULA){
                     casaToken(tabela.PONTOVIRGULA);
                  }
                  else{
                     //ERRO, NAO FINALIZADOU A LISTA DE IDS COM ;
                  }
               }
              

            }
            else{
               //ERRO PQ NAO ACHOU ID DPS DE INT,BYTE,BOOLEAN OU STRING
            }

         }else if(s.getToken() == tabela.FINAL){
            // se for 'final' ao invez de int,byte,boolean ou string
            casaToken(tabela.FINAL);
            if(s.getToken() == tabela.ID){
               casaToken(tabela.ID);
               if(s.getToken() == tabela.ATT){
                  casaToken(tebla.ATT);
                  if(s.getToken() == tabela.CONSTANTE){
                     CONST();
                     casaToken(tabela.CONSTANTE);
                     if(s.getToken() == tabela.PONTOVIRGULA){
                        casaToken(tabela.PONTOVIRGULA);
                     }
                  }
                  else{
                     //ERRO, NAO VEIO CONSTANTE DEPOIS DE ATRIBUIÇÃO
                  }
               } 
               else{
                  //ERRO NAO VEIO ATRIBUIÇÃO DEPOIS DE ID QUE ERA A UNICA OPÇÃO POSSIVEL
               }
            }
            else{
               //ERRO , NÃO VEIO ID DEPOIS DE FINAL QUE É A UNICA OPÇÃO POSSIVEL
            }
         
         }
         else{
            //ERRROOOOOU, NAO VEIO NADA DE DECLARAÇÃO
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

   // C -> id C' ';'| FOR id = E to E [step num] do C'' | if E then C''' | ';' |
   // readln'('id')'';' | write'('E{,E}')'';' | writeln'('E{,E}')'';'
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

            A(simboloParaAnalise);
            casaToken(tabela.PV);
         } else if (simbolo.getToken() == tabela.FOR) {
            casaToken(tabela.FOR);
            casaToken(tabela.ID);
            simboloId = simboloParaAnalise;
            casaToken(tabela.ATT);
            simboloEfor = E();
            String rotInicio = rotuloPJ.novoRotulo(); // GeracaoCodigo28
            String rotFim = rotuloPJ.novoRotulo(); // GeracaoCodigo28
            // geracaoMemoria.zerarTemp();

            // casaToken(tabela.VALORCONST); // @TODOVITAO AQUI DEVERIA SER E()
            casaToken(tabela.TO);
            /*
             * if(simbolo.getToken() == tabela.ID) { casaToken(tabela.ID);
             * acaoSemantica3(simboloParaAnalise); simboloid2 = simboloParaAnalise;
             * acaoSemantica32(simboloEfor,simboloid2,simboloId); } else {
             */
            simboloE2for = E();
            // geracaoMemoria.zerarTemp();
            // casaToken(tabela.VALORCONST); // @TODOVITAO AQUI DEVERIA SER E()

            // }
            if (simbolo.getToken() == tabela.STEP) {
               casaToken(tabela.STEP);
               casaToken(tabela.VALORCONST); // @TODO Como pegar o num ?
               // acaoSemantica3(simboloParaAnalise);
               // acaoSemantica36(simboloParaAnalise);
               // acaoSemantica34(); // n�o implementada a 34
            }
            casaToken(tabela.DO);
            H(rotInicio, simboloId);

         } else if (simbolo.getToken() == tabela.IF) {
            casaToken(tabela.IF);
            simboloEif = E();
            // geracaoMemoria.zerarTemp();

            String rotuloFalse = rotuloPJ.novoRotulo();
            String rotuloFim = rotuloPJ.novoRotulo();

            casaToken(tabela.THEN);
            J(rotuloFalse, rotuloFim);

            // casaToken(tabela.PV);
         } else if (simbolo.getToken() == tabela.PV) {
            casaToken(tabela.PV);
         } else if (simbolo.getToken() == tabela.READLN) {
            casaToken(tabela.READLN);
            casaToken(tabela.APAR);
            casaToken(tabela.ID);
            simboloId = simboloParaAnalise;
            condicao = acaoSemantica9();
            if (simbolo.getToken() == tabela.ACOL) {
               casaToken(tabela.ACOL);

               simboloEvet = E();

               // acaoSemantica41(simboloId,simboloEvet); nao eh mais necessaria
               casaToken(tabela.FCOL);
            }

            casaToken(tabela.FPAR);
            casaToken(tabela.PV);
         } else if (simbolo.getToken() == tabela.WRITELN) {
            int tempString = geracaoMemoria.novoTemp();
            casaToken(tabela.WRITELN);
            casaToken(tabela.APAR);
            simboloEwr = E();

            // geracaoCodigo22();
            while (simbolo.getToken() == tabela.VIR) {
               tempString = geracaoMemoria.novoTemp();
               casaToken(tabela.VIR);
               simboloEwr = E();

            }
            casaToken(tabela.FPAR);
            casaToken(tabela.PV);

            // geracaoMemoria.zerarTemp();
         } else if (simbolo.getToken() == tabela.WRITE) {
            int tempString = geracaoMemoria.novoTemp();
            casaToken(tabela.WRITE);
            casaToken(tabela.APAR);
            simboloEwr = E();

            // geracaoCodigo22();
            while (simbolo.getToken() == tabela.VIR) {
               tempString = geracaoMemoria.novoTemp();
               casaToken(tabela.VIR);
               simboloEwr = E();

               // geracaoCodigo22();
            }
            casaToken(tabela.FPAR);
            casaToken(tabela.PV);
            // geracaoMemoria.zerarTemp();
         } else {
            tokenInesperado();
         }

      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
      // geracaoMemoria.zerarTemp();
   }

   // A -> = E | '['E']' = E
   void A(Simbolo id) {
      Simbolo simboloA = new Simbolo();
      Simbolo simboloA1 = new Simbolo();
      Simbolo simboloA2 = new Simbolo();

      try {
         checkEOF();

         if (simbolo.getToken() == tabela.ATT) {
            casaToken(tabela.ATT);
            acaoSemantica5(id);
            simboloA = E(); // acaoSemantica49
            // geracaoMemoria.zerarTemp();

         } else {

            casaToken(tabela.ACOL);
            simboloA1 = E();
            // geracaoMemoria.zerarTemp();

            casaToken(tabela.FCOL);

            casaToken(tabela.ATT);
            simboloA2 = E(); // acaoSemantica49
            // geracaoMemoria.zerarTemp();

         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   // H -> C | '{' {C} '}'
   void H(String rotInicio, Simbolo contador) {
      try {
         checkEOF();

         if (simbolo.getToken() == tabela.ACHAVE) {
            casaToken(tabela.ACHAVE);
            while (ehComando()) {
               C();
            }

            casaToken(tabela.FCHAVE);
         } else {
            C();

         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

   // C1 -> (begin {C} endif | C) [else (begin {C} endelse | C)]
   void C1() {
      try {
         checkEOF();

         if (simbolo.getToken() == tabela.ACHAVE) {
            casaToken(tabela.ACHAVE);
            do {
               C();

            } while (ehComando());

            casaToken(tabela.FCHAVE);
            if (simbolo != null && simbolo.getToken() == tabela.ELSE) { // caso o opcional seja no EOF
               casaToken(tabela.ELSE);

               if (simbolo.getToken() == tabela.ACHAVE) {
                  casaToken(tabela.ACHAVE);
                  do {
                     C();
                  } while (ehComando());
                  casaToken(tabela.FCHAVE);
               } else {
                  C();
               }
            }
         } else {
            C();

            if (simbolo != null && simbolo.getToken() == tabela.ELSE) {
               casaToken(tabela.ELSE);

               if (simbolo.getToken() == tabela.ACHAVE) {
                  casaToken(tabela.ACHAVE);
                  C();
                  casaToken(tabela.FCHAVE);
               } else {
                  C();
               }
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