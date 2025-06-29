package com.example.gerenciadordetarefas

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gerenciadordetarefas.data.TaskDatabaseHelper
import com.example.gerenciadordetarefas.model.Task
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    // Variável para armazenar ID da tarefa (caso esteja editando)
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Referências aos campos do layout
        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtDescription = findViewById<EditText>(R.id.edtDescription)
        val edtDueDate = findViewById<EditText>(R.id.edtDueDate)
        val ratingBarPriority = findViewById<RatingBar>(R.id.ratingBarPriority) // Novo: RatingBar para prioridade
        val edtStatus = findViewById<EditText>(R.id.edtStatus)
        val edtNotes = findViewById<EditText>(R.id.edtNotes)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Aplica a máscara para digitação da data (você pode implementar essa função)
        applyDateMask(edtDueDate)

        // Abre o calendário ao clicar no campo de data
        edtDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Formata a data como dd/MM/yyyy e seta no campo
                    val formatted = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    edtDueDate.setText(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Banco de dados
        val dbHelper = TaskDatabaseHelper(this)

        // Verifica se recebeu uma tarefa para edição (ID vem pela Intent)
        taskId = intent.getIntExtra("taskId", -1).takeIf { it != -1 }

        if (taskId != null) {
            // Busca a tarefa no banco pelo ID
            val task = dbHelper.getTaskById(taskId!!)
            if (task != null) {
                // Preenche os campos com os dados da tarefa existente
                edtTitle.setText(task.title)
                edtDescription.setText(task.description)
                edtDueDate.setText(task.dueDate)
                edtStatus.setText(task.status)
                edtNotes.setText(task.notes)

                // Define a prioridade no RatingBar (converter string para float)
                val priorityInt = task.priority.toIntOrNull() ?: 0
                ratingBarPriority.rating = priorityInt.toFloat()
            }
        }

        // Ação do botão Salvar
        btnSave.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val description = edtDescription.text.toString().trim()
            val dueDate = edtDueDate.text.toString().trim()
            val status = edtStatus.text.toString().trim()
            val notes = edtNotes.text.toString().trim()
            val priorityValue = ratingBarPriority.rating.toInt() // Pega o valor do RatingBar como Int

            // Validação simples: título e prazo obrigatórios
            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Título e prazo são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val now = getCurrentTimestamp()

            if (taskId == null) {
                // Criação de nova tarefa
                val newTask = Task(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    status = status,
                    priority = priorityValue.toString(), // Salva prioridade como string
                    notes = notes,
                    createdAt = now,
                    updatedAt = now
                )
                val result = dbHelper.addTask(newTask)
                if (result != -1L) {
                    Toast.makeText(this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao salvar tarefa", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Atualização de tarefa existente
                val updatedTask = Task(
                    id = taskId!!,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    status = status,
                    priority = priorityValue.toString(), // Atualiza prioridade
                    notes = notes,
                    createdAt = dbHelper.getTaskById(taskId!!)?.createdAt ?: now, // Mantém data original
                    updatedAt = now
                )
                val success = dbHelper.updateTask(updatedTask)
                if (success > 0) {
                    Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao atualizar tarefa", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Função para obter timestamp atual formatado (para createdAt e updatedAt)
    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // Exemplo simples de função para aplicar máscara de data no EditText
    private fun applyDateMask(editText: EditText) {
        // Aqui você pode usar um TextWatcher para inserir "/" automaticamente
        // Implementação simplificada para ideia:
        editText.addTextChangedListener(object : android.text.TextWatcher {
            var isUpdating = false
            val mask = "##/##/####"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var str = s.toString().filter { it.isDigit() }
                var formatted = ""
                var i = 0

                for (m in mask.toCharArray()) {
                    if (m != '#') {
                        formatted += m
                        continue
                    }
                    if (i >= str.length) break
                    formatted += str[i]
                    i++
                }

                isUpdating = true
                editText.setText(formatted)
                editText.setSelection(formatted.length)
            }
        })
    }
}
