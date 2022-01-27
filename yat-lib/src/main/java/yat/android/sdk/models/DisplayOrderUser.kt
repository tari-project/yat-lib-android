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
 * The details of the user placing this order.
 * @param createdAt 
 * @param freeLimit 
 * @param id 
 * @param pubkeys 
 * @param remainingFreeEmoji 
 * @param role 
 * @param twoFactorShouldPrompt 
 * @param updatedAt 
 * @param alternateId 
 * @param deactivatedAt 
 * @param email 
 * @param emailVerifiedAt 
 * @param firstName 
 * @param lastName 
 * @param source 
 * @param twoFactorAuth 
 * @param twoFactorLastPromptedAt 
 */

data class DisplayOrderUser (
    @SerializedName( "created_at")
    val createdAt: java.time.OffsetDateTime,
    @SerializedName( "free_limit")
    val freeLimit: kotlin.Int,
    @SerializedName( "id")
    val id: java.util.UUID,
    @SerializedName( "pubkeys")
    val pubkeys: kotlin.collections.List<kotlin.String>,
    @SerializedName( "remaining_free_emoji")
    val remainingFreeEmoji: kotlin.Int,
    @SerializedName( "role")
    val role: DisplayOrderUser.Role,
    @SerializedName( "two_factor_should_prompt")
    val twoFactorShouldPrompt: kotlin.Boolean,
    @SerializedName( "updated_at")
    val updatedAt: java.time.OffsetDateTime,
    @SerializedName( "alternate_id")
    val alternateId: kotlin.String? = null,
    @SerializedName( "deactivated_at")
    val deactivatedAt: java.time.OffsetDateTime? = null,
    @SerializedName( "email")
    val email: kotlin.String? = null,
    @SerializedName( "email_verified_at")
    val emailVerifiedAt: java.time.OffsetDateTime? = null,
    @SerializedName( "first_name")
    val firstName: kotlin.String? = null,
    @SerializedName( "last_name")
    val lastName: kotlin.String? = null,
    @SerializedName( "source")
    val source: kotlin.String? = null,
    @SerializedName( "two_factor_auth")
    val twoFactorAuth: List<DisplayOrderUser.TwoFactorAuth>? = null,
    @SerializedName( "two_factor_last_prompted_at")
    val twoFactorLastPromptedAt: java.time.OffsetDateTime? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }

    /**
    * 
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
    /**
    * 
    * Values: googleAuthenticator,sMS
    */
    
    enum class TwoFactorAuth(val value: String){
        @SerializedName( "GoogleAuthenticator") googleAuthenticator("GoogleAuthenticator"),
        @SerializedName( "SMS") sMS("SMS");
    }
}

