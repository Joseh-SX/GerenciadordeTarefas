package com.example.gerenciadordetarefas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerenciadordetarefas.R
import com.example.gerenciadordetarefas.model.Task

class TaskAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    private val onItemClick: (Task) -> Unit,   // Ao clicar na tarefa (para editar)
    private val onExcluir: (Task) -> Unit      // Ao clicar no botão de excluir
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder que representa cada item da lista
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTarefa: TextView = itemView.findViewById(R.id.txtTarefa)
        val btnExcluir: Button = itemView.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Texto mostrando título, status, prioridade e data de vencimento
        holder.txtTarefa.text = "${task.title} - Status: ${task.status} - Prioridade: ${task.priority} - ${task.dueDate}"

        // Clique para editar
        holder.itemView.setOnClickListener {
            onItemClick(task)
        }

        // Clique no botão excluir
        holder.btnExcluir.setOnClickListener {
            onExcluir(task)
        }
    }
}
