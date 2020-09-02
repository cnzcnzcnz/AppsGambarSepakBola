package com.crizkyr.gambarpemainbola.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crizkyr.gambarpemainbola.ui.BlogpostActivity
import com.crizkyr.gambarpemainbola.R
import com.crizkyr.gambarpemainbola.model.Blog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_thumbnail.view.*
import org.jsoup.Jsoup

class AdapterBlogPost(var listData: ArrayList<Blog>? = null, var context: Context) :
    RecyclerView.Adapter<AdapterBlogPost.AdapterHolder>() {

    class AdapterHolder(itemView: View, var listData: Blog? = null) :
        RecyclerView.ViewHolder(itemView) {
        val tvLoc = itemView.tv_textImage
        val ivThumbnail = itemView.iv_thumbnail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        val layoutInflater =
            LayoutInflater.from(context).inflate(R.layout.item_thumbnail, null, false)
        return AdapterHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listData!!.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val index = listData!![position]

        val document = Jsoup.parse(index.content)
        val link = document.select("img").first()
        val altImage = link.attr("alt")
        val srcImage = link.attr("src")

        holder.tvLoc.text = index.title
        Picasso.get().load(srcImage).into(holder.ivThumbnail)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, BlogpostActivity::class.java)
            intent.putExtra("idPost", index.id)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        holder.setIsRecyclable(false)
        holder.listData = index
    }
}