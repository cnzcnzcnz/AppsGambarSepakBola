package com.crizkyr.gambarpemainbola.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crizkyr.gambarpemainbola.BuildConfig
import com.crizkyr.gambarpemainbola.R
import com.google.android.gms.ads.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ImageActivity : AppCompatActivity() {

    lateinit var builder: AlertDialog.Builder
    lateinit var pDialog: ProgressDialog

    private var screenWidth = 0
    private var screenHeight = 0

    private lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        pDialog = ProgressDialog(this)

        val metrics = DisplayMetrics()
        builder = AlertDialog.Builder(this)

        this.windowManager.defaultDisplay.getMetrics(metrics)

        MobileAds.initialize(this){}

        mAdView = findViewById(R.id.adView)
        val testDeviceId = ArrayList<String>()
        testDeviceId.add(AdRequest.DEVICE_ID_EMULATOR)



        if(BuildConfig.DEBUG){
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceId)
                .build()
            MobileAds.setRequestConfiguration(requestConfig)
        }
        else {

        }


        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels

        val imageLink = intent.getStringExtra("imageLink")

        Picasso.get().load(imageLink)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(iv_image_post)

        iv_button_set_as_wallpaper.setOnClickListener {
            SetWallpaper().execute(imageLink)
        }
        iv_btn_download.setOnClickListener {
            SaveWallpaperAsync(this).execute(imageLink)
        }
    }

    inner class SetWallpaper: AsyncTask<String, Void?, Void?>(){
        lateinit var bitmap: Bitmap
        lateinit var inputStream: InputStream

        override fun onPreExecute() {
            super.onPreExecute()

            pDialog.setMessage("Mohon tunggu, sedang mengatur wallpaper")
            pDialog.show()
        }

        override fun doInBackground(vararg p0: String?): Void? {
            try{
                val url = URL(p0[0])
                val connection = url.openConnection()
                connection.doInput = true
                connection.connect()
                inputStream = connection.getInputStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            }
            catch (e: Exception){
                Log.d("exception", e.message!!)
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            val wpManager = getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager?
            wpManager!!.setWallpaperOffsetSteps(0.0f, 0.0f)
            wpManager.suggestDesiredDimensions(screenWidth, screenHeight)
            try {
                builder.setTitle("Sukses")
                builder.setMessage("Wallpaper sudah terpasang")
                builder.setNegativeButton("OK", null)
                builder.show()

                wpManager!!.setBitmap(bitmap)
//                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                pDialog.dismiss()
            } catch (e: Exception) {

                builder.setTitle("Gagal")
                builder.setMessage("Wallpaper gagal terpasang")
                builder.setNegativeButton("OK", null)
                builder.show()
//                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                pDialog.dismiss()
            }
        }

    }

    inner class SaveWallpaperAsync(private val context: Context) :
        AsyncTask<String?, String?, String?>() {
        private var pDialog: ProgressDialog? = null
        var ImageUrl: URL? = null
        var bmImg: Bitmap? = null
        override fun onPreExecute() { // TODO Auto-generated method stub
            super.onPreExecute()
            pDialog = ProgressDialog(context)
            pDialog!!.setMessage("Sedang mengunduh gambar...")
            pDialog!!.isIndeterminate = false
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }

        override fun doInBackground(vararg args: String?): String? { // TODO Auto-generated method stub
            var `is`: InputStream? = null
            try {
                ImageUrl = URL(args[0])
                val conn: HttpURLConnection = ImageUrl!!
                    .openConnection() as HttpURLConnection
                conn.setDoInput(true)
                conn.connect()
                `is` = conn.getInputStream()
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                bmImg = BitmapFactory.decodeStream(`is`, null, options)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                val path = ImageUrl!!.path
                val idStr = path.substring(path.lastIndexOf('/') + 1)
                val filepath = Environment.getExternalStorageDirectory()
                val dir = File(
                    filepath.absolutePath
                        .toString() + "/Wallpapers/"
                )
                dir.mkdirs()
                val file = File(dir, idStr)
                val fos = FileOutputStream(file)
                bmImg!!.compress(CompressFormat.JPEG, 75, fos)
                fos.flush()
                fos.close()
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
            return null
        }

        override fun onPostExecute(args: String?) { // TODO Auto-generated method stub
            if (bmImg == null) {
                Toast.makeText(
                    context, "Image still loading...",
                    Toast.LENGTH_SHORT
                ).show()
                pDialog!!.dismiss()
            } else {
                if (pDialog != null) {
                    if (pDialog!!.isShowing) {
                        pDialog!!.dismiss()
                    }
                }
                builder.setTitle("Sukses")
                builder.setMessage("Gambar sudah terunduh")
                builder.setNegativeButton("OK", null)
                builder.show()
            }
        }
    }
}
