import java.util.Arrays;
import java.util.List;
import java.io.*;

public class AnalisadorLexico {
   public TabelaDeSimbolo simbolos;
   public AnalisadorLexico(TabelaDeSimbolo tabela){
      this.simbolos = tabela;
   }

   String lexema = "";
   public boolean devolve = false;
   Simbolo simb;
   char c;
   public static int linha = 0;
   public boolean ehComentario = false;
   public boolean ehEOF = false;
   public static int estadoInicial = 0;
   public static char validos[] = {' ' ,'_' ,  '.' ,  ',' ,  ';' ,  '&' ,  ':' ,  '(' ,  ')' ,  '[' ,  ']' , '{','}', '+' ,  '-' ,  '"' ,  '\'' , '/',  '!' ,  '?' ,  '>' ,  '<' ,  '=' , '\n'};
   
   
   Simbolo analisarLexema(boolean devolucao, BufferedReader arquivo) throws Exception {
      //BufferedReader arquivo2 = null;
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
                  c = (char) arquivo.read();
               
               }
               devolucao = false;
               //checkEOF(c);
            // Quebra de linha no arquivo
               if (c == '\n') { // @TODO entender o char 11
                  linha++;
               } else if (isLetra(c) || c == '_') {
               // lendo identificador
                  lexema += c;
                  estadoAtual = 5;
               } else if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')' || c == ';' || c == ',' || c == '=') {
               // LÃƒÂª os tokens que possuem somente 1 caractere e vao para o estado final
               // Nada ÃƒÂ© devolvido
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else if (c == 32 || c == 11 || c == 8 || c == 13 || c == 9) {
               // lendo "lixo" espaÃƒÂ§o em branco, enter tabs vertical e horizontal
                  estadoAtual = estadoInicial;
               } else if (c == '/') {
                  lexema += c;
                  estadoAtual = 14;
               } else if (c == '>' || c == '!') {
               // Possui 4 variacoes: '>' e '>=' e '!' e '!=', vai para o proximo estado para decidir qual o
               // token
                  lexema += c;
                  estadoAtual = 2;
               } else if(c == '<'){
                  lexema += c;
                  estadoAtual = 6;
               } else if(c == '&'){
                  lexema += c;
                  // estadoAtual = (criar estado de acordo com o nosso automato)
               } else if (c == '=') { // Possui 2 variacoes: '=' e '==', vai para o proximo
                  //estado para decidir qual o // token 
                  lexema += c; 
               
               } else if (c == '\'') {
               // Constante do tipo 'constante'
               //avaliar se deve ser retirado
                  lexema += c;
                  estadoAtual = 11;
               } else if (c == '"' || c == '\u00E2' || c == '\u20AC' || c == '\u0153') {
               // Constante do tipo "constante"
                  lexema += c;
                  estadoAtual = 12;
               } else if (isDigito(c)) {
                  if (c == '0') {
                  // Constante do tipo 0x(hexa)(hexa)
                     lexema += c;
                     estadoAtual = 7;
                  } else {
                  // Numero nao comecado por 0
                     lexema += c;
                     estadoAtual = 10;
                  }
               } else if (c == '-') {
                  lexema += c;
                  //estado definir
               } else if (c == 65535) {
                  //verificar com luigi
                  estadoAtual = estadoFinal;
                  lexema += c;
                  ehEOF = true;
                  devolve = false;
                  arquivo.close();
               } else {
                  //System.err.println(linha + ":Caractere invalido");
                  System.out.println("Erro na linha: " + (linha+1) + ". Lexema nao reconhecido: [" + c +"]");
                  System.exit(0);
               }
               break;
            case 2:
               c = (char) arquivo.read();
            //checkEOF(c);
            
               if (c == '=') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else {
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               }
               break;
            case 3:
               c = (char) arquivo.read();
            //checkEOF(c);
            
               if (c == '=') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else if (c == '>') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else {
                  estadoAtual = estadoFinal;
                  this.devolve = true;
                  devolucao = true;
               }
               break;
            case 4:
               c = (char) arquivo.read();
               //checkEOF(c);
               
               if (c == '\'') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else {
                  estadoAtual = estadoFinal;
                  this.devolve = true;
                  devolucao = true;
               }
               break;
            case 5:
               c = (char) arquivo.read();
               //checkEOF(c);
            
            // Continua no mesmo estado caso letra digito ou sublinhado
               if (isLetra(c) || c == '_' || isDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
                  devolve = true;
               }
               break;
            case 6:
               // Verifica as 3 variações de '<': '<-' , '<', '<='
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (c == '=' || c == '-') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
                  devolve = true;
               }
               break;
            case 7:
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (c == 'x' || c == 'X') {
                  lexema += c;
                  estadoAtual = 8;
               } else if (isDigito(c)) {
                  lexema += c;
                  estadoAtual = 10;
               } else {
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               }
               break;
            case 8:
               c = (char) arquivo.read();
               //checkEOF(c);
            
            // @TODO Aceitar todas as letras, e tratar o resultado no analisador sintatico ?
               if (Character.digit(c, 16) >= 0) {
                  lexema += c;
                  estadoAtual = 9;
               } else {
                  printErrorHexa();
               }
               break;
            case 9:
               c = (char) arquivo.read();
               //checkEOF(c);
            
            // @TODO Aceitar todas as letras, e tratar o resultado no analisador sintatico ?
               if (Character.digit(c, 16) >= 0) {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               }else {
                  printErrorHexa();
               }
               break;
            case 10:
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (isDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               }
               break;
            case 11:
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (isDigito(c) || isLetra(c) || isValido(c)) {
                  lexema += c;
                  estadoAtual = 4;
               } 
               break;
            case 12:
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (isValido(c)) {
                  lexema += c;
                  estadoAtual = 13;
               }
               break;
            case 13:
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (isValido(c)) {
                  lexema += c;
               } else if (c == '"' || c == '\u00E2' || c == '\u20AC' || c == '\u0153') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else {
                  printErrorCaracter();
               }
               break;
            case 14:
               c = (char) arquivo.read();
               //checkEOF(c);
            
               if (c != '*') {
                  estadoAtual = estadoFinal;
                  devolucao = true;
                  devolve = true;
               } else {
                  estadoAtual = 15;
                  ehComentario = true;
               }
               break;
            case 15:
               c = (char) arquivo.read();
               checkEOF(c);
               //System.out.println(c);
               if (c == '*')
                  estadoAtual = 16;
               break;
            case 16:
               c = (char) arquivo.read();
               //checkEOF(c);
               if (c == '*'){
                  estadoAtual = 16;
               } else if (c == '/') {
                  estadoAtual = estadoInicial;
                  lexema = "";
                  ehComentario = false;
               } else
                  estadoAtual = 15;
               break;
         }
      }
      simb = null;
      if (!ehEOF) {
         // Seleciona o simbolo da tabela de simbolos caso ele ja esteja na tabela
         if (simbolos.tabela.get(lexema != null) {
            simb = simbolos.tabela.get(lexema);
         } else if (isLetra(lexema.charAt(0)) || lexema.charAt(0) == '.' || lexema.charAt(0) == '_') {
            // Insere um novo ID na tabela de simbolos
            simb = simbolos.inserirID(lexema);
         } else if (isDigito(lexema.charAt(0))) {
         
            if (lexema.charAt(0) == '0') {
               if (lexema.length() == 1) {
                  simb = simbolos.inserirConst(lexema, "tipo_inteiro");
               } else {
                  // Constante hexadecimal
                  if (lexema.length() > 2 && (lexema.charAt(1) == 'X' || lexema.charAt(1) == 'x')) {
                     // Constantes hexa sao do tipo 0xFF -> 4 caracteres
                     if (lexema.length() == 4) {
                        // Verifica se os 2 ultimos digitos sao hexadecimais
                        if (Character.digit(lexema.charAt(2), 16) >= 0 && Character.digit(lexema.charAt(3), 16) >= 0) {
                           simb = simbolos.inserirConst(lexema, "tipo_caracter");
                        } else {
                           printError();
                        }
                     }
                  } else {
                     // Verifica se possui algum caracter nao numerico
                     for (int i = 0; i < lexema.length(); i++) {
                        if (!isDigito(lexema.charAt(i))) {
                           printError();
                        }
                     }
                  
                     simb = simbolos.inserirConst(lexema, "tipo_inteiro");
                  }
               
               }
            } else {
               // Verifica se possui algum caracter nao numerico
               for (int i = 0; i < lexema.length(); i++) {
                  if (!isDigito(lexema.charAt(i))) {
                     printError();
                  }
               }
            
               simb = simbolos.inserirConst(lexema, "tipo_inteiro");
            }
         } else if (lexema.charAt(0) == '\'' && lexema.charAt(lexema.length() - 1) == '\'') {
            simb = simbolos.inserirConst(lexema, "tipo_caracter");
         } else if (lexema.charAt(0) == '"' && lexema.charAt(lexema.length() - 1) == '"') {
            String x = lexema.substring(0,lexema.length()-1);
            x += "$";
            x += '"';
            lexema = x;
            simb = simbolos.inserirConst(lexema, "tipo_string");
         } else {
            printError();
         }
      }
   
      return simb;
   }

   public static boolean isLetra(char c) {
      boolean ehLetra = false;
      if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
         ehLetra = true;
      return ehLetra;
   }

   public static boolean isDigito(char c) {
      boolean ehDigito = false;
      if (c >= '0' && c <= '9')
         ehDigito = true;
      return ehDigito;
   }

   public static boolean isValido(char c) {
      return (isLetra(c) || isDigito(c) || new String(validos).indexOf(c) >= 0);
   }

   public void printError() {
      System.out.println("Erro na linha: " + (linha+1) + ". Lexema nao reconhecido: [" + lexema+"]");
      System.exit(1);
   }
   
   public void printErrorCaracter() {
      System.out.println("Erro na linha: " + (linha+1) + ". Caractere nao reconhecido: [" + c +"]");
      System.exit(1);
   }
   
   public void printErrorHexa() {
      System.out.println("Erro na linha: " + (linha+1) + ". Lexema nao reconhecido: [" + lexema +""+ c +"]");
      System.exit(1);
   }
    
   void checkEOF(char c) {
      if (this.ehEOF || c == 65535) {
         System.err.println(this.linha + ":Fim de arquivo nao esperado.");
         System.exit(0);
      }
   }

   public static void main(String[] args) throws Exception {
      try {
         TabelaDeSimbolo tS = new TabelaDeSimbolo();
         AnalisadorLexico aL = new AnalisadorLexico(tS);
         FileReader reader2 = new FileReader("exemplo1.l");
         BufferedReader br2 = new BufferedReader(reader2);
         Simbolo simbol = new Simbolo();
         while (br2.read() != 65535) {
            //simb = aL.analisarLexema(aL.devolve, br);
            simbol = aL.analisarLexema(aL.devolve, br2);
            if (simbol != null) {
               System.out.println(simbol.getToken()+" "+simbol.getLexema());
            }
         }
      } catch (IOException e) {
         System.err.format("IOException: %s%n", e);
      }
   }
}