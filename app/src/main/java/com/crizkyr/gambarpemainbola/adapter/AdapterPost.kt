package com.crizkyr.gambarpemainbola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crizkyr.gambarpemainbola.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image.view.*

class AdapterPost(var listData: ArrayList<String>, var context: Context, var callback: ActionListener) :
    RecyclerView.Adapter<AdapterPost.AdapterHolder>() {

    interface ActionListener{
        fun OnClickListener(imageLink: String)
    }
    inner class AdapterHolder(itemView: View, var listData: String? = null) :
        RecyclerView.ViewHolder(itemView) {
        val ivImage = itemView.iv_image

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        val layoutInflater =
            LayoutInflater.from(context).inflate(R.layout.item_image, null, false)
        return AdapterHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val index = listData[position]
        Picasso.get().load(index)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .into(holder.ivImage)

        holder.itemView.setOnClickListener {
            callback.OnClickListener(index)
//            if(mInterstitialAd.isLoaded){
//                mInterstitialAd.show()
//            }
//            else {
//                Log.d("TAG", "The interstitial wasn't loaded yet.")
//            }
//            val intent = Intent(context, ImageActivity::class.java).apply {
//                putExtra("imageLink", index)
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            context.startActivity(intent)
        }

        holder.setIsRecyclable(false)
        holder.listData = index
    }
}