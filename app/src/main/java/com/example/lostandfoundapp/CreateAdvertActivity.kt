package com.example.lostandfoundapp

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateAdvertActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etLocation: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var imagePreview: ImageView
    private lateinit var radioLost: RadioButton
    private lateinit var radioFound: RadioButton

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                imagePreview.setImageURI(uri)
                imagePreview.visibility = ImageView.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_advert)

        radioLost = findViewById(R.id.radioLost)
        radioFound = findViewById(R.id.radioFound)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etDescription = findViewById(R.id.etDescription)
        etDate = findViewById(R.id.etDate)
        etLocation = findViewById(R.id.etLocation)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        imagePreview = findViewById(R.id.imagePreview)

        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val btnSave = findViewById<Button>(R.id.btnSave)

        setupCategorySpinner()
        setupDatePicker()

        btnUploadImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            saveAdvert()
        }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf(
            "Select Category",
            "Electronics",
            "Pets",
            "Wallets",
            "Documents",
            "Keys",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = "%02d/%02d/%04d".format(
                        dayOfMonth,
                        month + 1,
                        year
                    )
                    etDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }
    }

    private fun saveAdvert() {
        val postType = if (radioLost.isChecked) "Lost" else "Found"
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val date = etDate.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val imageUri = selectedImageUri?.toString()

        val createdAt = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty()
            || date.isEmpty() || location.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (category == "Select Category") {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = DatabaseHelper(this)

        val success = dbHelper.insertAdvert(
            postType,
            name,
            phone,
            description,
            date,
            location,
            category,
            imageUri,
            createdAt
        )

        if (success) {
            Toast.makeText(this, "Advert saved!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show()
        }
    }
}