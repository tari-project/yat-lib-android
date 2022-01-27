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
 * @param config 
 * @param description A description for the loot box type
 * @param name the name of the loot box type
 */

data class AdminNewLootBoxType (
    @SerializedName( "config")
    val config: AdminNewLootBoxTypeConfig,
    /* A description for the loot box type */
    @SerializedName( "description")
    val description: kotlin.String,
    /* the name of the loot box type */
    @SerializedName( "name")
    val name: kotlin.String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}

