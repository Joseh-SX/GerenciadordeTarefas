package com.example.gerenciadordetarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gerenciadordetarefas.adapter.TaskAdapter
import com.example.gerenciadordetarefas.data.TaskDatabaseHelper
import com.example.gerenciadordetarefas.model.Task
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerTarefas: RecyclerView
    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Lê a preferência de modo escuro
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        val modoEscuroAtivado = prefs.getBoolean("modo_escuro", false)

        // 2. Aplica o tema ANTES do setContentView
        AppCompatDelegate.setDefaultNightMode(
            if (modoEscuroAtivado) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerTarefas = findViewById(R.id.recyclerTarefas)
        recyclerTarefas.layoutManager = LinearLayoutManager(this)

        dbHelper = TaskDatabaseHelper(this)

        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)
        val btnConfig = findViewById<Button>(R.id.btnConfig)

        // Abrir tela para adicionar nova tarefa
        btnAdicionar.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Ação do botão de configurações
        btnConfig.setOnClickListener {
            // Toast.makeText(this, "Botão Configurações clicado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
            
        }

        mostrarTarefas()
    }

    private fun mostrarTarefas() {
        val tarefas: List<Task> = dbHelper.getAllTasks()

        val adapter = TaskAdapter(
            context = this,
            taskList = tarefas,
            onItemClick = { tarefa ->
                // Ao clicar na tarefa, abre a tela de edição
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("taskId", tarefa.id)
                startActivity(intent)
            },
            onExcluir = { tarefa ->
                // Ao clicar em excluir
                val resultado = dbHelper.deleteTask(tarefa.id)
                if (resultado > 0) {
                    Toast.makeText(this, "Tarefa excluída", Toast.LENGTH_SHORT).show()
                    mostrarTarefas()
                } else {
                    Toast.makeText(this, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerTarefas.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        mostrarTarefas()
    }
}
