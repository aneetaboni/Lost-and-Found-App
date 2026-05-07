package com.example.lostandfoundapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ShowItemsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var spinnerFilter: Spinner
    private lateinit var dbHelper: DatabaseHelper
    private var adverts = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_items)

        listView = findViewById(R.id.listView)
        spinnerFilter = findViewById(R.id.spinnerFilter)
        dbHelper = DatabaseHelper(this)

        setupFilter()
        loadAdverts()
    }

    private fun setupFilter() {
        val categories = arrayOf(
            "All",
            "Electronics",
            "Pets",
            "Wallets",
            "Documents",
            "Keys",
            "Other"
        )

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                textView.setBackgroundColor(Color.LTGRAY)
                textView.textSize = 16f
                textView.setPadding(16, 10, 16, 10)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                textView.setBackgroundColor(Color.WHITE)
                textView.textSize = 16f
                textView.setPadding(16, 20, 16, 20)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loadAdverts()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun loadAdverts() {
        adverts.clear()

        val selectedCategory = spinnerFilter.selectedItem.toString()
        val allData = dbHelper.getAllAdverts()

        if (selectedCategory == "All") {
            adverts.addAll(allData)
        } else {
            for (item in allData) {
                if (item.contains("Category: $selectedCategory")) {
                    adverts.add(item)
                }
            }
        }

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            adverts
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                textView.textSize = 16f
                textView.setPadding(16, 16, 16, 16)
                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adverts[position]
            val lines = selectedItem.split("\n")

            val description = if (lines.size > 2) {
                lines[2].trim()
            } else {
                selectedItem
            }

            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("description", description)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAdverts()
    }
}