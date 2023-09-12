package com.example.ust_laundry

import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AlertDialog
import androidx.cursoradapter.widget.CursorAdapter
import androidx.fragment.app.Fragment
import com.example.ust_laundry.databinding.FragmentKategoriBinding

class FragmentKategori : Fragment(), View.OnClickListener{
    lateinit var b:FragmentKategoriBinding
    lateinit var thisParent : MainActivity
    lateinit var lsAdapter: ListAdapter
    lateinit var dialog: AlertDialog.Builder
    lateinit var v : View
    var idKategori : String = ""
    var namaKategori : String = ""
    var harga : Int = 0
    lateinit var db : SQLiteDatabase
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnInsert ->{
                dialog.setTitle("Konfirmasi").setMessage(
                    "Data yang akan dimasukan sudah benar?"
                )
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Ya", btnInsertDialog)
                    .setNegativeButton("Tidak", null)
                dialog.show()
                showDataKategori()
            }
            R.id.btnUpdate ->{
                dialog.setTitle("Konfirmasi").setMessage(
                    "Data yang akan dimasukkan sudah benar?"
                )
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Ya", btnUpdateDialog)
                    .setNegativeButton("Tidak", null)
                dialog.show()
            }
            R.id.btnDelete ->{
                dialog.setTitle("Konfirmasi").setMessage(
                    "Yakin akan menghapus data ini?"
                )
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Ya", btnDeleteDialog)
                    .setNegativeButton("Tidak", null)
                dialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as MainActivity
        b = FragmentKategoriBinding.inflate(layoutInflater)
        v = b.root

        b.btnInsert.setOnClickListener(this)
        b.btnUpdate.setOnClickListener(this)
        b.btnDelete.setOnClickListener(this)
        dialog = AlertDialog.Builder(thisParent)
        b.lsKategori.setOnItemClickListener(itemClick)
        return v
    }
    fun showDataKategori(){
        val c : Cursor = db.rawQuery(
            "select id_kategori as _id , nama_kategori, harga from kategori",
            null)
        val adapter = androidx.cursoradapter.widget.SimpleCursorAdapter(
            thisParent, R.layout.item_kategori, c,
            arrayOf("_id", "nama_kategori", "harga"),
            intArrayOf(R.id.txidkategori, R.id.txnamakategori, R.id.txharga),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        b.lsKategori.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        showDataKategori()
    }
    val itemClick = AdapterView.OnItemClickListener{
        parent, view, position, id ->
        val c:Cursor = parent.adapter.getItem(position) as Cursor
        idKategori = c.getString(c.getColumnIndexOrThrow("_id"))
        b.edNamaKategori.setText(c.getString(
            c.getColumnIndexOrThrow("nama_kategori")
        ))
        b.edHarga.setText(c.getString(
            c.getColumnIndexOrThrow("harga")
        ))
    }
    val btnInsertDialog = DialogInterface.OnClickListener{dialog,which ->
        val cv = ContentValues()
        cv.put("nama_kategori", b.edNamaKategori.text.toString())
        cv.put("harga", b.edHarga.text.toString().toInt())
        db.insert("kategori", null, cv)
        showDataKategori()
    }
    val btnUpdateDialog = DialogInterface.OnClickListener{dialog, which ->
        val cv = ContentValues()
        cv.put("nama_kategori", b.edNamaKategori.text.toString())
        cv.put("harga", b.edHarga.text.toString().toInt())
        db.update("kategori", cv,"id_kategori = $idKategori",null)
        showDataKategori()
    }
    val btnDeleteDialog = DialogInterface.OnClickListener{dialog,which ->
        db.delete("kategori","id_kategori = $idKategori",null)
        showDataKategori()
    }

}

