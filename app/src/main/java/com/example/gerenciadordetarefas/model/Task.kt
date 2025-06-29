package com.example.gerenciadordetarefas.model

/**
 * Representa uma tarefa no sistema de gerenciamento.
 */
data class Task(
    var id: Int = 0,                     // ID único da tarefa (gerado automaticamente)
    var title: String,                  // Título da tarefa
    var description: String = "",       // Descrição da tarefa
    var status: String = "",            // Status (ex: pendente, em andamento, concluída)
    var priority: String = "",          // Prioridade (ex: alta, média, baixa)
    var dueDate: String,                // Prazo da tarefa (data de vencimento)
    var createdAt: String = "",         // Data/hora de criação
    var updatedAt: String = "",         // Data/hora da última atualização
    var notes: String = ""              // Observações adicionais
)
