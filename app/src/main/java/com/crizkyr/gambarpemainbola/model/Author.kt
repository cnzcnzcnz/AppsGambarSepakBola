package com.crizkyr.gambarpemainbola.model

import com.google.gson.annotations.SerializedName

class Author {
    @SerializedName("id")
    val id: String ? = null
    @SerializedName("displayName")
    val displayName: String ? = null
    @SerializedName("url")
    val url: String ? = null
    @SerializedName("image")
    val image: UrlImage ? = null

}