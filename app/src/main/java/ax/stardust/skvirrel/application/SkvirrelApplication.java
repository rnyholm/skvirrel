package ax.stardust.skvirrel.application;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import ax.stardust.skvirrel.BuildConfig;
import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.schedule.MonitoringScheduler;
import timber.log.Timber;

/**
 * Base class for maintaining global application state and different setups.
 */
public class SkvirrelApplication extends Application {

    public static final String CHANNEL_ID = "stock_monitoring_notification_channel";

    public SkvirrelApplication() {
        super();
    }

    @Override
    public void onCreate() {
        // very, very important to call on create
        super.onCreate();

        // plant a debug log tree if not production
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ProductionTree());
        }

        createNotificationChannel();

        MonitoringScheduler.scheduleJob(this);
    }

    private void createNotificationChannel() {
        // create the notification channel, but only on API 26+ because
        // the notification channel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Simple log tree for production purposes. Only log messages with log
     * priorities ERROR and WARNING is logged using this tree.
     */
    private static class ProductionTree extends Timber.DebugTree {

        @Override
        protected void log(int priority, String tag, @NotNull String message, Throwable t) {
            // only log error and warning in production
            if (priority == Log.ERROR || priority == Log.WARN) {
                super.log(priority, tag, message, t);
            }
        }
    }
}
