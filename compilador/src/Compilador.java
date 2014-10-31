import java.io.IOException;

/**
 * Created by romulo-eduardo on 9/21/14.
 */
public class Compilador {
    
    public static void main(String[] args) throws IOException {

        AnalisadorLexico mAnalisadorLexico = new AnalisadorLexico();

        try {
            mAnalisadorLexico.executar();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
