// Define o pacote onde está essa classe, organizando o projeto
package com.example.gerenciadordetarefas.data

// Importa as bibliotecas necessárias para uso de banco de dados e manipulação de dados
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

// Importa o modelo de dados Task, que representa uma tarefa no sistema
import com.example.gerenciadordetarefas.model.Task

// Classe responsável por criar e gerenciar o banco de dados local SQLite
class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Constantes usadas para configurar o banco de dados
    companion object {
        private const val DATABASE_NAME = "gerenciadordetarefas.db"     // Nome do arquivo do banco
        private const val DATABASE_VERSION = 1                          // Versão do banco
        private const val TABLE_NAME = "tasks"                          // Nome da tabela
        private const val COL_ID = "id"                                 // Nome da coluna de ID
        private const val COL_TITLE = "title"                           // Nome da coluna de título
        private const val COL_DESCRIPTION = "description"            // Nome da coluna de descrição
        private const val COL_DUE_DATE = "dueDate"              // Nome da coluna de data de entrega
    }

    // Função chamada automaticamente quando o banco é criado pela primeira vez
    override fun onCreate(db: SQLiteDatabase) {
        // Cria a tabela "tasks" com as colunas definidas
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT,
                $COL_DESCRIPTION TEXT,
                $COL_DUE_DATE TEXT
            )
        """.trimIndent()
        db.execSQL(createTable) // Executa o SQL para criar a tabela
    }

    // Função chamada automaticamente quando a versão do banco é alterada
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Deleta a tabela existente (caso exista) e recria do zero
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // =============================
    // Funções CRUD (Create, Read, Update, Delete)
    // =============================

    // Adiciona uma nova tarefa no banco de dados
    fun addTask(task: Task): Long {
        val db = this.writableDatabase                         // Acesso em modo escrita
        val values = ContentValues()                           // Estrutura para armazenar os dados da nova tarefa
        values.put(COL_TITLE, task.title)                      // Define o título
        values.put(COL_DESCRIPTION, task.description)          // Define a descrição
        values.put(COL_DUE_DATE, task.dueDate)                 // Define a data de entrega
        return db.insert(TABLE_NAME, null, values)             // Insere no banco e retorna o ID da nova linha
    }

    // Retorna uma lista com todas as tarefas salvas no banco
    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()                   // Lista onde serão armazenadas as tarefas
        val db = this.readableDatabase                         // Acesso em modo leitura
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)  // Executa consulta SQL

        // Verifica se há ao menos um resultado
        if (cursor.moveToFirst()) {
            do {
                // Cria um objeto Task com os dados lidos do banco
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DUE_DATE))
                )
                taskList.add(task)                             // Adiciona a tarefa na lista
            } while (cursor.moveToNext())                      // Continua até o fim do resultado
        }

        cursor.close()                                         // Fecha o cursor (boa prática)
        return taskList                                        // Retorna a lista de tarefas
    }

    // Atualiza uma tarefa existente no banco de dados
    fun updateTask(task: Task): Int {
        val db = this.writableDatabase                         // Acesso em modo escrita
        val values = ContentValues()
        values.put(COL_TITLE, task.title)
        values.put(COL_DESCRIPTION, task.description)
        values.put(COL_DUE_DATE, task.dueDate)
        // Atualiza a linha onde o id bate com o da tarefa passada
        return db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(task.id.toString()))
    }

    // Deleta uma tarefa com base no ID
    fun deleteTask(id: Int): Int {
        val db = this.writableDatabase
        // Apaga a linha da tabela onde o ID bate com o passado
        return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }
}
