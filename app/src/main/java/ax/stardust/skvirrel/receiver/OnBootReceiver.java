package ax.stardust.skvirrel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ax.stardust.skvirrel.schedule.MonitoringScheduler;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MonitoringScheduler.scheduleJob(context);
    }
}
