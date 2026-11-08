package com.hydra.voicelock

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }
        fun btn(label: String, action: () -> Unit) {
            col.addView(Button(this).apply {
                text = label
                setOnClickListener { action() }
            })
        }

        col.addView(TextView(this).apply {
            text = "HydraVoiceLock\n\nComandos:\n- hydra trancar\n- hydra emergência\n- hydra abrir <app>\n- hydra liberar (pede PIN)"
            textSize = 16f
        })

        btn("1. Permitir microfone e notificações") {
            val perms = if (Build.VERSION.SDK_INT >= 33)
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS)
            else arrayOf(Manifest.permission.RECORD_AUDIO)
            requestPermissions(perms, 1)
        }
        btn("2. Ativar administrador (para trancar)") {
            startActivity(
                Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(this, HydraAdmin::class.java))
                    .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Necessário para trancar a tela por voz.")
            )
        }
        btn("3. Permitir exibir sobre outros apps (abrir apps por voz)") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }
        btn("4. Definir PIN") {
            fun save() = askPin("Novo PIN (mínimo 4 dígitos)") {
                if (it.length >= 4) { Store.setPin(this, it); toast("PIN salvo") } else toast("PIN muito curto")
            }
            if (Store.hasPin(this)) {
                askPin("PIN atual") { if (Store.verify(this, it, false)) save() else toast("PIN incorreto") }
            } else save()
        }
        btn("5. Iniciar escuta") {
            if (!Store.hasPin(this)) toast("Defina o PIN primeiro")
            else startForegroundService(Intent(this, VoiceService::class.java))
        }
        btn("6. Parar escuta (pede PIN)") {
            askPin("PIN para parar") {
                if (Store.verify(this, it, false)) stopService(Intent(this, VoiceService::class.java))
                else toast("PIN incorreto ou bloqueado")
            }
        }
        setContentView(col)
    }

    private fun askPin(title: String, ok: (String) -> Unit) {
        val e = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        AlertDialog.Builder(this).setTitle(title).setView(e)
            .setPositiveButton("OK") { _, _ -> ok(e.text.toString()) }
            .show()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
