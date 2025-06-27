package com.example.gerenciadordetarefas.model

data class Task(
    var id: Int = 0,
    var title: String,
    var description: String,
    var dueDate: String
)
