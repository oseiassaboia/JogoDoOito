package com.jogodooito;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JPanel;

public class IniciarJogo extends JPanel {

    private int tamanho;
    private int dimensao;
    private int margem;
    private int[] blocos;
    private int numerosBlocos;
    private int tamanhoBloco;
    private int tamanhoGrid;
    private int posicaoVazia;
    private boolean fimJogo;
    private static final Color FOREGROUND_COLOR = new Color(19, 111, 165);
    private static final Random RANDOM = new Random();

    public IniciarJogo(int tamanho, int dimensao, int margem) {
        this.tamanho = tamanho;
        this.dimensao = dimensao;
        this.margem = margem;

        numerosBlocos = tamanho * tamanho - 1;
        blocos = new int[tamanho * tamanho];

        tamanhoGrid = (dimensao - 2 * this.margem);
        tamanhoBloco = tamanhoGrid / tamanho;

        setPreferredSize(new Dimension(this.dimensao, this.dimensao + this.margem));
        setBackground(Color.DARK_GRAY);
        setForeground(FOREGROUND_COLOR);
        setFont(new Font("SansSerif", Font.BOLD, 60));

        fimJogo = true;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (fimJogo) {
                    novoJogo();
                } else {

                    int eixoX = e.getX() - margem;
                    int eixoY = e.getY() - margem;

                    if (eixoX < 0 || eixoX > tamanhoGrid || eixoY < 0 || eixoY > tamanhoGrid) {
                        return;
                    }

                    int coluna1 = eixoX / tamanhoBloco;
                    int linha1 = eixoY / tamanhoBloco;

                    int coluna2 = posicaoVazia % tamanho;
                    int linha2 = posicaoVazia / tamanho;

                    int posicaoClicada = linha1 * tamanho + coluna1;

                    int direcao = 0;

                    if (coluna1 == coluna2 && Math.abs(linha1 - linha2) > 0) {
                        direcao = (linha1 - linha2) > 0 ? tamanho : -tamanho;                        
                    } else if (linha1 == linha2 && Math.abs(coluna1 - coluna2) > 0) {
                        direcao = (coluna1 - coluna2) > 0 ? 1 : -1;
                    }

                    if (direcao != 0) {
                        do {
                            int novaPosicaoVazia = posicaoVazia + direcao;
                            blocos[posicaoVazia] = blocos[novaPosicaoVazia];
                            posicaoVazia = novaPosicaoVazia;
                        } while (posicaoVazia != posicaoClicada);

                        blocos[posicaoVazia] = 0;
                    }

                    fimJogo = verificarResolvido();
                }

                repaint();
            }
        });

        novoJogo();
    }

    private void novoJogo() {
        do {
            reiniciar();
            embaralhar();
        } while (!verificarSolucionado());

        fimJogo = false;
    }

    private void reiniciar() {
        for (int i = 0; i < blocos.length; i++) {
            blocos[i] = (i + 1) % blocos.length;
        }
        
        posicaoVazia = blocos.length - 1;
    }

    private void embaralhar() {
        
        int numeroBlocos = numerosBlocos;

        while (numeroBlocos > 1) {
            int embaralhar = RANDOM.nextInt(numeroBlocos--);
            int temp = blocos[embaralhar];
            blocos[embaralhar] = blocos[numeroBlocos];
            blocos[numeroBlocos] = temp;
        }
    }

    private boolean verificarSolucionado() {
        int contarInversoes = 0;

        for (int i = 0; i < numerosBlocos; i++) {
            for (int j = 0; j < i; j++) {
                if (blocos[j] > blocos[i]) {
                    contarInversoes++;
                }
            }
        }

        return contarInversoes % 2 == 0;
    }

    private boolean verificarResolvido() {
        if (blocos[blocos.length - 1] != 0) {
            return false;
        }

        for (int i = numerosBlocos - 1; i >= 0; i--) {
            if (blocos[i] != i + 1) {
                return false;
            }
        }

        return true;
    }
    
    private void carregarGrid(Graphics2D grafico) {
        for (int i = 0; i < blocos.length; i++) {

            int coluna = i / tamanho;
            int linha = i % tamanho;

            int eixoX = margem + linha * tamanhoBloco;
            int eixoY = margem + coluna * tamanhoBloco;

            if (blocos[i] == 0) {
                if (fimJogo) {
                    grafico.setColor(FOREGROUND_COLOR);
                    carregarTextoCentralizado(grafico, "\u2713", eixoX, eixoY);
                }
                continue;
            }

            grafico.setColor(getForeground());
            grafico.fillRoundRect(eixoX, eixoY, tamanhoBloco, tamanhoBloco, 25, 25);
            grafico.setColor(Color.BLACK);
            grafico.drawRoundRect(eixoX, eixoY, tamanhoBloco, tamanhoBloco, 25, 25);
            grafico.setColor(Color.WHITE);

            carregarTextoCentralizado(grafico, String.valueOf(blocos[i]), eixoX, eixoY);
        }
    }

    private void carregarMensagemInicio(Graphics2D grafico) {
        if (fimJogo) {
            grafico.setFont(getFont().deriveFont(Font.BOLD, 18));
            grafico.setColor(FOREGROUND_COLOR);
            String txtIniciar = "Clique para iniciar um novo jogo";
            grafico.drawString(txtIniciar, (getWidth() - grafico.getFontMetrics().stringWidth(txtIniciar)) / 2,
                    getHeight() - margem);
        }
    }

    private void carregarTextoCentralizado(Graphics2D grafico, String txtBloco, int eixoX, int eixoY) {
        FontMetrics metricasFonte = grafico.getFontMetrics();
        int ascente = metricasFonte.getAscent();
        int decrescente = metricasFonte.getDescent();
        grafico.drawString(txtBloco, eixoX + (tamanhoBloco - metricasFonte.stringWidth(txtBloco)) / 2,
                eixoY + (ascente + (tamanhoBloco - (ascente + decrescente)) / 2));
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        Graphics2D g2D = (Graphics2D) grafico;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        carregarGrid(g2D);
        carregarMensagemInicio(g2D);
    }

}
