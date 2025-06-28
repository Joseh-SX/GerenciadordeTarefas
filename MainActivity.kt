// Pacote onde essa classe está localizada
package com.example.gerenciadordetarefas

// Importações necessárias
import android.os.Bundle // Gerencia o ciclo de vida da atividade
import android.widget.Button // Representa os botões na interface
import androidx.appcompat.app.AppCompatActivity // Classe base para atividades modernas com suporte a ActionBar
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast // Exibe mensagens rápidas na tela (notificações curtas)
import android.content.Intent
import com.example.gerenciadordetarefas.data.TaskDatabaseHelper
import com.example.gerenciadordetarefas.model.Task

class MainActivity : AppCompatActivity() { // Define a classe da atividade principal
    private lateinit var listaTarefasLayout: LinearLayout
    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) { // Método chamado ao criar a activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa o banco de dados
        dbHelper = TaskDatabaseHelper(this)

        // Referência para o LinearLayout onde as tarefas vão aparecer
        listaTarefasLayout = findViewById(R.id.listaTarefas)

        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)
        val btnConfig = findViewById<Button>(R.id.btnConfig)

        // Atualiza a lista exibida toda vez que a activity é criada
        mostrarTarefas()

        btnAdicionar.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        btnConfig.setOnClickListener {
            // Toast.makeText(this, "Botão Configurações clicado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }
    }

    // Método para atualizar a lista de tarefas na tela
    private fun mostrarTarefas() {
        listaTarefasLayout.removeAllViews() // Limpa as tarefas atuais para atualizar

        // Busca todas as tarefas no banco
        val tarefas = dbHelper.getAllTasks()

        // Para cada tarefa, cria um TextView e adiciona no LinearLayout
        for (tarefa in tarefas) {
            val tarefaView = TextView(this).apply {
                text = "• ${tarefa.title} - ${tarefa.dueDate}"
                textSize = 16f
                setPadding(0, 0, 0, 16)
            }
            listaTarefasLayout.addView(tarefaView)
        }
    }

    override fun onResume() {
        super.onResume()
        // Atualiza a lista sempre que volta para essa activity (ex: após adicionar tarefa)
        mostrarTarefas()
    }
}
