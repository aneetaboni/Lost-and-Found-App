package com.example.lostandfoundapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "LostFoundDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE adverts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                postType TEXT,
                name TEXT,
                phone TEXT,
                description TEXT,
                date TEXT,
                location TEXT,
                category TEXT,
                imageUri TEXT,
                createdAt TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS adverts")
        onCreate(db)
    }

    fun insertAdvert(
        postType: String,
        name: String,
        phone: String,
        description: String,
        date: String,
        location: String,
        category: String,
        imageUri: String,
        createdAt: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put("postType", postType)
        values.put("name", name)
        values.put("phone", phone)
        values.put("description", description)
        values.put("date", date)
        values.put("location", location)
        values.put("category", category)
        values.put("imageUri", imageUri)
        values.put("createdAt", createdAt)

        val result = db.insert("adverts", null, values)
        return result != -1L
    }

    fun getAllAdverts(): List<String> {
        val list = ArrayList<String>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM adverts", null)

        if (cursor.moveToFirst()) {
            do {
                val postType = cursor.getString(1)
                val description = cursor.getString(4)
                val category = cursor.getString(7)
                val location = cursor.getString(6)
                val createdAt = cursor.getString(9)

                val formatted = """
                $postType Item
                
                $description
                
                Category: $category
                Location: $location
                
                Posted: $createdAt
            """.trimIndent()

                list.add(formatted)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }
    fun deleteAdvertByDescription(description: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete("adverts", "description=?", arrayOf(description))
        return result > 0
    }
}