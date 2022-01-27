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
 * @param activationSource Optional: Source of activation
 * @param currentPassword Optional: Current password, must be provided if one exists
 * @param email Optional: Email
 * @param firstName Optional: First name
 * @param freeLimit Optional: Free limit for how many yats the user may purchase
 * @param lastName Optional: Last name
 * @param password Optional: User password
 * @param role Optional: Update the user role
 */

data class AdminUpdateUserParameters (
    /* Optional: Source of activation */
    @SerializedName( "activation_source")
    val activationSource: kotlin.String? = null,
    /* Optional: Current password, must be provided if one exists */
    @SerializedName( "current_password")
    val currentPassword: kotlin.String? = null,
    /* Optional: Email */
    @SerializedName( "email")
    val email: kotlin.String? = null,
    /* Optional: First name */
    @SerializedName( "first_name")
    val firstName: kotlin.String? = null,
    /* Optional: Free limit for how many yats the user may purchase */
    @SerializedName( "free_limit")
    val freeLimit: kotlin.Int? = null,
    /* Optional: Last name */
    @SerializedName( "last_name")
    val lastName: kotlin.String? = null,
    /* Optional: User password */
    @SerializedName( "password")
    val password: kotlin.String? = null,
    /* Optional: Update the user role */
    @SerializedName( "role")
    val role: AdminUpdateUserParameters.Role? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

    /**
    * Optional: Update the user role
    * Values: admin,orgController,orgMember,orgOwner,bot,`super`,user
    */
    
    enum class Role(val value: kotlin.String){
        @SerializedName( "Admin") admin("Admin"),
        @SerializedName( "OrgController") orgController("OrgController"),
        @SerializedName( "OrgMember") orgMember("OrgMember"),
        @SerializedName( "OrgOwner") orgOwner("OrgOwner"),
        @SerializedName( "Bot") bot("Bot"),
        @SerializedName( "Super") `super`("Super"),
        @SerializedName( "User") user("User");
    }
}

