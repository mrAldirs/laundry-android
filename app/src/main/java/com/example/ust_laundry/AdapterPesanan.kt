package com.example.ust_laundry

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterPesanan(val data: List<HashMap<String,String>>, val parent: FragmentPesanan) :
    RecyclerView.Adapter<AdapterPesanan.HolderDataAdapter>(){
    class HolderDataAdapter(v : View) : RecyclerView.ViewHolder(v) {
        val nm = v.findViewById<TextView>(R.id.itemNama)
        val ktg = v.findViewById<TextView>(R.id.itemKategori)
        val alm = v.findViewById<TextView>(R.id.itemAlamat)
        val brt = v.findViewById<TextView>(R.id.itemHarga)
        val hrg = v.findViewById<TextView>(R.id.itemBerat)
        val btn = v.findViewById<TextView>(R.id.btnLokasi)
        val img = v.findViewById<ImageView>(R.id.itemImage)
        val ln = v.findViewById<LinearLayout>(R.id.liner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAdapter {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan,parent,false)
        return HolderDataAdapter(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HolderDataAdapter, position: Int) {
        val item = data.get(position)
        holder.nm.setText(item.get("nama_pelanggan"))
        holder.ktg.setText("Kategori : "+item.get("kategori"))
        holder.alm.setText("Alamat : "+item.get("alamat"))
        holder.brt.setText("Berat : "+item.get("berat")+" Kg")
        holder.hrg.setText("Harga : Rp."+item.get("harga"))
        Picasso.get().load(item.get("img")).into(holder.img)

        holder.btn.setOnClickListener {
            val intent = Intent(it.context, MapsActivity::class.java)
            intent.putExtra("alm", item.get("alamat").toString())
            it.context.startActivity(intent)
        }

        holder.ln.setOnClickListener {
            parent.id = item.get("id").toString()
            parent.b.edPelanggan.setText(item.get("nama_pelanggan").toString())
            parent.b.spKategori.setSelection(parent.strKategori.getPosition(item.get("kategori").toString()))
            parent.b.edAlamat.setText(item.get("alamat").toString())
            parent.b.edBerat.setText(item.get("berat").toString())
            parent.b.edHarga.setText(item.get("harga").toString())
            Picasso.get().load(item.get("img")).into(parent.b.imageView)
        }
    }
}