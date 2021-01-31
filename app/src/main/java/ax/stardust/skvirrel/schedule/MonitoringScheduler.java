package ax.stardust.skvirrel.schedule;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import ax.stardust.skvirrel.BuildConfig;
import ax.stardust.skvirrel.service.MonitoringJobService;
import ax.stardust.skvirrel.service.ServiceParams;
import timber.log.Timber;

/**
 * Class responsible for handling everything around job scheduling.
 */
public class MonitoringScheduler {

    // preferred and maximum time between runs in seconds
    private static final int PREFERRED_POLL_TIME_DEBUG = 60;    // 1 minute
    private static final int MAXIMUM_POLL_TIME_DEBUG = 120;     // 2 minutes
    private static final int PREFERRED_POLL_TIME_RELEASE = 900; // 15 minutes
    private static final int MAXIMUM_POLL_TIME_RELEASE = 1200;  // 20 minutes

    /**
     * Schedules monitoring jobs
     *
     * @param context context for the job
     */
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, MonitoringJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(ServiceParams.MONITORING_JOB_SERVICE_ID, serviceComponent);
        builder.setMinimumLatency(resolveMinimumLatency());
        builder.setOverrideDeadline(resolveOverrideDeadline());
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setPersisted(true);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

        Timber.d("Monitoring job successfully scheduled");
    }

    /**
     * Resolves minimum latency to be used for scheduled job. Different times are calculated
     * depending on if it's a debug or release build. Debug builds run much more frequent.
     *
     * @return resolved minimum latency
     */
    private static long resolveMinimumLatency() {
        return 1000 * (BuildConfig.DEBUG ? PREFERRED_POLL_TIME_DEBUG : PREFERRED_POLL_TIME_RELEASE);
    }

    /**
     * Resolves override deadline to be used for scheduled job. Different times are calculated
     * depending on if it's a debug or release build. Debug builds run much more frequent.
     *
     * @return resolved override deadline
     */
    private static long resolveOverrideDeadline() {
        return 1000 * (BuildConfig.DEBUG ? MAXIMUM_POLL_TIME_DEBUG : MAXIMUM_POLL_TIME_RELEASE);
    }
}
