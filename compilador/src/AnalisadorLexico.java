import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by romulo-eduardo on 9/21/14.
 */
public class AnalisadorLexico {

    private final Character[] delimitadores = {'=', '!', '+', '-', '*', '/', '>', '<', '=', '!', ';', '(', ')', '[', ']', ',', ':'};
    private final List<String> opMatematico = new ArrayList<String>();
    private final List<String> opLogico = new ArrayList<String>();
    private final List<String> arrayTipo = new ArrayList<String>();
    private final List<String> programa = new ArrayList<String>();
    private final List<String> var = new ArrayList<String>();
    private final List<String> constante = new ArrayList<String>();
    private final List<String> funcao = new ArrayList<String>();
    private final List<Character> numero = new ArrayList<Character>();
    private final List<String> bloco = new ArrayList<String>();
    private final List<String> comando = new ArrayList<String>();
    private final List<String> atribuicao = new ArrayList<String>();
    private final List<String> pontoVirgula = new ArrayList<String>();
    private final List<String> parenteses = new ArrayList<String>();
    private final List<String> colchetes = new ArrayList<String>();
    private final List<String> virgula = new ArrayList<String>();
    private final List<String> doisPontos = new ArrayList<String>();
    private List<Token> mTokenArray = new ArrayList<Token>();
    private int linhaCodigo = 1;
    NoToken arvoreTokens = new NoToken(null, null, null);
    private String line;
    private static int i = 0;
    private static Token t;
    private BufferedReader reader;

    public AnalisadorLexico() {

    }

    public void executar() throws IOException {

        setLinguagem();

        reader = new BufferedReader(new FileReader("/home/romulo-eduardo/Documents/Ciência da Computação/Compiladores/Trabalho/Código/codigo2.txt"));
        line = reader.readLine();

        if(line!=null) {
            arvoreTokens = programa(getLexemas(line));

        }else{

            System.out.println("ERRO: Arquivo vazio");
        }

        geraArquivoArvore(arvoreTokens);
    }

    public Token getLexemas(String linhaArquivo) throws IOException {

        List<Character> characterList = new ArrayList<Character>();

        while (i < linhaArquivo.length() && line !=null) {

            if (linhaArquivo.charAt(i) != 32 && linhaArquivo.charAt(i) != 9) {

                if ((linhaArquivo.charAt(i) >= 48 && linhaArquivo.charAt(i) <= 57)
                        || (linhaArquivo.charAt(i) >= 65 && linhaArquivo.charAt(i) <= 90)
                        || (linhaArquivo.charAt(i) >= 97 && linhaArquivo.charAt(i) <= 122)) {

                    characterList.add(linhaArquivo.charAt(i));
                    i++;

                } else {

                    if (characterList.size() != 0) {

                        String lexema = converterParaString(characterList);
                        return geraToken(lexema);

                    } else {

                        if (linhaArquivo.charAt(i) == ':' && linhaArquivo.charAt(i+1) == '=') {
                            i++;
                            return geraToken(":=");

                        } else {
                            for(Character c : delimitadores) {
                                if(linhaArquivo.charAt(i) == c) {

                                    characterList.add(linhaArquivo.charAt(i));
                                    i++;
                                    return geraToken(converterParaString(characterList));
                                }
                            }
                        }
                    }
                }
            }else {

                i++;
                if(characterList.size()!=0) {

                    return geraToken(converterParaString(characterList));

                }else{

                    return getLexemas(line);
                }
            }
        }

        i=0;
        line = reader.readLine();
        linhaCodigo++;
        if(characterList.size()!=0){

            return geraToken(converterParaString(characterList));

        }else{

            return getLexemas(line);
        }

    }

    String converterParaString(List<Character> characterList) {
        StringBuilder builder = new StringBuilder(characterList.size());
        for (Character ch : characterList) {
            builder.append(ch);
        }
        return builder.toString();
    }

    public Token geraToken(String string) {

        for (String rs : opLogico) {

            if (string.equals(rs)) {

                return new Token("Operador Logico", rs, linhaCodigo);
            }
        }

        for (String rs : opMatematico) {

            if (string.equals(rs)) {

                return new Token("Operador Matematico", rs, linhaCodigo);
            }
        }

        for (String rs : arrayTipo) {

            if (string.equals(rs)) {

                return new Token("Tipo", rs, linhaCodigo);
            }
        }

        for (String rs : programa) {

            if (string.equals(rs)) {

                return new Token("Programa", rs, linhaCodigo);
            }
        }

        for (String rs : var) {

            if (string.equals(rs)) {

                return new Token("Variavel", rs, linhaCodigo);
            }
        }

        for (String rs : constante) {

            if (string.equals(rs)) {

                return new Token("Constante", rs, linhaCodigo);
            }
        }

        for (String rs : colchetes) {

            if (string.equals(rs)) {

                return new Token("Colchetes", rs, linhaCodigo);
            }
        }

        for (String rs : pontoVirgula) {

            if (string.equals(rs)) {

                return new Token("Ponto Vírgula", rs, linhaCodigo);
            }
        }

        for (String rs : virgula) {

            if (string.equals(rs)) {

                return new Token("Vírgula", rs, linhaCodigo);
            }
        }

        for (String rs : parenteses) {

            if (string.equals(rs)) {

                return new Token("Parenteses", rs, linhaCodigo);
            }
        }

        for (String rs : doisPontos) {

            if (string.equals(rs)) {

                return new Token("Dois Pontos", rs, linhaCodigo);
            }
        }

        for (String rs : bloco) {

            if (string.equals(rs)) {

                return new Token("Bloco", rs, linhaCodigo);
            }
        }

        for (String rs : atribuicao) {

            if (string.equals(rs)) {

                return new Token("Atribuicao", rs, linhaCodigo);
            }
        }

        for (String rs : funcao) {

            if (string.equals(rs)) {

                return new Token("Função", rs, linhaCodigo);
            }
        }

        for (String rs : parenteses) {

            if (string.equals(rs)) {

                return new Token("Parenteses", rs, linhaCodigo);
            }
        }

        int counter = 0;

        for (Character rs : numero) {
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == rs) {

                    counter++;
                }
            }
        }
        if (counter == string.length()) {
            return new Token("Numero", string, linhaCodigo);
        }

        return new Token("Identificador", string, linhaCodigo);
    }

    public void setLinguagem() {

        opLogico.add("=");
        opLogico.add(">");
        opLogico.add("<");
        opLogico.add("!");

        opMatematico.add("+");
        opMatematico.add("-");
        opMatematico.add("*");
        opMatematico.add("/");

        arrayTipo.add("float");
        arrayTipo.add("real");
        arrayTipo.add("type");
        arrayTipo.add("integer");
        arrayTipo.add("array");
        arrayTipo.add("of");

        programa.add("program");

        var.add("var");

        constante.add("const");

        funcao.add("function");

        numero.add('1');
        numero.add('2');
        numero.add('3');
        numero.add('4');
        numero.add('5');
        numero.add('6');
        numero.add('7');
        numero.add('8');
        numero.add('9');
        numero.add('0');

        bloco.add("begin");
        bloco.add("end");

        comando.add("while");
        comando.add("if");
        comando.add("then");
        comando.add("read");
        comando.add("write");
        comando.add("else");

        atribuicao.add(":=");

        pontoVirgula.add(";");

        parenteses.add("(");
        parenteses.add(")");

        colchetes.add("[");
        colchetes.add("]");

        virgula.add(",");

        doisPontos.add(":");
    }

    private class NoToken {

        private Token token;
        private String classe;
        private int linha;
        private NoToken filho;
        private NoToken irmao;

        private NoToken(Token token, NoToken filho, NoToken irmao) {

            this.token = token;
            this.filho = filho;
            this.irmao = irmao;
        }

        private NoToken(String classe, int linha, NoToken filho, NoToken irmao) {

            this.classe = classe;
            this.linha = linha;
            this.filho = filho;
            this.irmao = irmao;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }

        public String getClasse() {
            return classe;
        }

        public void setClasse(String classe) {
            this.classe = classe;
        }

        public int getLinha() {
            return linha;
        }

        public void setLinha(int linha) {
            this.linha = linha;
        }

        public NoToken getFilho() {
            return filho;
        }

        public void setFilho(NoToken filho) {
            this.filho = filho;
        }

        public NoToken getIrmao() {
            return irmao;
        }

        public void setIrmao(NoToken irmao) {

            this.irmao = irmao;
        }
    }

    public NoToken identificador(Token token) throws IOException{

        NoToken root = new NoToken("<IDENTIFICADOR>", token.getLinha(), null, null);

        if (token.getClasse().equals("Identificador")) {

            root.setFilho(new NoToken(token, null, null));
            t = getLexemas(line);

        } else {
            System.out.println("ERRO: identificador esperado na linha: " + linhaCodigo);
        }

        return root;
    }

    public NoToken programa(Token token) throws IOException {

        NoToken nt;

        if (token.getClasse().equals("Programa")) {

            nt = new NoToken("<PROGRAMA>", linhaCodigo, null, null);
            nt.setFilho(new NoToken(token, null, null));
            t = getLexemas(line);

            nt.getFilho().setIrmao(identificador(t));

            if (t.getLexema().equals(";")) {

                nt.getFilho().getIrmao().setIrmao(new NoToken(t, null, null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().getIrmao().setIrmao(corpo(t));

                return nt;

            } else {

                System.out.println("ERRO: ';' esperado na linha " + linhaCodigo);
                return null;
            }

        } else {

            System.out.println("ERRO: 'program' esperado na linha " + linhaCodigo);
            return null;
        }
    }

    public NoToken corpo(Token token) throws IOException {

        NoToken nt = new NoToken("<CORPO>", linhaCodigo, null, null);

        if(token.getLexema().equals("var") || token.getLexema().equals("const") ||
                token.getLexema().equals("type") || token.getLexema().equals("function")){

            nt.setFilho(declaracoes(token));

            // nt.getFilho().setIrmao(bloco(getLexemas(line)));

        }else{

            nt.setFilho(bloco(getLexemas(line)));
        }
        return nt;
    }

    public NoToken bloco(Token token) throws IOException {

        NoToken nt;

        if(token.getClasse().equals("Bloco")){

            nt = new NoToken("<BLOCO>", linhaCodigo, null, null);

            if(token.getLexema().equals("begin")){

            }
            nt.setFilho(declaracoes(token));
            //nt.getFilho().setIrmao(comandos(getLexemas(line)));

            return nt;

        }else{

            return null;
        }
    }

    public NoToken declaracoes(Token token) throws IOException {


        NoToken nt = new NoToken("<DECLARACOES>", linhaCodigo, null, null);
        nt.setFilho(defConst(token));
        nt.getFilho().setIrmao(defTipos(t));
        nt.getFilho().getIrmao().setIrmao(defVar(t));

        return nt;
    }

    public NoToken defTipos(Token token) throws IOException {

        NoToken nt = new NoToken("<DEF\\_TIPOS>", linhaCodigo, null, null);

        if(token.getLexema().equals("type")) {

            nt.setFilho(new NoToken(token, null, null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(tipo(t));

            if(t.getLexema().equals(";")){

                nt.getFilho().getIrmao().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().getIrmao().setIrmao(tipos(t));

            }else{

                System.out.println("ERRO: ';' esperado na linha"+linhaCodigo);
            }
        }

        return nt;
    }

    public NoToken defVar(Token token) throws IOException {

        NoToken nt = new NoToken("<DEF\\_VAR>", linhaCodigo, null, null);

        if(token.getLexema().equals("var")){

            nt.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(variavel(t));

            if(t.getLexema().equals(";")){

                nt.getFilho().getIrmao().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().getIrmao().setIrmao(variaveis(t));

            }else{

                System.out.println("ERRO: ';' esperado na linha: "+linhaCodigo);
            }
        }

        return nt;
    }

    public NoToken variavel(Token token) throws IOException {

        NoToken nt = new NoToken("<VARIAVEL>", linhaCodigo, null, null);

        nt.setFilho(identificador(token));

        nt.getFilho().setIrmao(listaId(t));

        if(t.getLexema().equals(":")){

            nt.getFilho().getIrmao().setIrmao(new NoToken(t, null, null));

            t = getLexemas(line);

            nt.getFilho().getIrmao().getIrmao().setIrmao(tipoDado(t));

        }else{

            System.out.println("Erro: ':' esperado na linha: "+linhaCodigo);
        }

        return nt;
    }

    public NoToken variaveis(Token token) throws IOException {

        NoToken nt = new NoToken("<VARIAVEIS>", linhaCodigo, null, null);

        if(token.getClasse().equals("Identificador")) {

            nt.setFilho(variavel(token));

            if (t.getLexema().equals(";")) {

                nt.getFilho().setIrmao(new NoToken(t, null, null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().setIrmao(variaveis(t));

            } else {

                System.out.println("Erro: ':' esperado na linha: " + linhaCodigo);
            }
        }

        return nt;
    }

    public NoToken listaId(Token token) throws IOException {

        NoToken nt = new NoToken("<LISTA\\_ID>", linhaCodigo, null, null);

        if(token.getLexema().equals(",")){

            nt.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(identificador(t));

            nt.getFilho().getIrmao().setIrmao(listaId(t));
        }

        return nt;
    }

    public NoToken tipoDado(Token token) throws IOException {

        NoToken nt = new NoToken("<TIPO\\_DADO>", linhaCodigo, null, null);

        if (token.getLexema().equals("array")) {

            nt.setFilho(new NoToken(token,null,null));
            t = getLexemas(line);

            if (t.getLexema().equals("[")) {

                nt.getFilho().setIrmao(new NoToken(t, null, null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().setIrmao(numero(t));

                if (t.getLexema().equals("]")) {

                    nt.getFilho().getIrmao().getIrmao().setIrmao(new NoToken(t, null, null));

                    t = getLexemas(line);

                    if (t.getLexema().equals("of")) {

                        nt.getFilho().getIrmao().getIrmao()
                                .getIrmao().setIrmao(new NoToken(t, null, null));

                        t = getLexemas(line);

                        nt.getFilho().getIrmao().getIrmao()
                                .getIrmao().getIrmao().setIrmao(tipoDado(t));
                    }

                } else {

                    System.out.println("ERRO: ']' esperado na linha: " + linhaCodigo);
                }

            } else {

                System.out.println("ERRO: '[' esperado na linha: " + linhaCodigo);
            }

        }else if(token.getClasse().equals("Identificador")){

            nt.setFilho(identificador(token));

        }else{

            for(String s: arrayTipo){

                if(token.getLexema().equals(s)) {

                    nt.setFilho(new NoToken(token, null, null));
                    t = getLexemas(line);
                    break;
                }
            }

        }
        return nt;
    }

    public NoToken defConst(Token token) throws IOException {

        NoToken root = new NoToken("<DEF\\_CONST>",linhaCodigo,null,null);

        if(token.getLexema().equals("const")){

            root.setFilho(new NoToken(token,null,null));
            t = getLexemas(line);

            root.getFilho().setIrmao(constante(t));

            if(t.getLexema().equals(";")){

                root.getFilho().getIrmao().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

                root.getFilho().getIrmao().getIrmao().setIrmao(constantes(t));

            }else{

                System.out.println("ERRO: ';' esperado na linha "+linhaCodigo);
            }
            return root;

        }else{

            return root;
        }
    }

    public NoToken constante(Token token) throws IOException {

        NoToken root = new NoToken("<CONSTANTE>",linhaCodigo,null,null);
        root.setFilho(identificador(token));

        if(root.getFilho()!=null) {
            if(t.getLexema().equals("=")){

                root.getFilho().setIrmao(new NoToken(t,null,null));
                t = getLexemas(line);

                root.getFilho().getIrmao().setIrmao(constValor(t));

                return root;

            }else{

                System.out.println("ERRO: '=' esperado na linha "+linhaCodigo);
                return root;
            }

        }else{

            return root;
        }
    }

    public NoToken constantes(Token token) throws IOException {

        NoToken root = new NoToken("<CONSTANTES>", linhaCodigo, null, null);

        if (token.getClasse().equals("Identificador")) {

            root.setFilho(constante(token));

            if (t.getLexema().equals(";")) {

                root.getFilho().setIrmao(new NoToken(t, null, null));
                t = getLexemas(line);
                root.getFilho().getIrmao().setIrmao(constantes(t));
            }
            else{

                System.out.println("ERRO: ';' esperado na linha: "+linhaCodigo);
            }
        }

        return root;
    }

    public NoToken constValor(Token token) throws IOException{

        NoToken nt = new NoToken("<CONST\\_VALOR>",linhaCodigo,null,null);
        nt.setFilho(nomeNumero(token));

        return nt;
    }

    public NoToken nomeNumero(Token token) throws IOException{

        NoToken nt = new NoToken("<NOME\\_NUMERO>",linhaCodigo,null,null);

        nt.setFilho(numero(token));

        if(nt.getFilho() == null){

            nt.setFilho(nome(token));

            if( nt.getFilho() == null) {

                System.out.println("ERRO: <NOME> ou <NUMERO> esperado na linha " + linhaCodigo);
                return null;
            }else{
                return nt;}
        }else{
            return nt;}
    }

    public NoToken nome(Token token) throws IOException{

        NoToken nt = new NoToken("<NOME>",linhaCodigo,null,null);
        nt.setFilho(identificador(token));
        nt.getFilho().setIrmao(indice(t));

        return nt;
    }

    public NoToken tipo(Token token) throws IOException{

        NoToken root = new NoToken("<TIPO>",linhaCodigo,null,null);
        root.setFilho(identificador(token));

        if(t.getLexema().equals("=")){

            root.getFilho().setIrmao(new NoToken(t,null,null));

            t = getLexemas(line);

            root.getFilho().getIrmao().setIrmao(tipoDado(t));

        }else{

            System.out.println("ERRO: '=' esperado na linha "+linhaCodigo);
        }

        return root;
    }

    public NoToken tipos(Token token) throws IOException{

        NoToken root = new NoToken("<TIPOS>",linhaCodigo,null,null);

        if(token.getClasse().equals("Identificador")) {

            root.setFilho(tipo(token));

            if (t.getLexema().equals(";")) {

                root.getFilho().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

                root.getFilho().getIrmao().setIrmao(tipos(t));

            } else {
                System.out.println("ERRO: ';' esperado na linha " + linhaCodigo);
            }
        }
        return root;
    }

    public NoToken numero(Token token) throws IOException{

        NoToken nt = new NoToken("<NUMERO>", linhaCodigo, null, null);

        if(token.getClasse().equals("Numero")) {

            nt.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

        }else{

            System.out.println("ERRO: <NUMERO> esperado na linha "+linhaCodigo);
        }

        return nt;
    }

    public NoToken indice(Token token) throws IOException{

        if(token.getLexema().equals("[")){

            NoToken nt = new NoToken("<INDICE>",linhaCodigo,null,null);
            nt.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(nomeNumero(t));

            if(t.getLexema().equals("]")){

                nt.getFilho().getIrmao().getIrmao().setIrmao(new NoToken(t,null,null));
                t = getLexemas(line);
                return nt;

            }else{

                System.out.println("ERRO: ']' esperado");
                return null;
            }

        }else{

            System.out.println("ERRO: '[' esperado");
            return null;
        }
    }

    public void geraArquivoArvore(NoToken noToken){

        if(noToken != null){

            if(noToken.getToken()!=null){

                System.out.print("[. "+noToken.getToken().getLexema());

            }else{

                if(noToken.getClasse().equals("<PROGRAMA>")){

                    System.out.print("\\Tree [. "+noToken.getClasse()+"\n");

                }else {
                    System.out.print("[. " + noToken.getClasse() + "\n");
                }
            }
            geraArquivoArvore(noToken.getFilho());
            System.out.print(" ]");
            geraArquivoArvore(noToken.getIrmao());
        }
    }
}