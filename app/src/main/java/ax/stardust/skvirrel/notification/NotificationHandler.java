package ax.stardust.skvirrel.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.Skvirrel;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.util.SkvirrelUtils;

/**
 * Handler for everything about notifications within this application.
 */
public class NotificationHandler {

    // channel id for notification channel
    public static final String CHANNEL_ID = "stock_monitoring_notification_channel";

    /**
     * Creates a notification channel for this application from given context
     *
     * @param context context from which notification channel will be created
     */
    public static void createNotificationChannel(Context context) {
        // create the notification channel, but only on API 26+ because
        // the notification channel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Creates and sends notification for given data
     *
     * @param context                  context for notification
     * @param databaseManager          database manager for updating stock monitorings
     * @param stockMonitoringsToNotify map containing stock monitorings and abstract monitorings
     *                                 which notifications are created from
     */
    public static void notify(Context context, DatabaseManager databaseManager,
                              HashMap<StockMonitoring, List<AbstractMonitoring>> stockMonitoringsToNotify) {
        // iterate through every entry which notifications should be created for
        stockMonitoringsToNotify.entrySet().forEach(entry -> {
            StockMonitoring stockMonitoring = entry.getKey();
            List<AbstractMonitoring> abstractMonitorings = entry.getValue();

            // create pending intent to preserve back stack via task stack builder
            Intent notificationIntent = new Intent(context, Skvirrel.class);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(notificationIntent);

            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // collect translated texts from monitorings for notification
            List<String> notificationTexts = new ArrayList<>();
            abstractMonitorings.forEach(abstractMonitoring ->
                    notificationTexts.add(String.format("%s %s %s",
                            abstractMonitoring.getMonitoringType().getTranslatedName(context),
                            StringUtils.toRootLowerCase(abstractMonitoring.getComparator().getTranslatedName(context)),
                            abstractMonitoring.getValue())));

            // get joined notification text
            String notificationText = SkvirrelUtils.join(context, notificationTexts);

            // build up the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.notification_title, stockMonitoring.getCompanyName()))
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());

            // at last mark the "triggered" as notified and update to db
            abstractMonitorings.forEach(AbstractMonitoring::notifyy);
            databaseManager.update(stockMonitoring);
        });
    }
}
