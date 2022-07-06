package com.example.chatapp.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val username: String? = null, val city: String? = null ,val phone: String? = null,val bio: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "city" to city,
            "phone" to phone,
            "bio" to bio,

        )
    }
}