package br.com.portfolio.triagem.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Paciente implements Comparable<Paciente> {

    public enum Gravidade {
        VERMELHO(1, Duration.ZERO),
        LARANJA(2, Duration.ofMinutes(10)),
        AMARELO(3, Duration.ofMinutes(60)),
        VERDE(4, Duration.ofMinutes(120)),
        AZUL(5, Duration.ofMinutes(240));

        private final int nivel;
        private final Duration limiteEspera;

        Gravidade(int nivel, Duration limiteEspera) {
            this.nivel = nivel;
            this.limiteEspera = limiteEspera;
        }

        public int getNivel() { return nivel; }
        public Duration getLimiteEspera() { return limiteEspera; }

        public Gravidade promover() {
            return switch (this) {
                case AZUL -> VERDE;
                case VERDE -> AMARELO;
                case AMARELO -> LARANJA;
                case LARANJA, VERMELHO -> VERMELHO;
            };
        }
    }

    private final String prontuario;
    private final String nome;
    private Gravidade gravidade;
    private final LocalDateTime dataHoraChegada;
    private boolean reclassificado;

    public Paciente(String prontuario, String nome, Gravidade gravidade, LocalDateTime dataHoraChegada) {
        this.prontuario = prontuario;
        this.nome = nome;
        this.gravidade = gravidade;
        this.dataHoraChegada = dataHoraChegada;
        this.reclassificado = false;
    }

    public String getProntuario() { return prontuario; }
    public String getNome() { return nome; }
    public Gravidade getGravidade() { return gravidade; }
    public LocalDateTime getDataHoraChegada() { return dataHoraChegada; }
    public boolean isReclassificado() { return reclassificado; }

    public boolean avaliarEscalonamento(LocalDateTime agora) {
        if (this.gravidade == Gravidade.VERMELHO) return false;

        Duration espera = Duration.between(dataHoraChegada, agora);
        if (espera.compareTo(this.gravidade.getLimiteEspera()) > 0) {
            this.gravidade = this.gravidade.promover();
            this.reclassificado = true;
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Paciente outro) {
        // 1º Critério: Menor número tem maior prioridade clínica (1 = Vermelho, 5 = Azul)
        int diff = Integer.compare(this.gravidade.getNivel(), outro.gravidade.getNivel());
        if (diff != 0) return diff;

        // 2º Critério (Desempate): Quem chegou mais cedo tem prioridade (FIFO)
        return this.dataHoraChegada.compareTo(outro.dataHoraChegada);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Risco: %s %s| Chegada: %s",
                prontuario, nome, gravidade,
                reclassificado ? "(PROMOVIDO POR TEMPO) " : "",
                dataHoraChegada.toLocalTime());
    }
}