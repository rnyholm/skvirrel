package ax.stardust.skvirrel.receiver;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.Skvirrel;
import ax.stardust.skvirrel.application.SkvirrelApplication;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.parcelable.ParcelableStock;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import timber.log.Timber;

/**
 * Custom receiver responsible for handling stock info fetched actions. In turn this receiver
 * also checks if any stock monitorings should be notified and kicks of notifications based on that.
 */
public class MonitoringReceiver extends BroadcastReceiver {

    public static final String STOCK_INFOS_FETCHED = "ax.stardust.skvirrel.STOCK_INFOS_FETCHED";

    private DatabaseManager databaseManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder("Broadcast received\n");
        sb.append("Action: ").append(intent.getAction()).append("\n");
        sb.append("URI: ").append(intent.toUri(Intent.URI_INTENT_SCHEME)).append("\n");
        sb.append("ParcelableStocks: ");
        ArrayList<ParcelableStock> parcelableStocks = intent.getParcelableArrayListExtra(ServiceParams.ResultExtra.STOCK_INFOS);

        String collect = Objects.requireNonNull(parcelableStocks).stream().map(ParcelableStock::getName).collect(Collectors.joining(", "));
        sb.append(collect).append("\n");

        Timber.d(sb.toString());

        // map to store stock monitorings and it's abstract monitorings that got their monitoring
        // criteria met. From these notifications will be created later
        HashMap<StockMonitoring, List<AbstractMonitoring>> stockMonitoringsToNotify = new HashMap<>();

        // fetch all stock monitorings that should be monitored
        List<StockMonitoring> stockMonitorings = getDatabaseManager(context).fetchAllForMonitoring();

        // go through each parcelable stock and each stock monitoring...
        parcelableStocks.forEach(parcelableStock -> {
            // ...and each stock monitoring...
            stockMonitorings.forEach(stockMonitoring -> {
                // ...check that they have the same ticker...
                if (StringUtils.equals(parcelableStock.getTicker(), stockMonitoring.getTicker())) {
                    // ...go through every monitoring that should be monitored...
                    stockMonitoring.getMonitoringsThatShouldBeMonitored().forEach(abstractMonitoring -> {
                        // ...check if monitoring criteria is met for current abstract monitoring and given parcelable stock
                        // if so, add them to the map for further handling
                        boolean monitoringCriteriaMet = abstractMonitoring.checkMonitoringCriteria(parcelableStock);
                        if (monitoringCriteriaMet) {
                            stockMonitoringsToNotify.putIfAbsent(stockMonitoring, new ArrayList<>());
                            Objects.requireNonNull(stockMonitoringsToNotify.get(stockMonitoring)).add(abstractMonitoring);
                        }
                    });
                }
            });
        });

        // now handle the notifications
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
                    abstractMonitoring.getComparator().getTranslatedName(context),
                    abstractMonitoring.getValue())));

            // get joined notification text
            String notificationText = SkvirrelUtils.join(context, notificationTexts);

            // build up the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SkvirrelApplication.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.notification_title, stockMonitoring.getCompanyName()))
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) stockMonitoring.getId(), builder.build());

            // at last mark the "triggered" as notified and update to db
            abstractMonitorings.forEach(AbstractMonitoring::notifyy);
            getDatabaseManager(context).update(stockMonitoring);
        });
    }

    private DatabaseManager getDatabaseManager(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }
}
