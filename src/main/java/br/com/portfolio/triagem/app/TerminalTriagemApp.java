package br.com.portfolio.triagem.app;

import br.com.portfolio.triagem.model.Paciente;
import br.com.portfolio.triagem.model.Paciente.Gravidade;
import br.com.portfolio.triagem.model.SinaisVitais;
import br.com.portfolio.triagem.service.ClassificadorManchesterService;
import br.com.portfolio.triagem.service.FilaTriagemHospitalar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TerminalTriagemApp {

    private static final String RESET = "\u001B[0m";
    private static final String COR_VERMELHO = "\u001B[41m\u001B[37;1m";
    private static final String COR_LARANJA = "\u001B[43m\u001B[30;1m";
    private static final String COR_AMARELO = "\u001B[103m\u001B[30;1m";
    private static final String COR_VERDE = "\u001B[42m\u001B[37;1m";
    private static final String COR_AZUL = "\u001B[44m\u001B[37;1m";

    private static int sequencialProntuario = 1000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FilaTriagemHospitalar filaHospital = new FilaTriagemHospitalar();
        ClassificadorManchesterService motorClassificacao = new ClassificadorManchesterService();

        boolean executando = true;

        System.out.println("=================================================");
        System.out.println("   SISTEMA DE ACOLHIMENTO E TRIAGEM HOSPITALAR   ");
        System.out.println("            (Protocolo de Manchester)            ");
        System.out.println("=================================================");

        while (executando) {
            System.out.println("\n--- MENU DE OPERAÇÕES ---");
            System.out.println("1. Triar Novo Paciente");
            System.out.println("2. Chamar Próximo para Consultório Médico");
            System.out.println("3. Total de Pacientes em Espera");
            System.out.println("4. Encerrar Sistema");
            System.out.print("Selecione uma opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> triarPaciente(scanner, filaHospital, motorClassificacao);
                case "2" -> chamarConsultorio(filaHospital);
                case "3" -> System.out.println(">> Total aguardando: " + filaHospital.totalAguardando());
                case "4" -> {
                    System.out.println("\nEncerrando terminal...");
                    executando = false;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
        scanner.close();
    }

    private static void triarPaciente(Scanner scanner, FilaTriagemHospitalar fila, ClassificadorManchesterService motor) {
        System.out.print("\nNome completo do paciente: ");
        String nome = scanner.nextLine().trim();

        int satO2 = lerInteiro(scanner, "Saturação de Oxigênio (%): ", 50, 100);
        int fc = lerInteiro(scanner, "Frequência Cardíaca (bpm): ", 20, 250);
        int pas = lerInteiro(scanner, "Pressão Sistólica (mmHg): ", 40, 300);
        int dor = lerInteiro(scanner, "Nível de Dor (0 a 10): ", 0, 10);

        boolean dorPeito = lerSimNao(scanner, "Paciente relata dor torácica aguda? (S/N): ");
        boolean inconsciente = lerSimNao(scanner, "Paciente desmaiou / inconsciente? (S/N): ");

        SinaisVitais sinais = new SinaisVitais(satO2, fc, pas, dor, dorPeito, inconsciente);
        Gravidade gravidade = motor.sugerirClassificacao(sinais);

        System.out.println(">> Sugestão do Protocolo: " + colorirGravidade(gravidade));

        String prontuario = "PR-" + (++sequencialProntuario);
        Paciente paciente = new Paciente(prontuario, nome, gravidade, LocalDateTime.now());
        fila.registrarPaciente(paciente);

        System.out.println(">> Paciente registrado com sucesso na fila de espera!\n");
    }

    private static void chamarConsultorio(FilaTriagemHospitalar fila) {
        if (!fila.haPacientes()) {
            System.out.println("\n>> Fila vazia! Nenhum paciente aguardando.");
            return;
        }

        fila.aplicarRegraEscalonamento(LocalDateTime.now());
        Paciente proximo = fila.chamarProximoAtendimento();

        System.out.println("\n=================================================");
        System.out.println("CHAMANDO PACIENTE: " + proximo.getNome().toUpperCase());
        System.out.println("PRONTUÁRIO: " + proximo.getProntuario());
        System.out.println("GRAVIDADE : " + colorirGravidade(proximo.getGravidade()));
        System.out.println("=================================================");
    }

    private static String colorirGravidade(Gravidade g) {
        return switch (g) {
            case VERMELHO -> COR_VERMELHO + " [VERMELHO - EMERGÊNCIA] " + RESET;
            case LARANJA -> COR_LARANJA + " [LARANJA - MUITO URGENTE] " + RESET;
            case AMARELO -> COR_AMARELO + " [AMARELO - URGENTE] " + RESET;
            case VERDE -> COR_VERDE + " [VERDE - POUCO URGENTE] " + RESET;
            case AZUL -> COR_AZUL + " [AZUL - NÃO URGENTE] " + RESET;
        };
    }

    private static int lerInteiro(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= min && valor <= max) return valor;
                System.out.println("Digite um valor entre " + min + " e " + max);
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas números inteiros.");
            }
        }
    }

    private static boolean lerSimNao(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String resp = scanner.nextLine().trim().toUpperCase();
            if (resp.equals("S")) return true;
            if (resp.equals("N")) return false;
            System.out.println("Digite apenas 'S' para Sim ou 'N' para Não.");
        }
    }
}