package ax.stardust.skvirrel.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.Skvirrel;
import ax.stardust.skvirrel.application.SkvirrelApplication;
import ax.stardust.skvirrel.entity.StockMonitoring;
import ax.stardust.skvirrel.parcelable.ParcelableStock;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.service.ServiceParams;
import timber.log.Timber;

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

        List<StockMonitoring> stockMonitorings = getDatabaseManager(context).fetchAll();
        parcelableStocks.forEach(parcelableStock -> {
            Optional<StockMonitoring> optionalTriggeredStockMonitoring = stockMonitorings.stream()
                    .filter(stockMonitoring -> stockMonitoring.getSymbol().equals(parcelableStock.getSymbol()))
                    .filter(stockMonitoring -> Double.parseDouble(parcelableStock.getPrice()) <= stockMonitoring.getMonitoringOptions().getPrice())
                    .findFirst();

            if (optionalTriggeredStockMonitoring.isPresent()) {
                StockMonitoring triggeredStockMonitoring = optionalTriggeredStockMonitoring.get();

                // Create an explicit intent for an Activity in your app
                Intent notificationIntent = new Intent(context, Skvirrel.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SkvirrelApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.notification_title, triggeredStockMonitoring.getCompanyName()))
                        .setContentText(context.getString(R.string.notification_text, String.valueOf(triggeredStockMonitoring.getMonitoringOptions().getPrice())))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify((int) triggeredStockMonitoring.getId(), builder.build());
            }
        });
    }

    private DatabaseManager getDatabaseManager(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }
}
