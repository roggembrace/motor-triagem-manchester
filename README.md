# 🏥 Motor de Triagem Clínica com Prioridade Dinâmica (Protocolo de Manchester)
[🔗 Acessar Demonstração Interativa Online](https://roggembrace.github.io/motor-triagem-manchester/)
<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Version" />
  <img src="https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/Biomedical_Software-007ACC?style=for-the-badge" alt="Domain" />
  <img src="https://img.shields.io/badge/Status-Conclu%C3%ADdo-brightgreen?style=for-the-badge" alt="Status" />
</p>

---

## 📌 Visão Geral do Projeto

Implementação de um sistema de acolhimento hospitalar e suporte à decisão clínica baseado nas diretrizes internacionais do **Protocolo de Manchester**.

O software foi desenvolvido com foco em alta confiabilidade para ambientes de pronto atendimento, eliminando riscos de morbimortalidade associados ao tempo de espera excessivo e prevenindo a inanição clínica (*starvation*) de pacientes classificados inicialmente com menor gravidade.

---

## 🎯 Objetivos Clínicos & Empatia de Negócio

Em prontos-socorros sobrecarregados, a utilização de filas lineares puras (ordem de chegada - FIFO) ou vetores dinâmicos simples (`ArrayList`) gera dois problemas críticos:

1. **Latência de Processamento em Emergências:** Inserir pacientes críticos no início de filas lineares exige o deslocamento em lote de todos os elementos na memória, gerando atrasos em momentos de sobrecarga.
2. **Inanição Clínica (*Starvation*):** Pacientes de urgência intermediária (ex.: Amarelo ou Verde) podem permanecer indefinidamente sem atendimento caso novos pacientes de maior gravidade continuem dando entrada no hospital, acarretando descompensação e risco de óbito na sala de espera.

### Solução Proposta
* **Árvore de Decisão Médica:** Avaliação instantânea de sinais vitais (Saturação de $O_2$, FC, PAS, Nível de Dor e Queixas Críticas) em tempo constante.
* **Min-Heap de Prioridade:** Fila de espera que mantém o caso clínico mais urgente no topo da estrutura em tempo logarítmico.
* **Escalonamento Dinâmico (*Aging Pattern*):** Monitoramento contínuo dos limites de tolerância de cada cor. Caso o tempo de espera do paciente estoure a janela máxima recomendada pelo protocolo, ele é promovido automaticamente de gravidade e reordenado na fila.

---

## 📊 Matriz de Gravidade e Tempos Limites de Espera

| Nível Clínico | Cor Representativa | Tempo Máximo de Espera | Condição de Disparo (Gatilhos de Amostra) |
| :---: | :---: | :---: | :--- |
| **1** | **Vermelho** | **Imediato (0 min)** | Parada, Glasgow rebaixado, SatO2 $< 90\%$, Choque (PAS $< 80$) |
| **2** | **Laranja** | **10 minutos** | Dor torácica aguda (suspeita IAM), SatO2 $\le 94\%$, Dor $\ge 8$ |
| **3** | **Amarelo** | **60 minutos** | Hipertensão severa (PAS $\ge 180$), Taquicardia, Dor $\ge 5$ |
| **4** | **Verde** | **120 minutos** | Dor leve a moderada, sinais vitais estáveis |
| **5** | **Azul** | **240 minutos** | Quadro crônico sem descompensação aguda, queixa leve |

---

## ⚡ Análise de Complexidade Assintótica (Big-O)

| Operação | Implementação Ingênua (`ArrayList`) | Este Projeto (`PriorityQueue` + Min-Heap) | Justificativa Teórica |
| :--- | :---: | :---: | :--- |
| **Classificação de Sinais Vitais** | $O(1)$ | **$O(1)$** | Árvore condicional estática sem laços. |
| **Inserção de Paciente Crítico** | $O(n)$ | **$O(\log n)$** | Rebalanceamento em árvore binária sem deslocar array. |
| **Chamada para Consultório** | $O(n)$ ou $O(1)$ | **$O(\log n)$** | Remoção da raiz do Min-Heap com reconstituição imediata. |
| **Varredura de Aging (Anti-Starvation)** | $O(n \cdot \log n)$ | **$O(n)$** | Drenagem e reconstrução linear (*heapify*) dos nós elegíveis. |
| **Consumo de Memória Espacial** | $O(n)$ | **$O(n)$** | Espaço estritamente proporcional ao número de pacientes aguardando. |

---

## 🔄 Diagrama de Sequência do Ciclo de Atendimento (Mermaid)

```mermaid
sequenceDiagram
    autonumber
    actor E as Enfermeiro(a) Triador
    participant T as Terminal / CLI
    participant M as Motor de Regras (Manchester)
    participant F as Fila Hospitalar (Min-Heap)
    actor Med as Médico(a) de Plantão

    Note over E,T: Etapa 1: Acolhimento e Aferição
    E->>T: Insere dados e sinais vitais (SatO2, FC, PAS, Dor)
    T->>M: sugerirClassificacao(sinaisVitais)
    M-->>T: Retorna Gravidade Sugerida (ex.: LARANJA)
    T-->>E: Exibe sugestão clínica na tela
    E->>T: Confirma classificação (ou aplica override)
    
    Note over T,F: Etapa 2: Registro na Estrutura de Dados
    T->>F: registrarPaciente(Paciente [Nível, Timestamp])
    Note right of F: Insere e reequilibra<br/>a árvore em O(log n)
    T-->>E: Imprime pulseira colorida e confirmação

    Note over Med,F: Etapa 3: Convocação para Consultório
    Med->>T: Solicita próximo atendimento
    T->>F: aplicarRegraEscalonamento(agora)
    Note over F: Varre pacientes estourados<br/>e promove cor (Anti-Starvation)
    T->>F: chamarProximoAtendimento()
    F-->>T: Retorna Paciente de maior prioridade clínica
    T-->>Med: Exibe prontuário, risco e dados no painel médico