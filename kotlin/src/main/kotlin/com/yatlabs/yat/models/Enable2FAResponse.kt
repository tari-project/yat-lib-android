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
package com.yatlabs.yat.models


import com.squareup.moshi.Json
import java.io.Serializable

/**
 * 
 * @param backupCodes One time backup codes to login
 * @param gaQrCodeSvg GA secret as QR code in svg image
 * @param gaSecret GA base32 encoded secret, will be null when code is disabled
 * @param phoneLastDigits Phone last digits
 */

data class Enable2FAResponse (
    /* One time backup codes to login */
    @Json(name = "backup_codes")
    val backupCodes: kotlin.collections.List<kotlin.String>? = null,
    /* GA secret as QR code in svg image */
    @Json(name = "ga_qr_code_svg")
    val gaQrCodeSvg: kotlin.String? = null,
    /* GA base32 encoded secret, will be null when code is disabled */
    @Json(name = "ga_secret")
    val gaSecret: kotlin.String? = null,
    /* Phone last digits */
    @Json(name = "phone_last_digits")
    val phoneLastDigits: kotlin.String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}
