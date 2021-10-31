package yat.android.api.json

import java.io.Serializable

data class LoadValueFromKeyValueStoreResponse(
    val createdAt: String,
    val data: DynamicStoreData,
    val isLocked: Boolean,
    val lockedFutureWritesAt: String?,
    val updatedAt: String
) : Serializable

