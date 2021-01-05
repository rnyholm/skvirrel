package ax.stardust.skvirrel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ax.stardust.skvirrel.schedule.MonitoringScheduler;

/**
 * Receiver that listens at boot events and schedules monitoring jobs upon boot.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // verify that action of intent corresponds to what's declared in manifest
        if ("android.intent.action.BOOT_COMPLETED".equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action)) {
            MonitoringScheduler.scheduleJob(context);
        }
    }
}
