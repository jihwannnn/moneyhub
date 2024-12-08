package com.example.moneyhub.widget

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetManager
import com.example.moneyhub.R
import com.example.moneyhub.activity.mypage.MyPageActivity

class MyGroupsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 여러 위젯 인스턴스 처리
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.my_groups_widget)

            // ListView를 채워줄 Service 설정
            val serviceIntent = Intent(context, MyGroupsRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.widgetGroupListView, serviceIntent)

            // 빈뷰 설정 (데이터 없을 때)
            // views.setEmptyView(R.id.widgetGroupListView, R.id.tvEmptyView)

            // 리스트 아이템 클릭 시 실행될 인텐트 템플릿 설정
            val clickIntent = Intent(context, MyPageActivity::class.java)
            val pendingIntent1 = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // 위젯을 터치하면 MyPageActivity로 이동하는 인텐트
            val intent = Intent(context, MyPageActivity::class.java)
            val pendingIntent2 = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            views.setOnClickPendingIntent(R.id.tvWidgetTitle, pendingIntent2)
            views.setPendingIntentTemplate(R.id.widgetGroupListView, pendingIntent1)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetGroupListView)
        }
    }
}
