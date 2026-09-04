package br.com.portfolio.triagem.service;

import br.com.portfolio.triagem.model.Paciente.Gravidade;
import br.com.portfolio.triagem.model.SinaisVitais;

public class ClassificadorManchesterService {

    public Gravidade sugerirClassificacao(SinaisVitais sinais) {
        if (sinais == null) return Gravidade.AZUL;

        // Risco iminente de morte -> VERMELHO
        if (sinais.isPerdaConsciencia() ||
                sinais.getSaturacaoOxigenio() < 90 ||
                sinais.getPressaoSistolica() < 80 ||
                sinais.getFrequenciaCardiaca() < 40) {
            return Gravidade.VERMELHO;
        }

        // Urgência grave -> LARANJA
        if (sinais.isDorToracicaAguda() ||
                sinais.getSaturacaoOxigenio() <= 94 ||
                sinais.getFrequenciaCardiaca() > 130 ||
                sinais.getNivelDor() >= 8) {
            return Gravidade.LARANJA;
        }

        // Urgência moderada -> AMARELO
        if (sinais.getPressaoSistolica() >= 180 ||
                sinais.getFrequenciaCardiaca() >= 110 ||
                sinais.getNivelDor() >= 5) {
            return Gravidade.AMARELO;
        }

        // Pouco urgente -> VERDE
        if (sinais.getNivelDor() > 0) {
            return Gravidade.VERDE;
        }

        // Não urgente -> AZUL
        return Gravidade.AZUL;
    }
}