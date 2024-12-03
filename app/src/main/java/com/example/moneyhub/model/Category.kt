package com.example.moneyhub.model

data class Category(
    val gid: String = "",
    val category: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "gid" to gid,
            "category" to category
        )
    }

    companion object{
        fun fromMap(map: Map<String, Any?>): Category {
            return Category(
                gid = map["gid"] as? String ?: "",
                category = map["category"] as? List<String> ?: emptyList()
            )
        }
    }
}

