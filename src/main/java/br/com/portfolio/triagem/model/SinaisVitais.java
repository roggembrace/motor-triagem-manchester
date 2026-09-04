package br.com.portfolio.triagem.model;

public class SinaisVitais {
    private final int saturacaoOxigenio;
    private final int frequenciaCardiaca;
    private final int pressaoSistolica;
    private final int nivelDor;
    private final boolean dorToracicaAguda;
    private final boolean perdaConsciencia;

    public SinaisVitais(int saturacaoOxigenio, int frequenciaCardiaca, int pressaoSistolica,
                        int nivelDor, boolean dorToracicaAguda, boolean perdaConsciencia) {
        this.saturacaoOxigenio = saturacaoOxigenio;
        this.frequenciaCardiaca = frequenciaCardiaca;
        this.pressaoSistolica = pressaoSistolica;
        this.nivelDor = nivelDor;
        this.dorToracicaAguda = dorToracicaAguda;
        this.perdaConsciencia = perdaConsciencia;
    }

    public int getSaturacaoOxigenio() { return saturacaoOxigenio; }
    public int getFrequenciaCardiaca() { return frequenciaCardiaca; }
    public int getPressaoSistolica() { return pressaoSistolica; }
    public int getNivelDor() { return nivelDor; }
    public boolean isDorToracicaAguda() { return dorToracicaAguda; }
    public boolean isPerdaConsciencia() { return perdaConsciencia; }
}