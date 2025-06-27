// Define o pacote onde esta classe está localizada
package com.example.gerenciadordetarefas.adapter

// Importa as classes necessárias do Android
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

// Importa o modelo de dados Task
import com.example.gerenciadordetarefas.model.Task

// Importa o layout XML usado para cada item da lista
import com.example.gerenciadordetarefas.R

// Classe adaptadora que conecta a lista de tarefas (List<Task>) a uma ListView
class TaskAdapter(
    private val context: Context,            // Contexto da aplicação (geralmente a Activity)
    private val taskList: List<Task>         // Lista de tarefas a ser exibida
) : BaseAdapter() {                          // Herda de BaseAdapter, classe usada para criar listas personalizadas

    // Retorna o número de itens na lista
    override fun getCount(): Int = taskList.size

    // Retorna o item (tarefa) da posição indicada
    override fun getItem(position: Int): Any = taskList[position]

    // Retorna o ID único da tarefa (converte de Int para Long)
    override fun getItemId(position: Int): Long = taskList[position].id.toLong()

    // Retorna a View correspondente a cada item da lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Se convertView for nulo, infla (cria) uma nova View com o layout "task_item"
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)

        // Obtém a tarefa da posição atual
        val task = taskList[position]

        // Define o texto do título da tarefa no TextView correspondente
        view.findViewById<TextView>(R.id.txtTitle).text = task.title

        // Define o texto do prazo da tarefa no TextView correspondente
        view.findViewById<TextView>(R.id.txtDueDate).text = "Prazo: ${task.dueDate}"

        // Define o texto da descrição da tarefa no TextView correspondente
        view.findViewById<TextView>(R.id.txtDescription).text = task.description

        // Retorna a View totalmente preenchida para ser exibida na lista
        return view
    }
}
