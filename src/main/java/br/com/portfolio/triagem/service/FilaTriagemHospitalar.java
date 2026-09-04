package br.com.portfolio.triagem.service;

import br.com.portfolio.triagem.model.Paciente;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class FilaTriagemHospitalar {

    private final PriorityQueue<Paciente> heap = new PriorityQueue<>();

    public synchronized void registrarPaciente(Paciente paciente) {
        heap.offer(paciente);
    }

    public synchronized void aplicarRegraEscalonamento(LocalDateTime momentoAtual) {
        if (heap.isEmpty()) return;

        List<Paciente> temp = new ArrayList<>();
        while (!heap.isEmpty()) {
            Paciente p = heap.poll();
            p.avaliarEscalonamento(momentoAtual);
            temp.add(p);
        }
        heap.addAll(temp);
    }

    public synchronized Paciente chamarProximoAtendimento() {
        return heap.poll();
    }

    public synchronized boolean haPacientes() {
        return !heap.isEmpty();
    }

    public synchronized int totalAguardando() {
        return heap.size();
    }
}