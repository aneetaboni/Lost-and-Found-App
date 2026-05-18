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
        val btnShowOnMap = findViewById<Button>(R.id.btnShowOnMap)

        btnCreate.setOnClickListener {
            startActivity(Intent(this, CreateAdvertActivity::class.java))
        }

        btnShow.setOnClickListener {
            startActivity(Intent(this, ShowItemsActivity::class.java))
        }

        btnShowOnMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}