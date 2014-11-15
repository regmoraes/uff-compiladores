import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
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
    private final List<String> com = new ArrayList<String>();
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
    BufferedReader reader;
    public Formatter saida;
    String print = "\\documentclass[a4paper]{article}\n" +
            "\\usepackage[T1]{fontenc}\n" +
            "\\usepackage[utf8]{inputenc}\n" +
            "\\usepackage{lmodern}\n" +
            "\\usepackage{tikz}\n" +
            "\\usepackage{tikz-qtree}\n" +
            "\\usepackage[brazilian,portuguese]{babel}\n" +
            "\\usepackage{geometry}\n" +
            "\\geometry{paperwidth=3000mm, paperheight=1300pt, left=40pt, top=40pt, textwidth=280pt, marginparsep=20pt, marginparwidth=100pt, textheight=16263pt, footskip=40pt}\n" +
            "\\begin{document}\n" +
            "\n" +
            "\\begin{tikzpicture}";

    public AnalisadorLexico() {

    }

    public void executar() throws IOException {

        setLinguagem();

        reader = new BufferedReader(new FileReader("codigo.txt"));
        line = reader.readLine();

        if(line!=null) {
            arvoreTokens = programa(getLexemas(line));

        }else{

            System.out.println("ERRO: Arquivo vazio");
        }

        try {
            saida = new Formatter("arvoresintatica.tex");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        geraArquivoArvore(arvoreTokens);

        saida.format(print + "\n" + "\\end{tikzpicture}\n" +
                "\n" +
                "\n" +
                "\\end{document}");
        saida.close();
        String command = "pdflatex arvoresintatica.tex";
        Process p = Runtime.getRuntime().exec(command);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        command = "evince arvoresintatica.pdf";
        p = Runtime.getRuntime().exec(command);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public NoToken funcao(Token token) throws IOException{

        NoToken root = new NoToken("<FUNCAO>",linhaCodigo,null,null);

        root.setFilho(nomeFuncao(token));
        root.getFilho().setIrmao(blocoFuncao(t));

        return root;
    }

    public NoToken funcoes(Token token) throws IOException{

        NoToken root = new NoToken("<FUNCOES>",linhaCodigo,null,null);

        if(token.getClasse().equals("Tipo") || token.getClasse().equals("Identificador")){

            root.setFilho(funcao(token));
            root.getFilho().setIrmao(funcoes(t));
        }

        return root;
    }

    public NoToken nomeFuncao(Token token) throws IOException {

        NoToken root = new NoToken("<NOME\\_FUNCAO>",linhaCodigo,null,null);

        root.setFilho(tipoDado(token));

        root.getFilho().setIrmao(identificador(t));

        if(t.getLexema().equals("(")){

            root.getFilho().getIrmao().setIrmao(new NoToken(t,null,null));

            t = getLexemas(line);

            root.getFilho().getIrmao().getIrmao().setIrmao(variaveis(t));

            if(t.getLexema().equals(")")){

                root.getFilho().getIrmao().getIrmao().getIrmao().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

            }else{

                erro(")",linhaCodigo);
            }

        }else{
            erro("(", linhaCodigo);
        }

        return root;
    }

    public NoToken blocoFuncao(Token token) throws IOException {

        NoToken root = new NoToken("<BLOCO\\_FUNCAO>", linhaCodigo, null, null);

        root.setFilho(defVar(token));
        root.getFilho().setIrmao(bloco(t));

        return root;
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
                            i += 2;
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

                return new Token("Funcao", rs, linhaCodigo);
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

        com.add("while");
        com.add("if");
        com.add("then");
        com.add("read");
        com.add("write");
        com.add("else");

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

            nt.getFilho().setIrmao(bloco(t));

        }else{

            nt.setFilho(bloco(t));
        }
        return nt;
    }

    public NoToken bloco(Token token) throws IOException {

        NoToken nt = new NoToken("<BLOCO>", linhaCodigo, null, null);

        if(token.getLexema().equals("begin")) {

            nt.setFilho(new NoToken(token, null, null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(comandos(t));

        }
        else{

            nt.setFilho(comando(token));

            if(t.getLexema().equals(";")){

                nt.getFilho().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);
            }
            else{
                erro(";", linhaCodigo);
            }
        }

        return nt;
    }

    public NoToken declaracoes(Token token) throws IOException {


        NoToken nt = new NoToken("<DECLARACOES>", linhaCodigo, null, null);
        nt.setFilho(defConst(token));
        nt.getFilho().setIrmao(defTipos(t));
        nt.getFilho().getIrmao().setIrmao(defVar(t));
        nt.getFilho().getIrmao().getIrmao().setIrmao(defFunc(t));

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

                erro(";",linhaCodigo);
            }
        }

        return nt;
    }

    public NoToken defFunc(Token token) throws IOException {

        NoToken nt = new NoToken("<DEF\\_FUNC>", linhaCodigo, null, null);

        if(token.getLexema().equals("function")){

            nt.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(funcao(t));

            nt.getFilho().getIrmao().setIrmao(funcoes(t));
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

            erro(":",linhaCodigo);
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

    public NoToken valor(Token token) throws IOException{

        NoToken root = new NoToken("<VALOR>",linhaCodigo,null,null);

        if(token.getClasse().equals("Identificador")){

            root.setFilho(identificador(token));
            root.getFilho().setIrmao(valor2(t));

        }else if(token.getClasse().equals("Numero")){

            root.setFilho(numero(token));
            root.getFilho().setIrmao(expMatematica(t));

        }

        return root;
    }

    public NoToken valor2(Token token) throws IOException{

        NoToken root = new NoToken("<VALOR\\_2>",linhaCodigo,null,null);

        if(token.getLexema().equals("(")){


            root.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

            root.getFilho().setIrmao(parametro(t));

            if(t.getLexema().equals(")")){

                root.getFilho().getIrmao().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

            }else{

                erro("(",linhaCodigo);
            }

        }else{

            root.setFilho(indice(token));
            root.getFilho().setIrmao(expMatematica(t));
        }

        return root;
    }

    public NoToken parametro(Token token) throws IOException{

        NoToken root = new NoToken("<PARAMETRO>",linhaCodigo,null,null);

        root.setFilho(nomeNumero(token));

        root.getFilho().setIrmao(listaParam(t));

        return root;
    }

    public NoToken listaParam(Token token) throws IOException{

        NoToken root = new NoToken("<LISTA_PARAM>",linhaCodigo,null,null);

        if(token.getLexema().equals(",")){

            root.setFilho(new NoToken(",",linhaCodigo,null,null));

            t = getLexemas(line);

            root.getFilho().setIrmao(parametro(t));
        }

        return root;
    }

    public NoToken expMatematica(Token token) throws IOException{

        NoToken root = new NoToken("<EXP\\_MATEMATICA>",linhaCodigo,null,null);

        if(token.getClasse().equals("Operador Matematico")){

            root.setFilho(opMatematico(token));
            root.getFilho().setIrmao(nomeNumero(t));
            root.getFilho().getIrmao().setIrmao(expMatematica(t));
        }

        return root;
    }

    public NoToken expLogica(Token token) throws IOException{

        NoToken root = new NoToken("<EXP\\_LOGICA>",linhaCodigo,null,null);

        root.setFilho(nomeNumero(token));
        root.getFilho().setIrmao(expMatematica(t));
        root.getFilho().getIrmao().setIrmao(expLogica2(t));

        return root;
    }

    public NoToken expLogica2(Token token) throws IOException{

        NoToken root = new NoToken("<EXP\\_LOGICA\\_2>",linhaCodigo,null,null);

        if(token.getClasse().equals("Operador Logico")){

            root.setFilho(opLogico(token));
            root.getFilho().setIrmao(expLogica(t));
        }

        return root;
    }

    public NoToken opLogico(Token token) throws IOException{

        NoToken root = new NoToken("<OP\\_LOGICO>",linhaCodigo,null,null);

        if(token.getClasse().equals("Operador Logico")){

            root.setFilho(new NoToken(token,null,null));
            t = getLexemas(line);

        }else{

            erro(token.getClasse(),token.getLinha());
        }

        return root;
    }

    public NoToken opMatematico(Token token) throws IOException{

        NoToken root = new NoToken("<OP\\_MATEMATICO>",linhaCodigo,null,null);

        if(token.getClasse().equals("Operador Matematico")){

            root.setFilho(new NoToken(token,null,null));
            t = getLexemas(line);

        }else{

            erro(token.getClasse(),token.getLinha());
        }

        return root;
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

                    erro("]",linhaCodigo);
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

    public NoToken comando(Token token) throws IOException {

        NoToken nt = new NoToken("<COMANDO>", linhaCodigo, null, null);

        if(token.getLexema().equals("while")) {

            nt.setFilho(new NoToken(token, null, null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(expLogica(t));
            nt.getFilho().getIrmao().setIrmao(bloco(t));


        }else if (token.getLexema().equals("if")){

            nt.setFilho(new NoToken(token, null, null));

            t = getLexemas(line);

            nt.getFilho().setIrmao(expLogica(t));

            if(t.getLexema().equals("then")){

                nt.getFilho().getIrmao().setIrmao(new NoToken(t, null, null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().getIrmao().setIrmao(bloco(t));

            }else{

                erro("then", linhaCodigo);
            }


        }else if(token.getLexema().equals("write")){

            nt.setFilho(new NoToken(token, null, null));

        }else if((token.getLexema().equals("read"))){

            nt.setFilho(new NoToken(token, null, null));

        }else{

            nt.setFilho(nome(token));

            if(t.getLexema().equals(":=")){

                nt.getFilho().setIrmao(new NoToken(t,null,null));

                t = getLexemas(line);

                nt.getFilho().getIrmao().setIrmao(valor(t));

            }else{

                erro(":=",linhaCodigo);
            }

        }

        return nt;
    }

    public NoToken comandos(Token token) throws IOException{

        NoToken root = new NoToken("<COMANDOS>",linhaCodigo,null,null);

        if(token.getClasse().equals("Comando") || token.getClasse().equals("Identificador")){

            root.setFilho(comando(token));

            if(t.getLexema().equals(";")){

                root.getFilho().setIrmao(new NoToken(t, null, null));

                t = getLexemas(line);

                root.getFilho().getIrmao().setIrmao(comandos(t));

            }else{

                erro(";",linhaCodigo);
            }

        }else if(token.getLexema().equals("end")){

            root.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);
        }

        return root;
    }

    public NoToken constValor(Token token) throws IOException{

        NoToken nt = new NoToken("<CONST\\_VALOR>",linhaCodigo,null,null);
        nt.setFilho(nomeNumero(token));

        return nt;
    }

    public NoToken nomeNumero(Token token) throws IOException {

        NoToken nt = new NoToken("<NOME\\_NUMERO>", linhaCodigo, null, null);

        if (token.getClasse().equals("Numero")){

            nt.setFilho(numero(token));

        }else if(token.getClasse().equals("Identificador")){

            nt.setFilho(nome(token));

        }else{

            erro("'Identificador' ou 'Número'",linhaCodigo);
        }

        return nt;
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

        NoToken root = new NoToken("<INDICE>",linhaCodigo,null,null);

        if(token.getLexema().equals("[")){

            root.setFilho(new NoToken(token,null,null));

            t = getLexemas(line);

            root.getFilho().setIrmao(nomeNumero(t));

            if(t.getLexema().equals("]")){

                root.getFilho().getIrmao().getIrmao().setIrmao(new NoToken(t,null,null));
                t = getLexemas(line);

            }else{

                erro("]", linhaCodigo);
            }
        }

        return root;
    }

    public void geraArquivoArvore(NoToken noToken){

        if(noToken != null){

            if(noToken.getToken()!=null){

                print+="[. "+noToken.getToken().getLexema();

            }else{

                if(noToken.getClasse().equals("<PROGRAMA>")){

                    print+="\\Tree [. "+noToken.getClasse()+"\n";

                }else {
                    print+="[. " + noToken.getClasse() + "\n";
                }
            }
            geraArquivoArvore(noToken.getFilho());
            print+=" ]";
            geraArquivoArvore(noToken.getIrmao());
        }
    }

    public void erro(String string, int linhaCodigo){

        System.out.println("ERRO: " + string + " esperado na linha:" + linhaCodigo);

    }
}