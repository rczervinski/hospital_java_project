import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

class Saida {
    PrintWriter out;
    boolean deveFechar;

    Saida(PrintWriter out, boolean deveFechar) {
        this.out = out;
        this.deveFechar = deveFechar;
    }

    public static Saida getImpressoraDeSaida(String titulo) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Saida saida = null;

        while (true) {
            System.out.println("Deseja imprimir no terminal ou em um arquivo? [T]terminal  [A]arquivo)");
            String resposta = scanner.nextLine().trim().toLowerCase();

            if (resposta.equals("a")) {
                System.out.println("Digite o nome do arquivo:");
                String nomeArquivo = scanner.nextLine().trim();
                try {
                    saida = new Saida(new PrintWriter("arquivoSaida/" + nomeArquivo), true);
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("Erro ao criar o arquivo. Por favor, tente novamente.");
                }
            } else if (resposta.equals("t")) {
                saida = new Saida(new PrintWriter(System.out, true), false);
                break;
            } else {
                System.out.println("Resposta inválida. Por favor, escolha 'T' ou 'A'.");
            }
        }

        return saida;
    }
}