package com.example.firebaseprojek

data class Laporan(
    val jenisLaporan: String = "",   // kebakaran / pencurian / medis
    val tanggal: String = "",
    val telepon: String = "",
    val lokasi: String = "",
    val linkMaps: String = "",
    val isi: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val fotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
