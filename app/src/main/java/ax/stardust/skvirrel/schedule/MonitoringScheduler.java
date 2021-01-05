package ax.stardust.skvirrel.schedule;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import ax.stardust.skvirrel.service.MonitoringJobService;
import ax.stardust.skvirrel.service.ServiceParams;
import timber.log.Timber;

/**
 * Class responsible for handling everything around job scheduling.
 */
public class MonitoringScheduler {

    // preferred and maximum time between runs in seconds
    private static final int PREFERRED_POLL_TIME = 10; // 900 in production (15min)
    private static final int MAXIMUM_POLL_TIME = 40; // 1200 in production (20min)

    /**
     * Schedules monitoring jobs
     *
     * @param context context for the job
     */
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, MonitoringJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(ServiceParams.MONITORING_JOB_SERVICE_ID, serviceComponent);
        builder.setMinimumLatency(PREFERRED_POLL_TIME * 1000);
        builder.setOverrideDeadline(MAXIMUM_POLL_TIME * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setPersisted(true);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

        Timber.d("Monitoring job successfully scheduled");
    }
}
