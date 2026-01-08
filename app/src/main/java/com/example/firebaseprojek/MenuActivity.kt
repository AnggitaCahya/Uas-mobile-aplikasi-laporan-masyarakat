package com.example.firebaseprojek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Hubungkan CardView di layout
        val cardKebakaran = findViewById<CardView>(R.id.cardKebakaran)
        val cardMedis = findViewById<CardView>(R.id.cardMedis)
        val cardPencurian = findViewById<CardView>(R.id.cardPencurian)
        val cardRiwayat = findViewById<CardView>(R.id.cardRiwayat)

        // Klik masing-masing kartu
        cardKebakaran.setOnClickListener {
            val intent = Intent(this, LaporanUmumActivity::class.java)
            intent.putExtra(LaporanUmumActivity.EXTRA_JENIS, "kebakaran")
            startActivity(intent)
        }

        cardMedis.setOnClickListener {
            val intent = Intent(this, LaporanUmumActivity::class.java)
            intent.putExtra(LaporanUmumActivity.EXTRA_JENIS, "medis")
            startActivity(intent)
        }

        cardPencurian.setOnClickListener {
            val intent = Intent(this, LaporanUmumActivity::class.java)
            intent.putExtra(LaporanUmumActivity.EXTRA_JENIS, "pencurian")
            startActivity(intent)
        }

        cardRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatLaporanActivity::class.java))
        }
    }
}
