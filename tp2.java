import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class tp2 {
   public static void main(String[] args) throws Exception {
       // Criação dos objetos Tabela de Simbolos, simbolo e analisador lexico
       TabelaDeSimbolos tabela = new TabelaDeSimbolos();
       AnalisadorLexico analisador = new AnalisadorLexico(tabela);
       Simbolo simbolo = new Simbolo();
       Scanner s = new Scanner(System.in);
       s.useDelimiter(""); // Delimitador para que a classe scanner consiga ler char por char

       while (s.hasNext()) {// enquanto existir entrada na scanner
           try {
               simbolo = analisador.analisaLex(s); // metodo principal de analisar lexema
           } catch (Exception e) {
           }
           if (simbolo != null) {
               System.out.println(simbolo.getToken() + " " + simbolo.getLexema());
           }
       }
       System.out.println(analisador.linha + " linhas compiladas.");
   }

}

class AnalisadorSintatico {
   AnalisadorLexico lexico;
   TabelaDeSimbolos tabela;
   Simbolo s, simboloParaAnalise;
   Rotulo rotuloPJ;
   int endereco = geracaoMemoria.contador;
   
   private int procFend = 0;
   private int procTend = 0;
   private int procExpsend = 0;
   private int procExpend = 0;

   AnalisadorSintatico(BufferedReader arquivo,String arq) {
      try {
         this.arquivo = arquivo;
         tabela = new TabelaDeSimbolos();
         lexico = new AnalisadorLexico(tabela);
         rotuloPJ = new Rotulo();
         
         s = lexico.analisarLexema(lexico.devolve, arquivo);
         if (s == null) { // comentario
            s = lexico.analisarLexema(lexico.devolve, arquivo);
         }
      } catch (Exception e) {
         checkEOF();
         System.out.print(e.getMessage());
      }
   }

   void casaToken(byte token) {
      try {
         if (s != null) {
            if (s.getToken() == token) {
               simboloParaAnalise = s;
               s = lexico.analisarLexema(lexico.devolve, arquivo);
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
         if (s != null) {
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
   
   // D -> VAR {(integer | char) id [D'] ';'}+ | CONST id( = CONSTV' | '['num']' = '"' string '"') ';'
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
         if (s.getToken() == tabela.VAR) {
            casaToken(tabela.VAR);
            while(s.getToken() == tabela.INTEGER ||s.getToken() == tabela.CHAR){
               if (s.getToken() == tabela.INTEGER) {
                  casaToken(tabela.INTEGER);
                  
               
               } else {
                  casaToken(tabela.CHAR);
                  
               }
               casaToken(tabela.ID);
               Simbolo id = simboloParaAnalise;
               Simbolo retornoD1 = D1(simboloParaAnalise);
               casaToken(tabela.PV);
               if(retornoD1.getToken() == -1){
                  condGC=true;
               } else {
                  condGC=false;
               }
            
             
            }
         } else if (s.getToken() == tabela.CONST) { //CONST id( = CONSTV' | '['num']' = '"' string '"') ';'
            casaToken(tabela.CONST);
            casaToken(tabela.ID);
            simboloId = simboloParaAnalise;
            if (s.getToken() == tabela.ATT) {
               if (s.getToken() == tabela.ATT) {
                  casaToken(tabela.ATT);
                  simboloConst = CONSTV();
                  c = lexico.simbolos.buscaSimbolo(simboloId.getLexema());
                  c.setTipo(simboloConst.getTipo());// acaoSemantica53
                  
               }
            } else {
               casaToken(tabela.ACOL);
               casaToken(tabela.VALORCONST); //@TODO NUM
               simboloEconst = simboloParaAnalise;
               casaToken(tabela.FCOL);
               casaToken(tabela.ATT);
               // casaToken(tabela.ASPAS);
               casaToken(tabela.VALORCONST); // @TODO STRING
               simboloString = simboloParaAnalise;
               // casaToken(tabela.ASPAS);
             
            }
            casaToken(tabela.PV);
         }else {
            tokenInesperado();
         }
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   }

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
         if (s.getToken() == tabela.ATT || s.getToken() == tabela.ACOL || s.getToken() == tabela.VIR) {
            if (s.getToken() == tabela.ATT) {
               casaToken(tabela.ATT);
               temp = CONSTV();
               D1 = temp;
               if (s.getToken() == tabela.VIR) {
                  while (s.getToken() != tabela.PV) {
                     casaToken(tabela.VIR);
                     casaToken(tabela.ID);
                     simboloId = simboloParaAnalise;
                     if (s.getToken() == tabela.ACOL || s.getToken() == tabela.ATT) {
                        if (s.getToken() == tabela.ACOL) {
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
               /*if(condGC == false){
                  geracaoCodigo4(retornoD1, id);
               }*/
            } else if (s.getToken() == tabela.VIR) {
               D1.setToken((byte)999); // pois assim numa concatenacao retorna algo <> de -1 
               while (s.getToken() != tabela.PV) {
                  casaToken(tabela.VIR);
                  casaToken(tabela.ID);
                  simboloId = simboloParaAnalise;
                  if (s.getToken() == tabela.ACOL || s.getToken() == tabela.ATT) {
                     if (s.getToken() == tabela.ACOL) {
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
            } else if (s.getToken() == tabela.ACOL) {
               casaToken(tabela.ACOL);
               casaToken(tabela.VALORCONST);
               simboloEvet = simboloParaAnalise;
               D1 = simboloEvet;
               
               casaToken(tabela.FCOL);
              
               if (s.getToken() == tabela.VIR) {
                  while (s.getToken() != tabela.PV) {
                     casaToken(tabela.VIR);
                     casaToken(tabela.ID);
                     
                     simboloId = simboloParaAnalise;
                     
                     if (s.getToken() == tabela.ACOL || s.getToken() == tabela.ATT) {
                        condGC = acaoSemantica9();
                        if (s.getToken() == tabela.ACOL) {
                           casaToken(tabela.ACOL);
                           casaToken(tabela.VALORCONST);
                           simboloEvet = simboloParaAnalise;
                           //simboloEvet = E();
                          
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
         if (s.getToken() == tabela.VALORCONST) {
            casaToken(tabela.VALORCONST);
            constvSimbolo = simboloParaAnalise; // NOVA acaoSemantica44 e //acaoSemantica45 sera que da certo?
            //constvSimbolo.setTipo(simboloParaAnalise.getTipo()); // acaoSemantica44 e //acaoSemantica45
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
         if (s.getToken() == tabela.VALORCONST) { // @TODO Como pegar o 0 ?
            casaToken(tabela.VALORCONST); // HEXA
            // casaToken(tabela.X); // @TODO Como pegar o X ?
            // casaToken(tabela.HEXA); // @TODO Como pegar os hexa ?
            // casaToken(tabela.HEXA); // @TODO Como pegar os hexa ?
         } else if (s.getToken() == tabela.CHAR) {
            casaToken(tabela.CHAR);
         } else if (s.getToken() == tabela.SUB || s.getToken() == tabela.VALORCONST) {
            if (s.getToken() == tabela.SUB) {
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
         if (s.getToken() == tabela.ID) {
            casaToken(tabela.ID);
           
            A(simboloParaAnalise);
            casaToken(tabela.PV);
         } else if (s.getToken() == tabela.FOR) {
            casaToken(tabela.FOR);
            casaToken(tabela.ID);
            simboloId = simboloParaAnalise;
            casaToken(tabela.ATT);
            simboloEfor = E();
            String rotInicio = rotuloPJ.novoRotulo(); // GeracaoCodigo28
            String rotFim = rotuloPJ.novoRotulo(); // GeracaoCodigo28
            //geracaoMemoria.zerarTemp();

            // casaToken(tabela.VALORCONST); // @TODOVITAO AQUI DEVERIA SER E()
            casaToken(tabela.TO);
            /*
             * if(s.getToken() == tabela.ID) { casaToken(tabela.ID);
             * acaoSemantica3(simboloParaAnalise); simboloid2 = simboloParaAnalise;
             * acaoSemantica32(simboloEfor,simboloid2,simboloId); } else {
             */
            simboloE2for = E();
            //geracaoMemoria.zerarTemp();
            // casaToken(tabela.VALORCONST); // @TODOVITAO AQUI DEVERIA SER E()
           
            // }
            if (s.getToken() == tabela.STEP) {
               casaToken(tabela.STEP);
               casaToken(tabela.VALORCONST); // @TODO Como pegar o num ?
               // acaoSemantica3(simboloParaAnalise);
               // acaoSemantica36(simboloParaAnalise);
               // acaoSemantica34(); // n�o implementada a 34
            }
            casaToken(tabela.DO);
            H(rotInicio, simboloId);
            
         } else if (s.getToken() == tabela.IF) {
            casaToken(tabela.IF);
            simboloEif = E();
            //geracaoMemoria.zerarTemp();
           
            String rotuloFalse = rotuloPJ.novoRotulo();
            String rotuloFim = rotuloPJ.novoRotulo();
           
            casaToken(tabela.THEN);
            J(rotuloFalse, rotuloFim);
           
            // casaToken(tabela.PV);
         } else if (s.getToken() == tabela.PV) {
            casaToken(tabela.PV);
         } else if (s.getToken() == tabela.READLN) {
            casaToken(tabela.READLN);
            casaToken(tabela.APAR);
            casaToken(tabela.ID);
            simboloId = simboloParaAnalise;
            condicao = acaoSemantica9();
            if (s.getToken() == tabela.ACOL) {
               casaToken(tabela.ACOL);
             
               simboloEvet = E();
              
               // acaoSemantica41(simboloId,simboloEvet); nao eh mais necessaria
               casaToken(tabela.FCOL);
            }
            
            casaToken(tabela.FPAR);
            casaToken(tabela.PV);
         } else if (s.getToken() == tabela.WRITELN) {
            int tempString = geracaoMemoria.novoTemp();
            casaToken(tabela.WRITELN);
            casaToken(tabela.APAR);
            simboloEwr = E();
           
            //geracaoCodigo22();
            while (s.getToken() == tabela.VIR) {
               tempString = geracaoMemoria.novoTemp();
               casaToken(tabela.VIR);
               simboloEwr = E();
               
            }
            casaToken(tabela.FPAR);
            casaToken(tabela.PV);
            
            //geracaoMemoria.zerarTemp();
         } else if (s.getToken() == tabela.WRITE) {
            int tempString = geracaoMemoria.novoTemp();
            casaToken(tabela.WRITE);
            casaToken(tabela.APAR);
            simboloEwr = E();
            
            //geracaoCodigo22();
            while (s.getToken() == tabela.VIR) {
               tempString = geracaoMemoria.novoTemp();
               casaToken(tabela.VIR);
               simboloEwr = E();
             
               //geracaoCodigo22();
            }
            casaToken(tabela.FPAR);
            casaToken(tabela.PV);
            //geracaoMemoria.zerarTemp();
         } else {
            tokenInesperado();
         }
      
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
      //geracaoMemoria.zerarTemp();
   }

   // A -> = E | '['E']' = E
   void A(Simbolo id) {
      Simbolo simboloA = new Simbolo();
      Simbolo simboloA1 = new Simbolo();
      Simbolo simboloA2 = new Simbolo();
   
      try {
         checkEOF();
      
         if (s.getToken() == tabela.ATT) {
            casaToken(tabela.ATT);
            acaoSemantica5(id);
            simboloA = E(); // acaoSemantica49
            //geracaoMemoria.zerarTemp();
           
         } else {
            
            casaToken(tabela.ACOL);
            simboloA1 = E();
            //geracaoMemoria.zerarTemp();
           
            casaToken(tabela.FCOL);
            
            casaToken(tabela.ATT);
            simboloA2 = E(); // acaoSemantica49
            //geracaoMemoria.zerarTemp();
            
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
      
         if (s.getToken() == tabela.ACHAVE) {
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

   // J -> C [else ('{' {C} '}' || C)] | '{' {C} '}' [else ('{' {C} '}' || C)]
   void J(String rotuloFalse, String rotuloFim) {
      try {
         checkEOF();
      
         if (s.getToken() == tabela.ACHAVE) {
            casaToken(tabela.ACHAVE);
            do {
               C();
            
            } while (ehComando());
           
            casaToken(tabela.FCHAVE);
            if (s != null && s.getToken() == tabela.ELSE) { // caso o opcional seja no EOF
               casaToken(tabela.ELSE);
              
               if (s.getToken() == tabela.ACHAVE) {
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
           
            if (s != null && s.getToken() == tabela.ELSE) {
               casaToken(tabela.ELSE);
              
               if (s.getToken() == tabela.ACHAVE) {
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
      Simbolo simboloCloneE = new Simbolo();
      Simbolo simboloCloneE2 = new Simbolo();
      boolean condicao;
      int operacao = 0; /* 1 > / 2 < / 3 >=  / 4 <= / 5 <> / 6 = */
      try {
         checkEOF();
      
         simboloE = E1(); // acaoSemantica7
         procExpend = procExpsend; // geracaoCodigo17
         simboloCloneE = new Simbolo(simboloE.getToken(), simboloE.getLexema(), simboloE.getEndereco(),
               simboloE.getTipo(), simboloE.getClasse(), simboloE.getTamanho());
         if (s.getToken() == tabela.MAIOR || s.getToken() == tabela.MENOR || s.getToken() == tabela.MAIORIG
               || s.getToken() == tabela.MENORIG || s.getToken() == tabela.DIFF || s.getToken() == tabela.ATT) {
           
            /* 1 > / 2 < / 3 >=  / 4 <= / 5 <> / 6 = */
            if (s.getToken() == tabela.MAIOR) {
               casaToken(tabela.MAIOR);
               
               operacao = 1;
            } else if (s.getToken() == tabela.MENOR) {
               casaToken(tabela.MENOR);
               
               operacao = 2;
            } else if (s.getToken() == tabela.MAIORIG) {
               casaToken(tabela.MAIORIG);
             
               operacao = 3;
            } else if (s.getToken() == tabela.MENORIG) {
               casaToken(tabela.MENORIG);
            
               operacao = 4;
            } else if (s.getToken() == tabela.DIFF) {
               casaToken(tabela.DIFF);
               
               operacao = 5;
            } else if (s.getToken() == tabela.ATT) {
               casaToken(tabela.ATT);
              
               operacao = 6;
            }
         
            simboloE2 = E1();
            simboloCloneE2 = new Simbolo(simboloE2.getToken(), simboloE2.getLexema(), simboloE2.getEndereco(),
                  simboloE2.getTipo(), simboloE2.getClasse(), simboloE2.getTamanho());
         
            simboloCloneE.setTipo("tipo_logico"); // acaoSemantica47
            return simboloCloneE;
         }
      
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   
      return simboloE;
   }

   // E' -> [+ | -] E'' {('+' | '-' | or ) E''}
   Simbolo E1() {
      Simbolo simboloE1 = new Simbolo();
      Simbolo simboloE1_2 = new Simbolo();
      Simbolo simboloCloneE1 = new Simbolo();
      Simbolo simboloCloneE1_2 = new Simbolo();
      boolean condicao;
      int operacao = 0; /* 1 para add , 2 para sub , 3 para or, 0 default */
      try {
         checkEOF();
        
         if (s.getToken() == tabela.ADD) {
            casaToken(tabela.ADD);
            
         } else if (s.getToken() == tabela.SUB) {
            casaToken(tabela.SUB);
           
         }
         simboloE1 = E2(); // acaoSemantica14
         
         
         procExpsend = procTend;
         simboloCloneE1 = new Simbolo(simboloE1.getToken(), simboloE1.getLexema(), simboloE1.getEndereco(),
               simboloE1.getTipo(), simboloE1.getClasse(), simboloE1.getTamanho());
        
         while (s.getToken() == tabela.ADD || s.getToken() == tabela.SUB || s.getToken() == tabela.OR
               || s.getToken() == tabela.MUL) {
            // if (s.getToken() == tabela.ADD || s.getToken() == tabela.SUB || s.getToken()
            // == tabela.OR) {
            if (s.getToken() == tabela.ADD) {
               casaToken(tabela.ADD);
               operacao = acaoSemantica15(simboloE1);
            } else if (s.getToken() == tabela.SUB) {
               casaToken(tabela.SUB);
               operacao = acaoSemantica16(simboloE1);
            } else {
               casaToken(tabela.OR);
               operacao = acaoSemantica17(simboloE1);
            }
            int Tend = procTend;
            simboloE1_2 = E2();
            simboloCloneE1_2 = new Simbolo(simboloE1_2.getToken(), simboloE1_2.getLexema(), simboloE1_2.getEndereco(),
                  simboloE1_2.getTipo(), simboloE1_2.getClasse(), simboloE1_2.getTamanho());
         
         }
      
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   
      return simboloE1;
   
   }

   // E'' -> F {('*' | '/' | '%' | and) F}
   Simbolo E2() {
      Simbolo simboloE2 = new Simbolo();
      Simbolo simboloE2_1 = new Simbolo();
      Simbolo simboloCloneE2 = new Simbolo();
      Simbolo simboloCloneE2_1 = new Simbolo();
      int operador = 0;
      
      try {
         checkEOF();
      
         simboloE2 = F(); // acaoSemantica20
         simboloCloneE2 = new Simbolo(simboloE2.getToken(), simboloE2.getLexema(), simboloE2.getEndereco(),
               simboloE2.getTipo(), simboloE2.getClasse(), simboloE2.getTamanho());
         procTend = procFend; // geracaoCodigo14
         while (s.getToken() == tabela.MUL || s.getToken() == tabela.DIV || s.getToken() == tabela.MOD
               || s.getToken() == tabela.AND) {
            // if (s.getToken() == tabela.MUL || s.getToken() == tabela.DIV || s.getToken()
            // == tabela.MOD || s.getToken() == tabela.AND) {
            if (s.getToken() == tabela.MUL) {
               casaToken(tabela.MUL);
               
            } else if (s.getToken() == tabela.DIV) {
               casaToken(tabela.DIV);
             
            } else if (s.getToken() == tabela.MOD) {
               casaToken(tabela.MOD);
              
            } else {
               casaToken(tabela.AND);
             
            }
            simboloE2_1 = F();
            simboloCloneE2_1 = new Simbolo(simboloE2_1.getToken(), simboloE2_1.getLexema(), simboloE2_1.getEndereco(),
                  simboloE2_1.getTipo(), simboloE2_1.getClasse(), simboloE2_1.getTamanho());
           
         }
      
      } catch (Exception e) {
         checkEOF();
         System.err.println(e.toString());
      }
   
      return simboloE2;
   
   }

   // F -> '(' E ')' | not F | id ['[' E ']']| num
   Simbolo F() {
      Simbolo simboloF = new Simbolo(); // simbolo que vai ser retornado
      Simbolo simboloCloneF = new Simbolo();
      Simbolo simboloF1 = new Simbolo();
      Simbolo simboloE = new Simbolo();
      try {
      
         checkEOF();
      
         if (s.getToken() == tabela.APAR) {
            casaToken(tabela.APAR);
            simboloF = E(); // acaoSemantica27
            procFend = simboloF.getEndereco();
            casaToken(tabela.FPAR);
         } else if (s.getToken() == tabela.NOT) {
            casaToken(tabela.NOT);
            int Fend = procFend;
            simboloF1 = F();
          
            return simboloF1;
         } else if (s.getToken() == tabela.VALORCONST) {
            casaToken(tabela.VALORCONST);
            // \/ acaoSemantica29
            simboloF = new Simbolo(simboloParaAnalise.getToken(), simboloParaAnalise.getLexema(),
                  simboloParaAnalise.getEndereco(), simboloParaAnalise.getTipo(), "classe_variavel", 0);
                 
            
            
         } else {
            casaToken(tabela.ID);
            
            // acaoSemantica30 
            simboloF = lexico.simbolos.buscaSimbolo(simboloParaAnalise.getLexema());
            procFend = simboloF.getEndereco(); // geracaoCodigo9
            if (s.getToken() == tabela.ACOL) {
               casaToken(tabela.ACOL);
              
               // casaToken(tabela.VALORCONST); @TODO VITAO
               simboloE = E();
               
               simboloCloneF = new Simbolo(simboloF.getToken(), simboloF.getLexema(), simboloF.getEndereco(),
                     simboloF.getTipo(), simboloF.getClasse(), simboloF.getTamanho());
               
               casaToken(tabela.FCOL);
               
               return simboloCloneF;
            }
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
      System.err.println((lexico.linha + 1) + ":Token nao esperado: " + s.getLexema());
      System.exit(0);
   }

   boolean ehDeclaracao() {
      return (s != null && (s.getToken() == tabela.VAR || s.getToken() == tabela.CONST || s.getToken() == tabela.INTEGER
            || s.getToken() == tabela.CHAR));
   }

   boolean ehComando() {
      return (s != null && (s.getToken() == tabela.ID || s.getToken() == tabela.FOR || s.getToken() == tabela.IF
            || s.getToken() == tabela.PV || s.getToken() == tabela.READLN || s.getToken() == tabela.WRITELN
            || s.getToken() == tabela.WRITE));
   }

}