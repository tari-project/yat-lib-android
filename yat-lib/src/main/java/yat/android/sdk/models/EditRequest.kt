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

import com.squareup.moshi.Json
import java.io.Serializable

/**
 * 
 * @param bypassSingleRestrictions Optional: Allow many addresses per Tag
 * @param delete Optional: hashes of records to delete
 * @param insert Optional: list of records to add
 * @param merkleRoot Optional: merkle root (use WASM to generate)
 * @param signature Optional: signature (use WASM to generate)
 */

data class EditRequest (
    /* Optional: Allow many addresses per Tag */
    @Json(name = "bypass_single_restrictions")
    val bypassSingleRestrictions: kotlin.Boolean? = null,
    /* Optional: hashes of records to delete */
    @Json(name = "delete")
    val delete: kotlin.collections.List<kotlin.String>? = null,
    /* Optional: list of records to add */
    @Json(name = "insert")
    val insert: kotlin.collections.List<EditRequestInsert>? = null,
    /* Optional: merkle root (use WASM to generate) */
    @Json(name = "merkle_root")
    val merkleRoot: kotlin.String? = null,
    /* Optional: signature (use WASM to generate) */
    @Json(name = "signature")
    val signature: kotlin.String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}
