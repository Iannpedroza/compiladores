public class Simbolo {
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
 