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
 * @param email Email
 * @param gRecaptchaResponse Response from google Recaptcha
 * @param redirect Redirect path
 * @param userId User ID
 */

data class MagicLinkLoginRequest (
    /* Email */
    @SerializedName( "email")
    val email: kotlin.String? = null,
    /* Response from google Recaptcha */
    @SerializedName( "g_recaptcha_response")
    val gRecaptchaResponse: kotlin.String? = null,
    /* Redirect path */
    @SerializedName( "redirect")
    val redirect: kotlin.String? = null,
    /* User ID */
    @SerializedName( "user_id")
    val userId: java.util.UUID? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}

