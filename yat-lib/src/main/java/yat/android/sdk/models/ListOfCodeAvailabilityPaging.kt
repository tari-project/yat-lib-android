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
 * Paging information
 * @param dir 
 * @param limit 
 * @param page 
 * @param sort 
 * @param tags 
 * @param total 
 */

data class ListOfCodeAvailabilityPaging (
    @SerializedName( "dir")
    val dir: ListOfCodeAvailabilityPaging.Dir,
    @SerializedName( "limit")
    val limit: kotlin.Int,
    @SerializedName( "page")
    val page: kotlin.Int,
    @SerializedName( "sort")
    val sort: kotlin.String,
    @SerializedName( "tags")
    val tags: kotlin.collections.Map<kotlin.String, kotlin.Any>,
    @SerializedName( "total")
    val total: kotlin.Long
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

    /**
    * 
    * Values: asc,desc
    */
    
    enum class Dir(val value: kotlin.String){
        @SerializedName( "Asc") asc("Asc"),
        @SerializedName( "Desc") desc("Desc");
    }
}

