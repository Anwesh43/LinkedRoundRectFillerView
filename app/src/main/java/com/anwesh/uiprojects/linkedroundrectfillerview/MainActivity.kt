package com.anwesh.uiprojects.linkedroundrectfillerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.roundrectfillerview.RoundRectFillerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: RoundRectFillerView = RoundRectFillerView.create(this)
        fullScreen()
        view.addAnimationListener({ completeToast(it) }, { resetToast(it) })
    }

    fun createToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun completeToast(i: Int) {
        createToast("animation number ${i + 1} is complete")
    }

    fun resetToast(i: Int) {
        createToast("animation number ${i + 1} is reset")
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}