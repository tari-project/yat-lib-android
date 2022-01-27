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
 * @param ipr The inverse probability ratio. This is a 1:n value. i.e. an ipr of 5 means a 1 in 5 chance of occurring, or 20%
 * @param maxScore The highest (inclusive) rhythm score range for inclusion when the probability spec hits
 * @param minScore The lowest (inclusive) rhythm score range for inclusion when the probability spec hits
 */

data class AdminNewLootBoxTypeConfigWeights (
    /* The inverse probability ratio. This is a 1:n value. i.e. an ipr of 5 means a 1 in 5 chance of occurring, or 20% */
    @SerializedName( "ipr")
    val ipr: kotlin.Double,
    /* The highest (inclusive) rhythm score range for inclusion when the probability spec hits */
    @SerializedName( "max_score")
    val maxScore: kotlin.Long,
    /* The lowest (inclusive) rhythm score range for inclusion when the probability spec hits */
    @SerializedName( "min_score")
    val minScore: kotlin.Long
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

}

