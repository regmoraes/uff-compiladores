/**
 * Created by romulo-eduardo on 9/21/14.
 */
public class Token {

    String classe;
    String lexema;
    int linha;

    public Token(String classe, String lexema, int linha) {
        this.classe = classe;
        this.lexema = lexema;
        this.linha = linha;
    }

    public String getClasse() {
        return classe;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }
}
