package com.crizkyr.gambarpemainbola.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.crizkyr.gambarpemainbola.BuildConfig
import com.crizkyr.gambarpemainbola.adapter.AdapterBlogPost
import com.crizkyr.gambarpemainbola.consts.Config
import com.crizkyr.gambarpemainbola.model.Blog
import com.crizkyr.gambarpemainbola.model.response.BlogApiResponse
import com.google.android.gms.ads.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import com.crizkyr.gambarpemainbola.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_layout.view.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    private var recyclerView: RecyclerView? = null
    private var adapterBlogPost: AdapterBlogPost? = null

    private var listUrl = ArrayList<Blog>()
    private var url = "${Config.baseUrl}${Config.idBlog}/posts?maxResults=500&key=${Config.apiKey}"
    private var PERMISSION_ALL = 1

    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.SET_WALLPAPER,
        Manifest.permission.SET_WALLPAPER_HINTS
    )

    private var mAdView: AdView? = null
    private var pDialog: ProgressDialog? = null

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null) {
            for (permission in PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }

        MobileAds.initialize(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val adView = AdView(this)
        adView.adSize = (AdSize.BANNER)
        adView.adUnitId = Config.adUnitIdBanner

        mAdView = findViewById(R.id.adView)
        val testDeviceId = ArrayList<String>()
        testDeviceId.add(Config.testDeviceId)
        testDeviceId.add(AdRequest.DEVICE_ID_EMULATOR)
        val adRequest = AdRequest.Builder().build()

        val requestConfig = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceId)
            .build()
        MobileAds.setRequestConfiguration(requestConfig)

//        if (BuildConfig.DEBUG) {
//
//        }
        mAdView?.loadAd(adRequest)

        initView()
    }

    private fun initView() {
        recyclerView = rv_url
        pDialog = ProgressDialog(this)
        pDialog?.setMessage("Please wait...")
        pDialog?.show()

        val drawer = drawer_layout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        val navigationView = navigation_view
        val headerView = navigationView.getHeaderView(0)
        val tvVersion = headerView.tv_version
        tvVersion.text = BuildConfig.VERSION_NAME
        try {

            val packageInfo = this.packageManager.getPackageInfo(packageName, 0)

            Log.d("check", "${packageInfo.versionName}")
        }
        catch (e: Exception){
            Log.e("MainActivity", e.message + e.stackTrace + e.cause)
        }

        drawer.setDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_about_us -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("About us")
                    dialog.setMessage("We are a group of people who like to make mobile apps.")
                    dialog.setNegativeButton("OK", null)
                    dialog.show()
                }
                R.id.nav_more_apps -> {
//                    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Abstraksi+Developer")))
                }
                R.id.nav_contact_us -> {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.type = "message/rfc822"
                    intent.putExtra(Intent.EXTRA_EMAIL, "abstraksideveloper@gmail.com")
                    startActivity(Intent.createChooser(intent, "Send Email"))
                }
                R.id.nav_rate_us -> {
                    try{
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")));
                    }
                    catch(e: Exception){
                        Log.d("MainActivity", e.message!!)
                    }
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
        getBlogPost()
    }

    private fun getBlogPost() {
        val queue = Volley.newRequestQueue(this)
        val gson = Gson()
        val request = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->

            if(response != null){
                try{
                    val classResponse = gson.fromJson(response.toString(), BlogApiResponse::class.java)
                    listUrl.addAll(classResponse.items!!)
                    adapterBlogPost = AdapterBlogPost(listUrl, this)
                    recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    recyclerView!!.adapter = adapterBlogPost
                    pDialog!!.dismiss()
                }
                catch (e: Exception){
                    Log.e("MainActivity", "${e.message} ${e.stackTrace} ${e.stackTrace}")
                }
            }
                Log.d("MainActivity", "response : $response")

            },
            Response.ErrorListener {
                Toast.makeText(this, "There was an error, please check your Internet Connection", Toast.LENGTH_LONG).show()
                pDialog?.dismiss()
            })
        queue.add(request)
    }
}
