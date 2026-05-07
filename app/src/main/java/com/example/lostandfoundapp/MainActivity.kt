package com.example.lostandfoundapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCreate = findViewById<Button>(R.id.btnCreate)
        val btnShow = findViewById<Button>(R.id.btnShow)

        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateAdvertActivity::class.java)
            startActivity(intent)
        }

        btnShow.setOnClickListener {
            val intent = Intent(this, ShowItemsActivity::class.java)
            startActivity(intent)
        }
    }
}