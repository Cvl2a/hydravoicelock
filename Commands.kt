package com.hydra.voicelock

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.text.Normalizer

/**
 * Todos os comandos comecam com a palavra "hydra" (evita acionar sem querer).
 *  hydra trancar | hydra emergencia | hydra abrir <app> | hydra liberar
 */
object Commands {
    private const val WAKE = "hydra"

    private fun norm(s: String) =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    fun handle(ctx: Context, raw: String): Boolean {
        val t = norm(raw)
        if (!t.contains(WAKE)) return false
        val cmd = t.substringAfter(WAKE).trim()
        return when {
            cmd.startsWith("trancar") || cmd.startsWith("bloquear") -> { lock(ctx); true }
            cmd.startsWith("emergencia") || cmd.startsWith("socorro") -> {
                Store.closeGate(ctx); lock(ctx); true
            }
            cmd.startsWith("abrir ") -> openApp(ctx, cmd.removePrefix("abrir ").trim())
            cmd.startsWith("liberar") -> {
                ctx.startActivity(Intent(ctx, PinActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                true
            }
            else -> false
        }
    }

    private fun lock(ctx: Context) {
        val dpm = ctx.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isAdminActive(ComponentName(ctx, HydraAdmin::class.java))) dpm.lockNow()
    }

    private fun openApp(ctx: Context, name: String): Boolean {
        val pm = ctx.packageManager
        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val app = pm.queryIntentActivities(launcher, 0)
            .firstOrNull { norm(it.loadLabel(pm).toString()).contains(name) } ?: return false
        val i = pm.getLaunchIntentForPackage(app.activityInfo.packageName) ?: return false
        ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return true
    }
}
