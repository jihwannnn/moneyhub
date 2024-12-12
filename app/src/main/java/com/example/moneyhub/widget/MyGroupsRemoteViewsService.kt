package com.example.moneyhub.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.moneyhub.R
import com.example.moneyhub.utils.LocalCacheUtils.loadUserGroupsFromLocalCache

class MyGroupsRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MyGroupsRemoteViewsFactory(applicationContext)
    }
}

class MyGroupsRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var groupList: List<Pair<String, String>> = emptyList()
    // 예: (groupId, groupName)

    override fun onCreate() {
        // 초기화 작업
    }

    override fun onDataSetChanged() {
        // 여기서 로컬에 캐싱된 그룹 데이터를 불러온다.
        // 예를 들어 SharedPreferences나 Room DB 등에서 현재 유저가 속한 그룹 리스트 로드
        // groupList = loadUserGroupsFromLocalCache()
        // 예시
        groupList = loadUserGroupsFromLocalCache(context)
        Log.d("MyGroupsRemoteViewsFactory", "Loaded groups: $groupList")
    }

    override fun onDestroy() {
        // 정리 작업
    }

    override fun getCount(): Int = groupList.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_widget_group)
        val groupId = groupList[position].first
        val groupName = groupList[position].second
        rv.setTextViewText(R.id.tvGroupNameWidget, groupName)

        // 항목 클릭 시 특정 액션 수행 (옵션)
        val fillInIntent = Intent().apply {
            putExtra("gid", groupId)
            putExtra("gname", groupName)
        }
        // fillInIntent.putExtra("gid", groupList[position].first)
        rv.setOnClickFillInIntent(R.id.widgetItemRoot, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}
