package com.example.uts_dyah

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import android.widget.ImageView

class PhotoHelper {
    var namaFile = ""
    var fileUri = Uri.parse("")
    val RC_CAMERA = 200

    fun getMyFileName() : String {
        return this.namaFile
    }

    fun getRcCamera() : Int {
        return this.RC_CAMERA
    }

    fun getOutputMediaFile() : File?{
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ), "PhotoActivity")
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.e("mkdir", "Gagal Membuat Direktori")
            }
        }
        val mediaFile = File(mediaStorageDir.path + File.separator + this.namaFile)
        return mediaFile
    }

    fun getOutputMediaFileUri() : Uri{
        val timestamp =  SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        this.namaFile = "IMG_${timestamp}.jpg"
        this.fileUri = Uri.fromFile(getOutputMediaFile())
        return this.fileUri
    }

    fun getOutputMediaFileUri2() : Uri{
        val timestamp =  SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        this.namaFile = "IMG_${timestamp}.jpg"
        this.fileUri = Uri.fromFile(getOutputMediaFile())
        return this.fileUri
    }

    fun bitMapToString(bmp : Bitmap) : String{
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitMapToString(imV : ImageView, uri : Uri) : String {
        var bmp = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888)
        bmp = BitmapFactory.decodeFile(this.fileUri.path)
        var dim = 720
        if (bmp.height > bmp.width){
            bmp = Bitmap.createScaledBitmap(bmp, (bmp.width * dim).div(bmp.height), dim, true)
        }else{
            bmp = Bitmap.createScaledBitmap(bmp, dim, (bmp.height * dim).div(bmp.width), true)
        }
        imV.setImageBitmap(bmp)
        return bitMapToString(bmp)
        return ""
    }

}
