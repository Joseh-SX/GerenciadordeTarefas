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

    // Variável para armazenar o ID da tarefa (em caso de edição)
    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Referências aos campos do layout
        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtDescription = findViewById<EditText>(R.id.edtDescription)
        val edtDueDate = findViewById<EditText>(R.id.edtDueDate)
        val ratingBarPriority = findViewById<RatingBar>(R.id.ratingBarPriority)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatus)
        val edtNotes = findViewById<EditText>(R.id.edtNotes)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Cria o adapter para o Spinner (com os valores do array XML)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        // Aplica máscara de data no campo de data
        applyDateMask(edtDueDate)

        // Mostra o DatePicker ao clicar no campo de data
        edtDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formatted = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    edtDueDate.setText(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Instância do banco de dados
        val dbHelper = TaskDatabaseHelper(this)

        // Recupera o ID da tarefa (se estiver em modo de edição)
        taskId = intent.getIntExtra("taskId", -1).takeIf { it != -1 }

        if (taskId != null) {
            // Se o ID foi passado, carrega a tarefa do banco e preenche os campos
            val task = dbHelper.getTaskById(taskId!!)
            if (task != null) {
                edtTitle.setText(task.title)
                edtDescription.setText(task.description)
                edtDueDate.setText(task.dueDate)
                edtNotes.setText(task.notes)

                // Define a seleção correta do status no Spinner
                val statusIndex = adapter.getPosition(task.status)
                spinnerStatus.setSelection(statusIndex)

                // Define a prioridade no RatingBar
                val priorityInt = task.priority.toIntOrNull() ?: 0
                ratingBarPriority.rating = priorityInt.toFloat()
            }
        }

        // Ação do botão "Salvar"
        btnSave.setOnClickListener {
            // Lê os valores dos campos
            val title = edtTitle.text.toString().trim()
            val description = edtDescription.text.toString().trim()
            val dueDate = edtDueDate.text.toString().trim()
            val status = spinnerStatus.selectedItem.toString()
            val notes = edtNotes.text.toString().trim()
            val priorityValue = ratingBarPriority.rating.toInt()

            // Validação básica: título e prazo são obrigatórios
            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Título e prazo são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val now = getCurrentTimestamp()

            if (taskId == null) {
                // Criando uma nova tarefa
                val newTask = Task(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    status = status,
                    priority = priorityValue.toString(),
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
                // Atualizando uma tarefa existente
                val updatedTask = Task(
                    id = taskId!!,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    status = status,
                    priority = priorityValue.toString(),
                    notes = notes,
                    createdAt = dbHelper.getTaskById(taskId!!)?.createdAt ?: now,
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

    // Retorna a data e hora atual formatada (yyyy-MM-dd HH:mm:ss)
    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // Aplica uma máscara simples ao campo de data no formato dd/MM/yyyy
    private fun applyDateMask(editText: EditText) {
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
