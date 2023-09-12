package com.example.ust_laundry

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBOpenHelper (context :
Context):SQLiteOpenHelper(context,DB_Name,null,DB_Ver){
    override fun onCreate(db: SQLiteDatabase?) {
        val tUser = "create table user(id_user integer primary key autoincrement, " +
                "username text not null, password text not null)"
        val tKategori = "create table kategori( id_kategori integer primary key autoincrement, nama_kategori text not null, harga int not null)"
        val tPesanan = "create table pesanan(id_pesanan integer primary key autoincrement, " +
                "nama_pelanggan text not null, id_kategori integer not null," +
                "berat integer not null, total integer)"

        db?.execSQL(tUser)
        db?.execSQL(tKategori)
        db?.execSQL(tPesanan)

        val cv = ContentValues()
        cv.put("username", "ananta")
        cv.put("password", "123")
        db?.insert("user",null,cv)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int,
                           newVersion: Int) {
    }
    companion object {
        val DB_Name = "laundry"
        val DB_Ver = 1
    }
}