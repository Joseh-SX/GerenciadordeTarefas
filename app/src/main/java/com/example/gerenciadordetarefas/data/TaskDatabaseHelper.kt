package com.example.gerenciadordetarefas.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.gerenciadordetarefas.model.Task

// Classe responsável por criar e gerenciar o banco de dados local SQLite
class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Constantes usadas para configurar o banco de dados e nome das colunas
    companion object {
        private const val DATABASE_NAME = "gerenciadordetarefas.db"   // Nome do banco
        private const val DATABASE_VERSION = 2                        // Versão do banco (aumentada)

        private const val TABLE_NAME = "tasks"

        // Colunas
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_DESCRIPTION = "description"
        private const val COL_STATUS = "status"
        private const val COL_PRIORITY = "priority"
        private const val COL_DUE_DATE = "dueDate"
        private const val COL_CREATED_AT = "createdAt"
        private const val COL_UPDATED_AT = "updatedAt"
        private const val COL_NOTES = "notes"
    }

    // Cria a tabela com os novos campos
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_DESCRIPTION TEXT,
                $COL_STATUS TEXT,
                $COL_PRIORITY TEXT,
                $COL_DUE_DATE TEXT,
                $COL_CREATED_AT TEXT,
                $COL_UPDATED_AT TEXT,
                $COL_NOTES TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    // Atualiza a estrutura do banco se a versão mudar
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") // Apaga e recria
        onCreate(db)
    }

    // Insere uma nova tarefa no banco
    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, task.title)
            put(COL_DESCRIPTION, task.description)
            put(COL_STATUS, task.status)
            put(COL_PRIORITY, task.priority)
            put(COL_DUE_DATE, task.dueDate)
            put(COL_CREATED_AT, task.createdAt)
            put(COL_UPDATED_AT, task.updatedAt)
            put(COL_NOTES, task.notes)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Retorna uma lista com todas as tarefas armazenadas
    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                    priority = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRIORITY)),
                    dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DUE_DATE)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT)),
                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_UPDATED_AT)),
                    notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES))
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return taskList
    }

    fun getTaskById(id: Int): Task? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var task: Task? = null
        if (cursor.moveToFirst()) {
            task = Task(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                priority = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRIORITY)),
                dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DUE_DATE)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_UPDATED_AT)),
                notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES))
            )
        }

        cursor.close()
        return task
    }

    // Atualiza uma tarefa existente no banco
    fun updateTask(task: Task): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, task.title)
            put(COL_DESCRIPTION, task.description)
            put(COL_STATUS, task.status)
            put(COL_PRIORITY, task.priority)
            put(COL_DUE_DATE, task.dueDate)
            put(COL_UPDATED_AT, task.updatedAt)
            put(COL_NOTES, task.notes)
        }
        return db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(task.id.toString()))
    }

    // Deleta uma tarefa com base no ID
    fun deleteTask(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }
}
