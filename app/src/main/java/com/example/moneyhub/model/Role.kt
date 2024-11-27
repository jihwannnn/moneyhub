package com.example.moneyhub.model

enum class Role {
    OWNER,     // 소유자
    MANAGER,   // 매니저
    REGULAR;   // 일반

    companion object {
        fun fromName(name: String): Role {
            return valueOf(name.uppercase())
        }
    }
}