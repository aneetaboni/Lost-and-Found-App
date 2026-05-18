package com.example.lostandfoundapp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.SimpleDateFormat
import java.util.*

class CreateAdvertActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var selectedLatitude = 0.0
    private var selectedLongitude = 0.0

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etLocation: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var imagePreview: ImageView
    private lateinit var radioLost: RadioButton
    private lateinit var radioFound: RadioButton

    private val AUTOCOMPLETE_REQUEST_CODE = 101

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

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCm78yRDWK_t_VyUu9tuORMyytL-vzdLlo")
        }

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
        val btnCurrentLocation = findViewById<Button>(R.id.btnCurrentLocation)

        setupCategorySpinner()
        setupDatePicker()

        etLocation.setOnClickListener {
            openAutocomplete()
        }

        btnUploadImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        btnSave.setOnClickListener {
            saveAdvert()
        }
    }

    private fun openAutocomplete() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        ).build(this)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val place = Autocomplete.getPlaceFromIntent(data)

                etLocation.setText(place.address)

                place.latLng?.let {
                    selectedLatitude = it.latitude
                    selectedLongitude = it.longitude
                }

                Toast.makeText(this, "Location selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                selectedLatitude = location.latitude
                selectedLongitude = location.longitude

                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    etLocation.setText(addresses[0].getAddressLine(0))
                } else {
                    etLocation.setText("$selectedLatitude, $selectedLongitude")
                }

                Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Could not get location. Try again.", Toast.LENGTH_SHORT).show()
            }
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

        if (selectedLatitude == 0.0 || selectedLongitude == 0.0) {
            Toast.makeText(this, "Please select location using autocomplete or current location", Toast.LENGTH_SHORT).show()
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
            createdAt,
            selectedLatitude,
            selectedLongitude
        )

        if (success) {
            Toast.makeText(this, "Advert saved!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show()
        }
    }
}