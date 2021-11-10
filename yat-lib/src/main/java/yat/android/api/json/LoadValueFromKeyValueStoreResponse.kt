package yat.android.api.json

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoadValueFromKeyValueStoreResponse(
    @SerializedName("createAt")
    val createdAt: String,
    @SerializedName("data")
    val data: DynamicStoreData,
    @SerializedName("isLocked")
    val isLocked: Boolean,
    @SerializedName("lockedFutureWritesAt")
    val lockedFutureWritesAt: String?,
    @SerializedName("updateAt")
    val updatedAt: String
) : Serializable

