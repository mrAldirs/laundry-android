package com.example.ust_laundry

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ust_laundry.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var b:ActivityLoginBinding

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NAMA = "nama"
    val DEF_NAMA = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        b.btLogin.setOnClickListener {
            if (b.txUsn.text.toString().equals("ananta") && b.txPwd.text.toString().equals("123")) {
                val prefEditor = preferences.edit()
                prefEditor.putString(NAMA, b.txUsn.text.toString())
                prefEditor.commit()

                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Berhasil Login!", Toast.LENGTH_SHORT).show()
            } else if (b.txUsn.text.toString().equals("") || b.txPwd.text.toString().equals("")) {
                Toast.makeText(this, "Username atau Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Username & Password Anda Salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}