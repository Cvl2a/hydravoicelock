package com.hydra.voicelock

import android.content.Context
import java.security.MessageDigest
import java.util.UUID

object Store {
    private fun p(c: Context) = c.getSharedPreferences("hydra", Context.MODE_PRIVATE)

    private fun hash(pin: String, salt: String): String =
        MessageDigest.getInstance("SHA-256").digest((salt + pin).toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun hasPin(c: Context) = p(c).contains("pin")

    fun setPin(c: Context, pin: String) {
        val salt = UUID.randomUUID().toString()
        p(c).edit().putString("salt", salt).putString("pin", hash(pin, salt)).apply()
    }

    /** Confere o PIN. 5 erros = bloqueio de 60s. Se openGate, libera o "portao" por 5 min. */
    fun verify(c: Context, pin: String, openGate: Boolean): Boolean {
        val now = System.currentTimeMillis()
        val sp = p(c)
        if (now < sp.getLong("blockedUntil", 0)) return false
        val ok = hash(pin, sp.getString("salt", "") ?: "") == sp.getString("pin", null)
        val e = sp.edit()
        if (ok) {
            e.putInt("fails", 0)
            if (openGate) e.putLong("gateUntil", now + 5 * 60_000)
        } else {
            val f = sp.getInt("fails", 0) + 1
            if (f >= 5) { e.putInt("fails", 0); e.putLong("blockedUntil", now + 60_000) }
            else e.putInt("fails", f)
        }
        e.apply()
        return ok
    }

    /** Gancho para o futuro: hydrafonevpn e apps protegidos consultam isso. */
    fun gateOpen(c: Context) = System.currentTimeMillis() < p(c).getLong("gateUntil", 0)

    fun closeGate(c: Context) = p(c).edit().putLong("gateUntil", 0).apply()
}
