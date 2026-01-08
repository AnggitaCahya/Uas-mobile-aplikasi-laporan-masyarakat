package com.example.firebaseprojek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()
            val confirmPassword = etConfirmPassword.text?.toString()?.trim().orEmpty()

            // Validasi input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Semua field wajib diisi")
                return@setOnClickListener
            }
            if (password.length < 6) {
                showToast("Password minimal 6 karakter")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showToast("Password dan konfirmasi tidak sama")
                return@setOnClickListener
            }

            // Proses registrasi Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // Simpan data user ke Realtime Database (jika gagal, tetap lanjut ke login)
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val dbRef = FirebaseDatabase.getInstance().reference
                            val userData = mapOf(
                                "name" to name,
                                "email" to email
                            )
                            dbRef.child("users").child(uid).setValue(userData)
                                .addOnFailureListener { e ->
                                    // Hanya tampilkan pesan, tapi tidak menghalangi pindah ke login
                                    showToast("Gagal simpan data: ${e.message}")
                                }
                        }

                        // Selalu masuk ke halaman LOGIN setelah registrasi berhasil
                        showToast("Registrasi berhasil, silakan login")

                        // GANTI MainActivity jika halaman login kamu pakai nama class lain
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        startActivity(intent)
                        finish()

                    } else {
                        showToast("Registrasi gagal: ${task.exception?.message}")
                    }
                }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
