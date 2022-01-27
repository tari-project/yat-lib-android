/**
* Emoji ID API server
* Emoji ID is a directory service that associates almost any type of structured data with a short, memorable identifier the emoji id.
*
* The version of the OpenAPI document: 0.2.262
* 
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package yat.android.sdk.models



import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 
 * @param createdAt Created at time for the record
 * @param data Data value stored by key and EmojiID  If there is no value for the key an empty object `{}` is returned
 * @param isLocked Data is locked
 * @param updatedAt Updated at time for the record
 * @param lockedFutureWritesAt Time the record was locked from future writes
 */

data class LoadJsonResponse (
    /* Created at time for the record */
    @SerializedName( "created_at")
    val createdAt: java.time.OffsetDateTime,
    /* Data value stored by key and EmojiID  If there is no value for the key an empty object `{}` is returned */
    @SerializedName( "data")
    val data: DynamicStoreData?,
    /* Data is locked */
    @SerializedName( "is_locked")
    val isLocked: kotlin.Boolean,
    /* Updated at time for the record */
    @SerializedName( "updated_at")
    val updatedAt: java.time.OffsetDateTime,
    /* Time the record was locked from future writes */
    @SerializedName( "locked_future_writes_at")
    val lockedFutureWritesAt: java.time.OffsetDateTime? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}

