import java.util.HashMap;

public class TabelaDeSimbolos {
   public HashMap<String, Simbolo> tabela = new HashMap<>();
   public static int endereco = -1;
    
   public final byte IF = 0;
   public final byte INT = 1;
   public final byte BYTE = 2; 
   public final byte WRITELN = 3;
   public final byte WRITE = 4;
   public final byte READLN = 5;
   public final byte ELSE = 6;
   public final byte ACHAVE = 7; // {
   public final byte FCHAVE = 8; // }
   public final byte ACOL = 9; // [
   public final byte FCOL = 10; // ]
   public final byte APAR = 11; // (
   public final byte FPAR = 12; // )
   public final byte PONTOVIRGULA = 13;
   public final byte ATT = 14;
   public final byte OR = 15;
   public final byte MAIOR = 16;
   public final byte MENOR = 17;
   public final byte MAIORIG = 18;
   public final byte MENORIG = 19;
   public final byte DIFF = 20;
   public final byte VIRGULA = 21;
   public final byte SUM = 22;
   public final byte SUB = 23;
   public final byte DIV = 24;
   public final byte MULT = 25;
   public final byte TRUE = 26;
   public final byte FALSE = 27;
   public final byte BOOLEAN = 28;
   public final byte ASPAS = 29;
   public final byte APOST = 30;
   public final byte ID = 31;
   public final byte VALORCONST = 32;
   public final byte FINAL = 33;
   public final byte STRING = 34;
   public final byte WHILE = 35;
   public final byte AND = 36;
   public final byte NEG = 37;
   public final byte BEGIN = 38;
   public final byte ENDWHILE = 39;
   public final byte ENDIF = 40;
   public final byte ENDELSE = 41;
   public final byte COMPARA = 42;            
	
   public TabelaDeSimbolos() {
      tabela.put("if", new Simbolo(IF,"if", ++endereco));
      tabela.put("int", new Simbolo(INT,"int", ++endereco));
      tabela.put("writeln", new Simbolo(WRITELN,"writeln", ++endereco));
      tabela.put("write", new Simbolo(WRITE,"write", ++endereco));
      tabela.put("readln", new Simbolo(READLN,"readln", ++endereco));
      tabela.put("else", new Simbolo(ELSE,"else", ++endereco));
      tabela.put("{", new Simbolo(ACHAVE,"{", ++endereco));
      tabela.put("}", new Simbolo(FCHAVE,"}", ++endereco));
      tabela.put("[", new Simbolo(ACOL,"[", ++endereco));
      tabela.put("]", new Simbolo(FCOL,"]", ++endereco));
      tabela.put("(", new Simbolo(APAR,"(", ++endereco));
      tabela.put(")", new Simbolo(FPAR,")", ++endereco));
      tabela.put(";", new Simbolo(PONTOVIRGULA,";", ++endereco));
      tabela.put("<-", new Simbolo(ATT,"<-", ++endereco));
      tabela.put(">", new Simbolo(MAIOR,">",++endereco));
      tabela.put("<", new Simbolo(MENOR,"<", ++endereco));
      tabela.put(">=", new Simbolo(MAIORIG,">=", ++endereco));
      tabela.put("<=", new Simbolo(MENORIG,"<=", ++endereco));
      tabela.put("!=", new Simbolo(DIFF,"!=", ++endereco));
      tabela.put(",", new Simbolo(VIRGULA,",", ++endereco));
      tabela.put("+", new Simbolo(SUM,"+", ++endereco));
      tabela.put("-", new Simbolo(SUB,"-", ++endereco));
      tabela.put("/", new Simbolo(DIV,"/", ++endereco));
      tabela.put("*", new Simbolo(MULT,"*", ++endereco));
      tabela.put("boolean", new Simbolo(BOOLEAN,"boolean", ++endereco));   
      tabela.put("'", new Simbolo(APOST,"'", ++endereco));   
      tabela.put("\"", new Simbolo(ASPAS,"\"", ++endereco));
      tabela.put("final", new Simbolo(FINAL,"final", ++endereco)); 
      tabela.put("string", new Simbolo(STRING,"string", ++endereco));
      tabela.put("while", new Simbolo(WHILE,"while", ++endereco));
      tabela.put("&&", new Simbolo(AND,"&&", ++endereco)); 
      tabela.put("||", new Simbolo(OR,"||", ++endereco));
      tabela.put("!", new Simbolo(NEG,"!", ++endereco));
      tabela.put("begin", new Simbolo(BEGIN,"begin", ++endereco)); 
      tabela.put("endwhile", new Simbolo(ENDWHILE,"endwhile", ++endereco)); 
      tabela.put("endif", new Simbolo(ENDIF,"endif", ++endereco));
      tabela.put("endelse", new Simbolo(ENDELSE,"endelse", ++endereco));
      tabela.put("=", new Simbolo(COMPARA,"=", ++endereco));                    
   }

   public String pesquisa(String lexema){
        Simbolo aux = tabela.get(lexema);
        return ((aux == null) ? "NULL" : ""+aux.getEnd());
   }
  
   public Simbolo inserirID(String lexema){
       Simbolo simbolo = new Simbolo(ID,lexema, ++endereco);
       tabela.put(lexema, simbolo);
       return tabela.get(lexema);
   }
    
   public Simbolo buscaSimbolo(String lexema){
    Simbolo aux = tabela.get(lexema);
    return ((aux == null) ? null : tabela.get(lexema));

   }

  
   public Simbolo inserirConst(String lexema, String tipo){
       Simbolo simbolo = new Simbolo(VALORCONST, lexema, ++endereco, tipo);
       tabela.put(lexema, simbolo);
       return tabela.get(lexema);
    }

   public static void main(String[] args){
       TabelaDeSimbolos tbl = new TabelaDeSimbolos();

       System.out.println(tbl.pesquisa("if"));
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
    }

    class Simbolo {
        private String lexema = "";
        private byte token;
        private String classe = "";
        private String tipo = "";
        private int tamanho = 0;
        private int endereco;
        
        public Simbolo(){
           this.token = -1;
        }
           
        public Simbolo(byte token, String lexema, int endereco){
           this.lexema = lexema;
           this.token = token;
           this.endereco = endereco;
        }
         
        public Simbolo(byte token, String lexema,int endereco, String tipo){
           this.token = token;
           this.lexema = lexema;
           this.tipo = tipo;
           this.endereco = endereco;
        }
        
        public Simbolo(byte token,String lexema, int endereco, String tipo,String classe, int tamanho) {
           this.token = token;
           this.lexema = lexema;
           this.classe = classe;
           this.tipo = tipo;
           this.endereco = endereco;
           this.tamanho = tamanho;
        }
     
        public byte getToken(){
           return token;
        }
         
        public int getEnd(){
           return endereco;
        }
         
        public String getLexema(){
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
}

