package com.example.gerenciadordetarefas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.appcompat.widget.SwitchCompat


class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Lê preferências antes de aplicar o tema
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        val modoEscuroAtivado = prefs.getBoolean("modo_escuro", false)
        AppCompatDelegate.setDefaultNightMode(
            if (modoEscuroAtivado) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        // Trata os insets da janela (barra de status, etc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referência aos switches
        val switchNotificacoes = findViewById<SwitchCompat>(R.id.switch1)
        val switchModoEscuro = findViewById<SwitchCompat>(R.id.switch2)


        // Define o estado inicial do switch
        switchModoEscuro.isChecked = modoEscuroAtivado

        // Ação para o switch de notificações
        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Notificações ativadas" else "Notificações desativadas"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Ação para o switch de modo escuro
        switchModoEscuro.setOnCheckedChangeListener { _, isChecked ->
            // Salva a preferência
            prefs.edit {
                putBoolean("modo_escuro", isChecked)
            }

            // Aplica o modo escuro ou claro
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}
