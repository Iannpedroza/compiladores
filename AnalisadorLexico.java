import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Scanner; 

public class AnalisadorLexico {
   public TabelaDeSimbolos simbolos;
   public AnalisadorLexico(TabelaDeSimbolos tabela){
      this.simbolos = tabela;
   }
   char c;
   String lexema = "";
   public boolean devolve = false;
   Simbolo simb;
   public static int linha = 0;
   public boolean ehComentario = false;
   public boolean ehEOF = false;
   public static final int estadoInicial = 0;
   public static char validos[] = {' ' ,'_' ,  '.' ,  ',' ,  ';' ,  '&' ,  ':' ,  '(' ,  ')' ,  '[' ,  ']' , '{','}', '+' ,  '-' ,  '"' ,  '\'' , '/',  '!' ,  '?' ,  '>' ,  '<' ,  '=' , '\n'};
   
   
   Simbolo analisarLexema(boolean devolucao, Scanner s) throws Exception {
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
                  c = s.next().charAt(0);
      
               }
               devolucao = false;
               //checkEOF(c);
            // Quebra de linha no arquivo
               if (c == '\n') { // @TODO entender o char 11
                  linha++;
               } else if (ehLetra(c) || c == '_') {
               // lendo identificador
                  lexema += c;
                  estadoAtual = 5;
               } else if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')' || c == ';'|| c == ',' || c == '=') {
               // LÃƒÂª os tokens que possuem somente 1 caractere e vao para o estado final
               // Nada ÃƒÂ© devolvido
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               } else if (c == 8 || c == 9 || c == 11 || c == 13 || c == 32) {
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
                  estadoAtual = 11;
               } else if (c == '=') { // = apenas para comparacao
                  lexema += c; 
               
               } else if (c == '"') {
               // String
                  lexema += c;
                  estadoAtual = 12;
               } else if (ehDigito(c)) {
                  //Validação de constantes 
                  if (c == '0') {
                  // Hexadecimal ou número iniciado de 0
                     lexema += c;
                     estadoAtual = 7;
                  } else {
                  // Numero nao comecado por 0
                     lexema += c;
                     estadoAtual = 10;
                  }
               }  else if (c == 65535) {
                  //verificar com luigi
                  estadoAtual = estadoFinal;
                  lexema += c;
                  ehEOF = true;
                  devolve = false;
                  //arquivo.close();
               }else if(c == '|'){
                  lexema += c;
                  estadoAtual = 17;

               }else {
                  printErrorCaracter();
               }
               break;
            case 2:
               c = s.next().charAt(0);
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
               c = s.next().charAt(0);
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
               c = s.next().charAt(0);
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
               c = s.next().charAt(0);
               //checkEOF(c);
            
            // Continua no mesmo estado caso letra digito ou sublinhado
               if (ehLetra(c) || c == '_' || ehDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
                  devolve = true;
               }
               break;
            case 6:
               // Verifica as 3 variações de '<': '<-' , '<', '<='
               c = s.next().charAt(0);
               //checkEOF(c);
            
               if (c == '=' || c == '-') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false; //AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
               } else {
                  estadoAtual = estadoFinal;
                  devolucao = true;
                  devolve = true;
               }
               break;
            case 7:
               c = s.next().charAt(0);
               //checkEOF(c);
            
               if (c == 'x') {
                  lexema += c;
                  estadoAtual = 8;
               } else if (ehDigito(c)) {
                  lexema += c;
                  estadoAtual = 10;
               } else {
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               }
               break;
            case 8:
               c = s.next().charAt(0);
               //checkEOF(c);
            
               //VALIDA SE É UM HEXA VALIDO
               if (ehHexa(c)) {
                  lexema += c;
                  estadoAtual = 9;
               } else {//VALIDAR ISSO
                  lexema += c;
                  printError();
               }
               break;
            case 9:
               c = s.next().charAt(0);
               //checkEOF(c);
            
               //VALIDA SE É UM HEXA VALIDO
               if (ehHexa(c)) {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               }else {
                  lexema += c;
                  printError();
               }
               break;
            case 10:
               //VALIDA CONSTANTE
               c = s.next().charAt(0);
               //checkEOF(c);
            
               if (ehDigito(c)) {
                  lexema += c;
               } else {
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               }
               break;
            case 11:
               c = s.next().charAt(0);
               //checkEOF(c);
            
               if (c == '&') {
                  lexema += c;
                  estadoAtual = estadoFinal;
               } else{
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               }
               break;
            case 12:
               //Validação de strings
               c = s.next().charAt(0);
               //checkEOF(c);
            
               if (c == '"') {
                  lexema += c;
                  estadoAtual = estadoFinal;
                  devolve = false;
               
               }else if (c == '\n' || c== '\r') {
                  printError();
               } else if (ehValido(c)) {
                  lexema += c;
               }else {
                  lexema += c;
                  printErrorCaracter();
               }
               break;
            case 13:
               c = s.next().charAt(0);
               //checkEOF(c);
            
               
               break;
            case 14:
               // Cases 14, 15 e 16 validam os comentários e o token '/'
               c = s.next().charAt(0);
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
               c = s.next().charAt(0);
               checkEOF(c);
               //System.out.println(c);
               if (c == '*') {
                  estadoAtual = 16;
               }
               break;
            case 16:
               c = s.next().charAt(0);
               //checkEOF(c);
               if (c == '*'){
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

               if(c == '|'){
                  estadoAtual = estadoFinal;
                  devolve = false;
               } 
               else{
                  estadoAtual = estadoFinal;
                  devolve = true;
                  devolucao = true;
               } 
               
               
         }
      }
      simb = null;
      if (!ehEOF) {
         // Seleciona o simbolo da tabela de simbolos caso ele ja esteja na tabela
         if (simbolos.tabela.get(lexema) != null) {//TRROOOCAAAARRRR
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
            String x = lexema.substring(0,lexema.length()-1);
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
      if (c >= 'A' && c <= 'F' || ehDigito(c)){
         return true;
      }
      return false;
   }

   public static boolean ehLetra(char c) {
      if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'){
         return true;
      }
      return false;
   }

   public static boolean ehDigito(char c) {
      if (c >= '0' && c <= '9'){
         return true;
      }
      return false;
   }

   public static boolean ehValido(char c) {
      return (ehLetra(c) || ehDigito(c) || new String(validos).indexOf(c) >= 0);
   }

   public void printError() {
      System.out.println(linha+1);
      System.out.println("Lexema nao identificado [" +lexema+"].");
      System.exit(1);
   }
   
   public void printErrorCaracter() {
      System.out.println(linha+1);
      System.out.println("caractere invalido.");
      System.exit(1);
   }
   
  /* public void printErrorHexa() {
      System.out.println("Erro na linha: " + (linha+1) + ". Lexema nao reconhecido: [" + lexema +""+ c +"]");
      System.exit(1);
   }*/ //RETIRAR DO ANALIZADOR LEXICOOOOOOOOOOOOOOO
    
   void checkEOF(char c) {
      if (this.ehEOF || c == 65535) {
         System.out.println(linha);
         System.err.println( "fim de arquivo nao esperado.");
         System.exit(0);
      }
   }

   public static void main(String[] args) throws Exception {
      try {
         TabelaDeSimbolos tS = new TabelaDeSimbolos();
         AnalisadorLexico aL = new AnalisadorLexico(tS);
         //FileReader reader2 = new FileReader("exemplo1.l");
        // BufferedReader br2 = new BufferedReader(reader2);
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Scanner s = new Scanner(System.in);
         s.useDelimiter(""); //delimiter ""
         Simbolo simbol = new Simbolo();
         while (s.hasNext()) {
              //simb = aL.analisarLexema(aL.devolve, br);
            simbol = aL.analisarLexema(aL.devolve, s);
            if (simbol != null) {
               System.out.println(simbol.getToken()+" "+simbol.getLexema());
            }
         }
      } catch (IOException e) {
         System.err.format("IOException: %s%n", e);
      }
   }
}
