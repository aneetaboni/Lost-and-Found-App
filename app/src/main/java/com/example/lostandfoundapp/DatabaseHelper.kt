package com.example.lostandfoundapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "LostFoundDB", null, 2) {

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
                createdAt TEXT,
                latitude REAL,
                longitude REAL
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
        createdAt: String,
        latitude: Double,
        longitude: Double
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
        values.put("latitude", latitude)
        values.put("longitude", longitude)

        val result = db.insert("adverts", null, values)
        return result != -1L
    }

    fun getAllAdverts(): List<String> {
        val list = ArrayList<String>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM adverts", null)

        if (cursor.moveToFirst()) {
            do {
                val postType = cursor.getString(cursor.getColumnIndexOrThrow("postType"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))

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

    fun getAllAdvertObjects(): ArrayList<Advert> {
        val adverts = ArrayList<Advert>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM adverts", null)

        if (cursor.moveToFirst()) {
            do {
                val advert = Advert(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    postType = cursor.getString(cursor.getColumnIndexOrThrow("postType")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt")),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
                )

                adverts.add(advert)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return adverts
    }

    fun deleteAdvertByDescription(description: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete("adverts", "description=?", arrayOf(description))
        return result > 0
    }
}