package com.example.ust_laundry

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.example.ust_laundry.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var b:ActivityMainBinding
    lateinit var db : SQLiteDatabase
    lateinit var fragKategori: FragmentKategori
    lateinit var fragmentPesanan: FragmentPesanan
    lateinit var ft : FragmentTransaction

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NAMA = "nama"
    val DEF_NAMA = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        fragKategori = FragmentKategori()
        fragmentPesanan = FragmentPesanan()

        db = DBOpenHelper(this).writableDatabase

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        b.tv.text = "Selamat Datang "+preferences.getString(NAMA, DEF_NAMA).toString()

        b.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.itemKatgeori ->{
                    ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frameLayout, fragKategori)
                    ft.commit()
                    b.frameLayout.setBackgroundColor(
                        Color.argb(245, 255, 255, 225)
                    )
                    b.frameLayout.visibility = View.VISIBLE
                }
                R.id.itemPesanan ->{
                    ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frameLayout, fragmentPesanan)
                    ft.commit()
                    b.frameLayout.setBackgroundColor(
                        Color.argb(245, 255, 255, 225)
                    )
                    b.frameLayout.visibility = View.VISIBLE
                }
            }
            true
        }
    }
}


