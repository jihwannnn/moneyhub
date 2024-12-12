package com.example.moneyhub.utils

import android.content.Context

object LocalCacheUtils {

    // 캐시 불러오기 메서드
    fun loadUserGroupsFromLocalCache(context: Context): List<Pair<String, String>> {
        val prefs = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val json = prefs.getString("userGroups", "{}") ?: "{}"
        val obj = org.json.JSONObject(json)
        val groupsObj = obj.optJSONObject("groups") ?: org.json.JSONObject()

        val result = mutableListOf<Pair<String, String>>()
        val keys = groupsObj.keys()
        while (keys.hasNext()) {
            val gid = keys.next()
            val gname = groupsObj.getString(gid)
            result.add(gid to gname)
        }
        return result
    }
}