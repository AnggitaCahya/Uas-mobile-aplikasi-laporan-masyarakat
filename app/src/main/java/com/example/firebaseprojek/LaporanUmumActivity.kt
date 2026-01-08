package com.example.firebaseprojek

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class LaporanUmumActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_JENIS = "jenis_laporan" // "kebakaran" / "medis" / "pencurian"
    }

    private val REQUEST_CAMERA = 100
    private val REQUEST_CAMERA_PERMISSION = 101
    private val REQUEST_LOCATION_PERMISSION = 102

    private val databaseRef = FirebaseDatabase.getInstance().reference

    private var capturedBitmap: Bitmap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    private lateinit var toolbarTitle: TextView
    private lateinit var btnKembali: Button
    private lateinit var btnKirim: Button
    private lateinit var btnAddImage: LinearLayout
    private lateinit var btnAmbilLokasi: Button
    private lateinit var btnLihatMaps: Button
    private lateinit var imgPreview: ImageView
    private lateinit var etTanggal: EditText
    private lateinit var etTelepon: EditText
    private lateinit var etLokasi: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var etLinkMaps: EditText
    private lateinit var tvDeskripsiLabel: TextView
    private lateinit var etIsi: EditText
    private lateinit var progressBar: ProgressBar

    private var jenisLaporan: String = "kebakaran"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_umum)

        jenisLaporan = intent.getStringExtra(EXTRA_JENIS) ?: "kebakaran"

        initializeViews()
        setupToolbarTitleDanLabel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupListeners()
    }

    private fun initializeViews() {
        toolbarTitle = findViewById(R.id.tv_toolbar_title)
        btnKembali = findViewById(R.id.btn_kembali)
        btnKirim = findViewById(R.id.btn_kirim)
        btnAddImage = findViewById(R.id.btn_add_image)
        btnAmbilLokasi = findViewById(R.id.btn_ambil_lokasi)
        btnLihatMaps = findViewById(R.id.btn_lihat_maps)
        imgPreview = findViewById(R.id.img_preview)
        etTanggal = findViewById(R.id.et_tanggal)
        etTelepon = findViewById(R.id.et_telepon)
        etLokasi = findViewById(R.id.et_lokasi)
        etLatitude = findViewById(R.id.et_latitude)
        etLongitude = findViewById(R.id.et_longitude)
        etLinkMaps = findViewById(R.id.et_link_maps)
        tvDeskripsiLabel = findViewById(R.id.tv_deskripsi_label)
        etIsi = findViewById(R.id.et_isi)
        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.GONE
    }

    private fun setupToolbarTitleDanLabel() {
        when (jenisLaporan) {
            "medis" -> {
                toolbarTitle.text = "Laporan Medis"
                tvDeskripsiLabel.text = "Deskripsi Medis"
            }
            "pencurian" -> {
                toolbarTitle.text = "Laporan Pencurian"
                tvDeskripsiLabel.text = "Deskripsi Pencurian"
            }
            else -> {
                toolbarTitle.text = "Laporan Kebakaran"
                tvDeskripsiLabel.text = "Deskripsi Kebakaran"
            }
        }
    }

    private fun setupListeners() {
        btnKembali.setOnClickListener { finish() }
        btnAddImage.setOnClickListener { checkCameraPermissionAndOpen() }
        btnAmbilLokasi.setOnClickListener { getCurrentLocation() }
        btnLihatMaps.setOnClickListener { bukaGoogleMaps() }
        btnKirim.setOnClickListener { kirimLaporan() }
        etTanggal.setOnClickListener { showDateTimePicker() }
    }

    // ===== Date + Time picker =====
    private fun showDateTimePicker() {
        val cal = Calendar.getInstance()

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                etTanggal.setText(sdf.format(cal.time))
            }

            TimePickerDialog(
                this,
                timeListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        DatePickerDialog(
            this,
            dateListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // ===== Kamera =====
    private fun checkCameraPermissionAndOpen() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            bukaKamera()
        }
    }

    private fun bukaKamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA)
        } else {
            Toast.makeText(this, "Tidak ada aplikasi kamera", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                capturedBitmap = bitmap
                imgPreview.visibility = View.VISIBLE
                imgPreview.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Foto tidak ditemukan dari kamera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ===== Lokasi / GPS =====
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLng = location.longitude

                    etLatitude.setText(location.latitude.toString())
                    etLongitude.setText(location.longitude.toString())

                    val link =
                        "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                    etLinkMaps.setText(link)

                    Toast.makeText(this, "Lokasi berhasil diambil", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun bukaGoogleMaps() {
        val lat = currentLat
        val lng = currentLng
        if (lat == null || lng == null) {
            Toast.makeText(this, "Lokasi belum diambil", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = android.net.Uri.parse("geo:0,0?q=$lat,$lng(Laporan)")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Google Maps tidak terpasang", Toast.LENGTH_SHORT).show()
        }
    }

    // ===== Permission =====
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            requestCode == REQUEST_CAMERA_PERMISSION &&
                    grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                bukaKamera()
            }
            requestCode == REQUEST_LOCATION_PERMISSION &&
                    grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ===== Kirim laporan =====
    private fun kirimLaporan() {
        val tanggal = etTanggal.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val lokasiDeskripsi = etLokasi.text.toString().trim()
        val latitudeText = etLatitude.text.toString().trim()
        val longitudeText = etLongitude.text.toString().trim()
        val linkMaps = etLinkMaps.text.toString().trim()
        val isi = etIsi.text.toString().trim()

        val latValue = latitudeText.toDoubleOrNull()
        val lngValue = longitudeText.toDoubleOrNull()

        when {
            tanggal.isEmpty() ||
                    telepon.isEmpty() ||
                    lokasiDeskripsi.isEmpty() ||
                    isi.isEmpty() -> {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
                return
            }
            latValue == null || lngValue == null -> {
                Toast.makeText(this, "Latitude/Longitude belum valid", Toast.LENGTH_SHORT).show()
                return
            }
        }

        currentLat = latValue
        currentLng = lngValue
        progressBar.visibility = View.VISIBLE

        if (capturedBitmap == null) {
            saveLaporanToDatabase(
                tanggal,
                telepon,
                lokasiDeskripsi,
                linkMaps,
                isi,
                "tidak_ada_foto"
            )
        } else {
            CloudinaryHelper.uploadImageToCloudinary(
                capturedBitmap!!,
                onSuccess = { fotoUrl ->
                    // callback OkHttp di background thread → pindah ke main thread
                    runOnUiThread {
                        saveLaporanToDatabase(
                            tanggal,
                            telepon,
                            lokasiDeskripsi,
                            linkMaps,
                            isi,
                            fotoUrl
                        )
                    }
                },
                onError = { error ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Gagal upload foto: $error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    private fun saveLaporanToDatabase(
        tanggal: String,
        telepon: String,
        lokasiDeskripsi: String,
        linkMaps: String,
        isi: String,
        fotoUrl: String
    ) {
        val node = when (jenisLaporan) {
            "medis" -> "laporan_medis"
            "pencurian" -> "laporan_pencurian"
            else -> "laporan_kebakaran"
        }

        try {
            val timestamp = System.currentTimeMillis()
            val laporanId = databaseRef.child(node).push().key ?: return

            val dataMap = mapOf(
                "id" to laporanId,
                "jenis" to jenisLaporan,
                "tanggal" to tanggal,
                "telepon" to telepon,
                "lokasiDeskripsi" to lokasiDeskripsi,
                "latitude" to currentLat,
                "longitude" to currentLng,
                "linkMaps" to linkMaps,
                "deskripsi" to isi,
                "fotoUrl" to fotoUrl,
                "timestamp" to timestamp,
                "status" to "pending"
            )

            databaseRef.child(node).child(laporanId)
                .setValue(dataMap)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Laporan berhasil dikirim", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Gagal simpan database: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
