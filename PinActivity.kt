package com.hydra.voicelock

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

class PinActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 27) setShowWhenLocked(true)
        val box = EditText(this).apply {
            hint = "PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        val btn = Button(this).apply {
            text = "Liberar"
            setOnClickListener {
                if (Store.verify(this@PinActivity, box.text.toString(), true)) {
                    Toast.makeText(this@PinActivity, "Liberado por 5 min", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@PinActivity, "PIN incorreto ou bloqueado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            addView(box)
            addView(btn)
        })
    }
}
