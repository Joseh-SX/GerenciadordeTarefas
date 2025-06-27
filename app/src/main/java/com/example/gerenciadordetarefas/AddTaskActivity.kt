package com.example.gerenciadordetarefas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gerenciadordetarefas.data.TaskDatabaseHelper
import com.example.gerenciadordetarefas.model.Task

class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtDescription = findViewById<EditText>(R.id.edtDescription)
        val edtDueDate = findViewById<EditText>(R.id.edtDueDate)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val dbHelper = TaskDatabaseHelper(this)

        btnSave.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val description = edtDescription.text.toString().trim()
            val dueDate = edtDueDate.text.toString().trim()

            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Título e prazo são obrigatórios", Toast.LENGTH_SHORT).show()
            } else {
                val task = Task(title = title, description = description, dueDate = dueDate)
                val result = dbHelper.addTask(task)

                if (result != -1L) {
                    Toast.makeText(this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // fecha a tela e volta para a anterior
                } else {
                    Toast.makeText(this, "Erro ao salvar tarefa", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
