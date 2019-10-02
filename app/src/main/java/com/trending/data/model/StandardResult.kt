package com.trending.data.model

import com.google.gson.annotations.SerializedName

data class StandardResult(

    @field:SerializedName("forks")
    val forks: Int? = null,

    @field:SerializedName("builtBy")
    val builtBy: List<BuiltByItem?>? = null,

    @field:SerializedName("author")
    val author: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("avatar")
    val avatar: String? = null,

    @field:SerializedName("languageColor")
    val languageColor: String? = null,

    @field:SerializedName("stars")
    val stars: Int? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("currentPeriodStars")
    val currentPeriodStars: Int? = null
)