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
 * @param ownerEmail Assign lootbox an owner with matching email  Should not be set if owner_id is set
 * @param ownerId Assign lootbox an owner, if set requires status `Owned`
 * @param status Update status  If status is `Used` lootbox with be automatically opened
 * @param yats LootBox emoji IDs
 */

data class AdminUpdateLootBoxBody (
    /* Assign lootbox an owner with matching email  Should not be set if owner_id is set */
    @SerializedName( "owner_email")
    val ownerEmail: kotlin.String? = null,
    /* Assign lootbox an owner, if set requires status `Owned` */
    @SerializedName( "owner_id")
    val ownerId: java.util.UUID? = null,
    /* Update status  If status is `Used` lootbox with be automatically opened */
    @SerializedName( "status")
    val status: AdminUpdateLootBoxBody.Status? = null,
    /* LootBox emoji IDs */
    @SerializedName( "yats")
    val yats: kotlin.collections.List<kotlin.String>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

    /**
    * Update status  If status is `Used` lootbox with be automatically opened
    * Values: draft,available,owned,used
    */
    
    enum class Status(val value: kotlin.String){
        @SerializedName( "Draft") draft("Draft"),
        @SerializedName( "Available") available("Available"),
        @SerializedName( "Owned") owned("Owned"),
        @SerializedName( "Used") used("Used");
    }
}

