package com.example.lostandfoundapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvDetails: TextView
    private lateinit var btnRemove: Button
    private var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        dbHelper = DatabaseHelper(this)
        tvDetails = findViewById(R.id.tvDetails)
        btnRemove = findViewById(R.id.btnRemove)

        description = intent.getStringExtra("description") ?: ""

        tvDetails.text = "Selected Advert:\n\n$description"

        btnRemove.setOnClickListener {
            val deleted = dbHelper.deleteAdvertByDescription(description)

            if (deleted) {
                Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error removing advert", Toast.LENGTH_SHORT).show()
            }
        }
    }
}