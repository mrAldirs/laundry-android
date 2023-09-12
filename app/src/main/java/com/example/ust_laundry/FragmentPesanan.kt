package com.example.ust_laundry

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import android.widget.ListAdapter
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ust_laundry.databinding.FragmentPesananBinding
import com.example.uts_dyah.PhotoHelper
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class FragmentPesanan :Fragment(), View.OnClickListener {
    lateinit var b:FragmentPesananBinding
    lateinit var thisParent: MainActivity
    lateinit var dialog: AlertDialog.Builder
    lateinit var v:View

    lateinit var photoHelper: PhotoHelper
    var imStr = ""
    var namaFile = ""
    var fileUri = Uri.parse("")

    val url = "http://192.168.137.1/web_service_laundry/crud_laundry.php"

    val ktgSpinner = mutableListOf<String>()
    lateinit var strKategori: ArrayAdapter<String>

    val data = mutableListOf<HashMap<String,String>>()
    lateinit var adapter: AdapterPesanan

    var id = ""

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnInsertpsn ->{
                AlertDialog.Builder(v.context)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setTitle("Konfirmasi!")
                    .setMessage("Apakah Anda sudah yakin ingin mengirim data?")
                    .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        insert_update("insert")
                        thisParent.recreate()
                    })
                    .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(v.context,"Berhasil Membatalkan!", Toast.LENGTH_SHORT).show()
                    })
                    .show()
                true
            }
            R.id.btnDeletepsn ->{
                AlertDialog.Builder(v.context)
                .setIcon(android.R.drawable.ic_input_get)
                .setTitle("Peringatan")
                .setMessage("Apakah Anda ingin menghapus data ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    delete(id)
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                })
                .show()
            true
            }
            R.id.btnUpdatepsn ->{
                AlertDialog.Builder(v.context)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setTitle("Konfirmasi!")
                    .setMessage("Apakah Anda sudah yakin ingin mengedit data?")
                    .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        insert_update("edit")
                        thisParent.recreate()
                    })
                    .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(v.context,"Berhasil Membatalkan!", Toast.LENGTH_SHORT).show()
                    })
                    .show()
                true
            }
        }
        true
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as MainActivity
        b = FragmentPesananBinding.inflate(layoutInflater)
        v = b.root

        photoHelper = PhotoHelper()
        adapter = AdapterPesanan(data, this)
        b.recyclerView.layoutManager = LinearLayoutManager(v.context)
        b.recyclerView.adapter = adapter

        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        b.btnChoose.setOnClickListener {
            requestPermission()
        }

        b.btnDeletepsn.setOnClickListener(this)
        b.btnInsertpsn.setOnClickListener(this)
        b.btnUpdatepsn.setOnClickListener(this)

        strKategori = ArrayAdapter(v.context, android.R.layout.simple_list_item_1, ktgSpinner)
        b.spKategori.adapter = strKategori

        return v
    }

    override fun onStart() {
        super.onStart()
        getKategori()
        showData()
    }

    fun requestPermission() = runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA ) {
        fileUri = photoHelper.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, photoHelper.getRcCamera())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            photoHelper.getRcCamera() -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        imStr = photoHelper.getBitMapToString(b.imageView, fileUri)
                        namaFile = photoHelper.getMyFileName()
                        Toast.makeText(v.context, "Berhasil upload foto", Toast.LENGTH_SHORT).show()
                    }

                    AppCompatActivity.RESULT_CANCELED -> {
                        // kode untuk kondisi kedua jika dibatalkan
                    }
                }
            }
        }
    }

    fun insert_update(mode: String) {
        val request = object : StringRequest(
            Method.POST,url,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                if (respon.equals("1")) {
                    Toast.makeText(v.context, "Berhasil mengirim data", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(v.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                when(mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                        hm.put("nama_pelanggan", b.edPelanggan.text.toString())
                        hm.put("alamat", b.edAlamat.text.toString())
                        hm.put("kategori", b.spKategori.selectedItem.toString())
                        hm.put("berat", b.edBerat.text.toString())
                        hm.put("harga", b.edHarga.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",namaFile)
                    }
                    "edit" -> {
                        hm.put("mode", "edit")
                        hm.put("id", id)
                        hm.put("nama_pelanggan", b.edPelanggan.text.toString())
                        hm.put("alamat", b.edAlamat.text.toString())
                        hm.put("kategori", b.spKategori.selectedItem.toString())
                        hm.put("berat", b.edBerat.text.toString())
                        hm.put("harga", b.edHarga.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",namaFile)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(v.context)
        queue.add(request)
    }

    fun getKategori() {
        val request = object : StringRequest(
            Method.POST,url,
            Response.Listener { response ->
                ktgSpinner.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    ktgSpinner.add(jsonObject.getString("kategori"))
                }
                strKategori.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->

            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("mode", "get_kategori")
                return hm
            }
        }
        val queue = Volley.newRequestQueue(v.context)
        queue.add(request)
    }

    private fun showData() {
        val request = object : StringRequest(
            Method.POST,url,
            Response.Listener { response ->
                data.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var  frm = HashMap<String,String>()
                    frm.put("id",jsonObject.getString("id"))
                    frm.put("nama_pelanggan",jsonObject.getString("nama_pelanggan"))
                    frm.put("alamat",jsonObject.getString("alamat"))
                    frm.put("kategori",jsonObject.getString("kategori"))
                    frm.put("berat",jsonObject.getString("berat"))
                    frm.put("harga",jsonObject.getString("harga"))
                    frm.put("img",jsonObject.getString("img"))

                    data.add(frm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(v.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("mode", "show_data")

                return hm
            }
        }
        val queue = Volley.newRequestQueue(v.context)
        queue.add(request)
    }

    fun delete(id: String) {
        val request = object : StringRequest(
            Method.POST,url,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                if (respon.equals("1")) {
                    Toast.makeText(v.context, "Berhasil menghapus data!", Toast.LENGTH_SHORT).show()
                    thisParent.recreate()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(v.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                hm.put("mode", "delete")
                hm.put("id", id)

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(v.context)
        queue.add(request)
    }
}