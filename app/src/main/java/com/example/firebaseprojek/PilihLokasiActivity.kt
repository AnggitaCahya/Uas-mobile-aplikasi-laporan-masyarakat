package com.example.firebaseprojek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PilihLokasiActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_lokasi)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnPilihLokasi).setOnClickListener {
            selectedLatLng?.let { latLng ->
                val data = Intent().apply {
                    putExtra("lat", latLng.latitude)
                    putExtra("lng", latLng.longitude)
                }
                setResult(Activity.RESULT_OK, data)
                finish()
            } ?: run {
                Toast.makeText(this, "Silakan tap peta untuk pilih titik", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Kebakaran"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
    }
}
