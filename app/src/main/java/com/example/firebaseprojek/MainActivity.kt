package com.example.firebaseprojek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {   // Halaman LOGIN

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // ID sesuai dengan XML login
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnToRegister = findViewById<Button>(R.id.btnToRegister)

        // Tombol LOGIN
        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email dan password wajib diisi")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Login berhasil")

                        // Pindah ke halaman MENU (activity_menu.xml)
                        val intent = Intent(this, MenuActivity::class.java)
                        intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        startActivity(intent)
                        finish()   // tutup halaman login
                    } else {
                        showToast("Login gagal: ${task.exception?.message}")
                    }
                }
        }

        // Tombol ke halaman REGISTER
        btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
