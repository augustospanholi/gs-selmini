import java.util.Random;
import java.util.Scanner;

public class CidadeChuva {

    public static void main(String[] args) {
        int[] rms, parametros, deslocamentoLinha, deslocamentoColuna, aguaPorLinha;
        int[][] altitude, laminaInicial, lamina, laminaPasso3, laminaIngenua;
        boolean[][] alerta;
        int qtd, rm, N, M, K, L, ordem, a, b, i, passo;
        int totalInicio, totalFim, qtdAlerta, critica, aguaCritica, d1, d2;
        Random gerador;
        Scanner teclado = new Scanner(System.in);

        System.out.println("Digite a quantidade de integrantes (1 a 3):");
        qtd = teclado.nextInt();
        rms = new int[qtd];
        for(i = 0; i < rms.length; i++) {
            System.out.println("Digite o RM do integrante " + (i + 1) + ":");
            rms[i] = teclado.nextInt();
        }

        rm = calcularRmEquipe(rms);
        parametros = calcularParametros(rm);
        N = parametros[0];
        M = parametros[1];
        K = parametros[2];
        L = parametros[3];
        ordem = parametros[4];
        a = rm % N;
        b = (rm / 7) % M;

        System.out.println("RM da equipe: " + rm);
        System.out.println("N = " + N);
        System.out.println("M = " + M);
        System.out.println("K = " + K);
        System.out.println("L = " + L);
        System.out.println("ordem = " + ordem);

        gerador = new Random(rm);
        altitude = gerarAltitude(gerador, N, M);
        laminaInicial = gerarMatriz(gerador, N, M, 0, 10);
        deslocamentoLinha = criarDeslocamentoLinha(ordem);
        deslocamentoColuna = criarDeslocamentoColuna(ordem);

        imprimir(altitude, "Altitude (cm):");
        lamina = copiar(laminaInicial);
        imprimir(lamina, "Lamina inicial (cm):");

        totalInicio = somar(lamina);
        laminaPasso3 = new int[N][M];
        for(passo = 1; passo <= K; passo++) {
            if(passo == 3) {
                laminaPasso3 = copiar(lamina);
            }
            lamina = simularPasso(altitude, lamina, deslocamentoLinha, deslocamentoColuna);
            imprimir(lamina, "Lamina apos o passo " + passo + " (cm):");
        }
        totalFim = somar(lamina);

        System.out.println("Agua total no inicio: " + totalInicio);
        System.out.println("Agua total no fim: " + totalFim);
        alerta = marcarAlerta(lamina, L);
        qtdAlerta = contarAlerta(alerta);
        System.out.println("Celulas em alerta: " + qtdAlerta);

        imprimirMapaAlerta(alerta);

        aguaPorLinha = calcularAguaPorLinha(lamina);
        critica = encontrarLinhaCritica(aguaPorLinha);
        aguaCritica = aguaPorLinha[critica];
        System.out.println("Agua por linha (cm):");
        for(i = 0; i < aguaPorLinha.length; i++) {
            System.out.println("Linha " + i + ": " + aguaPorLinha[i]);
        }
        System.out.println("Linha critica: " + critica + " (" + aguaCritica + " cm)");

        laminaIngenua = copiar(laminaInicial);
        for(passo = 1; passo < 3; passo++) {
            laminaIngenua = simularPassoIngenuo(altitude, laminaIngenua, deslocamentoLinha, deslocamentoColuna);
        }
        d1 = laminaPasso3[a][b];
        d2 = laminaIngenua[a][b];
        imprimir(laminaPasso3, "Lamina no passo 3 - versao correta (cm):");
        imprimir(laminaIngenua, "Lamina no passo 3 - versao ingenua (cm):");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("D1 = " + d1);
        System.out.println("D2 = " + d2);

        System.out.println("RESUMO RM=" + rm + " ALERTA=" + qtdAlerta + " CRITICA=" + critica + " AGUA=" + aguaCritica + " D1=" + d1 + " D2=" + d2);
        teclado.close();
    }

    public static int calcularRmEquipe(int[] rms) {
        int i, soma = 0;
        for(i = 0; i < rms.length; i++) {
            soma = soma + rms[i];
        }
        return soma;
    }

    public static int[] calcularParametros(int rm) {
        int[] parametros = new int[5];
        parametros[0] = 8 + rm % 3;
        parametros[1] = 10 + (rm / 10) % 3;
        parametros[2] = 4 + (rm / 100) % 3;
        parametros[3] = 10 + 2 * (rm % 4);
        parametros[4] = rm % 2;
        return parametros;
    }

    public static int[][] gerarAltitude(Random gerador, int n, int m) {
        int[][] altitude = new int[n][m];
        int i, j;
        for(i = 0; i < altitude.length; i++) {
            for(j = 0; j < altitude[i].length; j++) {
                altitude[i][j] = 3 * Math.abs(i - n / 2) + gerador.nextInt(5);
            }
        }
        return altitude;
    }

    public static int[][] gerarMatriz(Random gerador, int n, int m, int minimo, int maximo) {
        int[][] matriz = new int[n][m];
        int i, j;
        for(i = 0; i < matriz.length; i++) {
            for(j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = minimo + gerador.nextInt(maximo - minimo + 1);
            }
        }
        return matriz;
    }

    public static int[] criarDeslocamentoLinha(int ordem) {
        int[] deslocamento = new int[4];
        if(ordem == 0) {
            deslocamento[0] = -1;
            deslocamento[1] = 0;
            deslocamento[2] = 1;
            deslocamento[3] = 0;
        } else {
            deslocamento[0] = 0;
            deslocamento[1] = 1;
            deslocamento[2] = 0;
            deslocamento[3] = -1;
        }
        return deslocamento;
    }

    public static int[] criarDeslocamentoColuna(int ordem) {
        int[] deslocamento = new int[4];
        if(ordem == 0) {
            deslocamento[0] = 0;
            deslocamento[1] = 1;
            deslocamento[2] = 0;
            deslocamento[3] = -1;
        } else {
            deslocamento[0] = -1;
            deslocamento[1] = 0;
            deslocamento[2] = 1;
            deslocamento[3] = 0;
        }
        return deslocamento;
    }

    public static boolean dentro(int i, int j, int n, int m) {
        boolean existe = false;
        if(i >= 0 && i < n && j >= 0 && j < m) {
            existe = true;
        }
        return existe;
    }

    public static int[][] copiar(int[][] matriz) {
        int[][] copia = new int[matriz.length][matriz[0].length];
        int i, j;
        for(i = 0; i < matriz.length; i++) {
            for(j = 0; j < matriz[i].length; j++) {
                copia[i][j] = matriz[i][j];
            }
        }
        return copia;
    }

    public static void imprimir(int[][] matriz, String titulo) {
        String linha;
        int i, j;
        System.out.println(titulo);
        for(i = 0; i < matriz.length; i++) {
            linha = "";
            for(j = 0; j < matriz[i].length; j++) {
                if(j > 0) {
                    linha = linha + "\t";
                }
                linha = linha + matriz[i][j];
            }
            System.out.println(linha);
        }
    }

    public static int somar(int[][] matriz) {
        int i, j, soma = 0;
        for(i = 0; i < matriz.length; i++) {
            for(j = 0; j < matriz[i].length; j++) {
                soma = soma + matriz[i][j];
            }
        }
        return soma;
    }

    public static int[][] simularPasso(int[][] altitude, int[][] lamina, int[] deslocamentoLinha, int[] deslocamentoColuna) {
        int[][] nova;
        int n, m, i, j, d, t, vi, vj, menorNivel, nivelVizinho, melhor;
        n = altitude.length;
        m = altitude[0].length;
        nova = copiar(lamina);

        for(i = 0; i < n; i++) {
            for(j = 0; j < m; j++) {
                t = lamina[i][j] / 2;
                if(t > 0) {
                    menorNivel = altitude[i][j] + lamina[i][j];
                    melhor = -1;
                    for(d = 0; d < 4; d++) {
                        vi = i + deslocamentoLinha[d];
                        vj = j + deslocamentoColuna[d];
                        if(dentro(vi, vj, n, m)) {
                            nivelVizinho = altitude[vi][vj] + lamina[vi][vj];
                            if(nivelVizinho < menorNivel) {
                                menorNivel = nivelVizinho;
                                melhor = d;
                            }
                        }
                    }
                    if(melhor != -1) {
                        vi = i + deslocamentoLinha[melhor];
                        vj = j + deslocamentoColuna[melhor];
                        nova[i][j] = nova[i][j] - t;
                        nova[vi][vj] = nova[vi][vj] + t;
                    }
                }
            }
        }
        return nova;
    }

    public static int[][] simularPassoIngenuo(int[][] altitude, int[][] lamina, int[] deslocamentoLinha, int[] deslocamentoColuna) {
        int n, m, i, j, d, t, vi, vj, menorNivel, nivelVizinho, melhor;
        n = altitude.length;
        m = altitude[0].length;

        for(i = 0; i < n; i++) {
            for(j = 0; j < m; j++) {
                t = lamina[i][j] / 2;
                if(t > 0) {
                    menorNivel = altitude[i][j] + lamina[i][j];
                    melhor = -1;
                    for(d = 0; d < 4; d++) {
                        vi = i + deslocamentoLinha[d];
                        vj = j + deslocamentoColuna[d];
                        if(dentro(vi, vj, n, m)) {
                            nivelVizinho = altitude[vi][vj] + lamina[vi][vj];
                            if(nivelVizinho < menorNivel) {
                                menorNivel = nivelVizinho;
                                melhor = d;
                            }
                        }
                    }
                    if(melhor != -1) {
                        vi = i + deslocamentoLinha[melhor];
                        vj = j + deslocamentoColuna[melhor];
                        lamina[i][j] = lamina[i][j] - t;
                        lamina[vi][vj] = lamina[vi][vj] + t;
                    }
                }
            }
        }
        return lamina;
    }

    public static boolean[][] marcarAlerta(int[][] lamina, int limiarAlerta) {
        boolean[][] alerta = new boolean[lamina.length][lamina[0].length];
        int i, j;
        for(i = 0; i < lamina.length; i++) {
            for(j = 0; j < lamina[i].length; j++) {
                if(lamina[i][j] >= limiarAlerta) {
                    alerta[i][j] = true;
                }
            }
        }
        return alerta;
    }

    public static int contarAlerta(boolean[][] alerta) {
        int i, j, cont = 0;
        for(i = 0; i < alerta.length; i++) {
            for(j = 0; j < alerta[i].length; j++) {
                if(alerta[i][j]) {
                    cont++;
                }
            }
        }
        return cont;
    }

    public static void imprimirMapaAlerta(boolean[][] alerta) {
        String linha;
        int i, j;
        System.out.println("Mapa de alerta (# = alagado, . = normal):");
        for(i = 0; i < alerta.length; i++) {
            linha = "";
            for(j = 0; j < alerta[i].length; j++) {
                if(j > 0) {
                    linha = linha + "\t";
                }
                if(alerta[i][j]) {
                    linha = linha + "#";
                } else {
                    linha = linha + ".";
                }
            }
            System.out.println(linha);
        }
    }

    public static int[] calcularAguaPorLinha(int[][] lamina) {
        int[] aguaPorLinha = new int[lamina.length];
        int i, j, soma;
        for(i = 0; i < lamina.length; i++) {
            soma = 0;
            for(j = 0; j < lamina[i].length; j++) {
                soma = soma + lamina[i][j];
            }
            aguaPorLinha[i] = soma;
        }
        return aguaPorLinha;
    }

    public static int encontrarLinhaCritica(int[] aguaPorLinha) {
        int i, maior, linha;
        maior = aguaPorLinha[0];
        linha = 0;
        for(i = 0; i < aguaPorLinha.length; i++) {
            if(aguaPorLinha[i] > maior) {
                maior = aguaPorLinha[i];
                linha = i;
            }
        }
        return linha;
    }
}
