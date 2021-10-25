package yat.android.api

import java.io.Serializable

class ResponseError(
    val code: Int,
    val reason: String
): Serializable