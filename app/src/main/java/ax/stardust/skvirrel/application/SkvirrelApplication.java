package ax.stardust.skvirrel.application;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import ax.stardust.skvirrel.R;

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

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
