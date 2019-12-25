package com.example.kontrol.plan.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.core.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.kontrol.plan.R;
import com.example.kontrol.plan.activities.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class PlanAppWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                               int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.plan_app_widget

            );
            String planName = PreferenceManager.getDefaultSharedPreferences(context).getString("WIDGET_PLAN_NAME", "Plan Name");
            views.setTextViewText(R.id.widget_plan_name, planName);

            Intent intent = new Intent(context, MessagesListService.class);
            views.setRemoteAdapter(R.id.widget_message_list_view, intent);
            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_message_list_view, clickPendingIntentTemplate);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widget_message_list_view);

        }
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, PlanAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plan_app_widget);
            String planName = PreferenceManager.getDefaultSharedPreferences(context).getString("WIDGET_PLAN_NAME", "Plan Name");
            views.setTextViewText(R.id.widget_plan_name, planName);

            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, PlanAppWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_message_list_view);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(cn, views);

        }
        super.onReceive(context, intent);
    }

}

