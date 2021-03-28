package ax.stardust.skvirrel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.BuildConfig;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.notification.NotificationHandler;
import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.service.ServiceParams;
import timber.log.Timber;

/**
 * Custom broadcast receiver responsible for handling stock infos fetched actions. In turn this receiver
 * also checks if any stock monitorings should be notified and kicks of notifications based on that.
 */
public class MonitoringReceiver extends BroadcastReceiver {

    // action which this receiver listens at
    public static final String ACTION_STOCK_INFOS_FETCHED = "ax.stardust.skvirrel.STOCK_INFOS_FETCHED";

    private DatabaseManager databaseManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // get parcelable stocks from intent
        ArrayList<ParcelableStock> parcelableStocks =
                intent.getParcelableArrayListExtra(ServiceParams.ResultExtra.STOCK_INFOS);

        // sanity check of intent
        if (parcelableStocks == null) {
            String errorMessage = "Intent with parcelable stocks of \"null\" has been passed in to receiver";
            IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
            Timber.e(exception, "Unable to handle intent");
            throw exception;
        }

        // log to logcat if application is debug built
        logIfDebug(intent, parcelableStocks);

        // map to store stock monitorings and it's abstract monitorings that got their monitoring
        // criteria met. From these notifications will be created later
        HashMap<StockMonitoring, List<AbstractMonitoring>> stockMonitoringsToNotify = new HashMap<>();

        // fetch all stock monitorings that should be monitored
        List<StockMonitoring> stockMonitorings = getDatabaseManager(context).fetchAllStockMonitoringsForMonitoring();

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

        // create and send some notifications
        NotificationHandler.notify(context, getDatabaseManager(context), stockMonitoringsToNotify);
    }

    private DatabaseManager getDatabaseManager(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }

    private void logIfDebug(Intent intent, List<ParcelableStock> parcelableStocks) {
        // if application is built with debug config only
        if (BuildConfig.DEBUG) {
            String parcelableStocksString = parcelableStocks.stream()
                    .map(parcelableStock ->
                            String.format("%s(%s)", parcelableStock.getName(), parcelableStock.getTicker()))
                    .collect(Collectors.joining(", "));

            String logEntry = String.format("Broadcast received\nAction: %s\nParcelable stocks: %s",
                    intent.getAction(), parcelableStocksString);

            Timber.d(logEntry);
        }
    }
}
