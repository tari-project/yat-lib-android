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
 * @param data Data value allows to store any Json value, limited by 250Kb
 * @param linkedTags Link tag items as part of the transaction  All previously linked tags not present in new request will be deleted
 */

data class StoreJsonBody (
    /* Data value allows to store any Json value, limited by 250Kb */
    @SerializedName( "data")
    val data: kotlin.Any,
    /* Link tag items as part of the transaction  All previously linked tags not present in new request will be deleted */
    @SerializedName( "linked_tags")
    val linkedTags: kotlin.collections.List<EditRequestInsert>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}

