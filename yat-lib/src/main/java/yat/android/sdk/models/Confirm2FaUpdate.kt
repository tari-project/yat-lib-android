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
 * @param code Auth code of new 2FA provider
 */

data class Confirm2FaUpdate (
    /* Auth code of new 2FA provider */
    @SerializedName( "code")
    val code: kotlin.String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}

