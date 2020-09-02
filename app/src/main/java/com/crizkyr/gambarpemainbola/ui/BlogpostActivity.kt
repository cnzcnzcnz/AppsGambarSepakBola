package com.crizkyr.gambarpemainbola.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.crizkyr.gambarpemainbola.R
import com.crizkyr.gambarpemainbola.adapter.AdapterPost
import com.crizkyr.gambarpemainbola.consts.Config
import com.crizkyr.gambarpemainbola.model.Blog
import com.google.android.gms.ads.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_blogpost.*
import org.jsoup.Jsoup


class BlogpostActivity : AppCompatActivity() {

    private var url = String()
    private var idPost = String()
    private var listUrl = ArrayList<String>()

    lateinit var recyclerView: RecyclerView
    lateinit var adapterPost: AdapterPost
    lateinit var mAdView: AdView
    lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blogpost)

        MobileAds.initialize(this){}

        mAdView = findViewById(R.id.adView)

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = Config.adUnitIdInterstitial
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        idPost = intent.getStringExtra("idPost")!!
        url = "${Config.baseUrl}${Config.idBlog}/posts/$idPost?key=${Config.apiKey}"

        recyclerView = rv_image

        val pDialog = ProgressDialog(this)
        pDialog.setMessage("Please wait...")
        pDialog.show()

        adapterPost = AdapterPost(listUrl, this, object: AdapterPost.ActionListener{
                override fun OnClickListener(imageLink: String) {
                    val intent = Intent(applicationContext, ImageActivity::class.java).apply {
                        putExtra("imageLink", imageLink)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
            })

        val gson = Gson()
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url, Response.Listener {
            val response = gson.fromJson(it, Blog::class.java)
            val document = Jsoup.parse(response.content)
            val link = document.select("img")

            for(i in link) {
                listUrl.add(i.attr("src"))
            }
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapterPost
            pDialog.dismiss()

        }, Response.ErrorListener {
            Log.d("Error", it.message!!)
            pDialog.dismiss()
        })
        queue.add(request)
    }
}
