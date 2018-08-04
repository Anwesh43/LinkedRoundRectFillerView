package com.anwesh.uiprojects.linkedroundrectfillerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.roundrectfillerview.RoundRectFillerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RoundRectFillerView.create(this)
    }
}
