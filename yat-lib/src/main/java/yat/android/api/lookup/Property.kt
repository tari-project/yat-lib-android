package yat.android.api.lookup

import com.google.gson.annotations.SerializedName

data class Property (
    val address: String,
    val category: String,
    val default: Boolean,
    val description: String,

    @SerializedName("long_name")
    val longName: String,

    @SerializedName("settlement_network")
    val settlementNetwork: String,

    @SerializedName("short_name")
    val shortName: String,

    val signature: String
)