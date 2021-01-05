package ax.stardust.skvirrel.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.schedule.MonitoringScheduler;
import timber.log.Timber;

/**
 * Job service responsible for kicking the actual monitoring to life.
 */
public class MonitoringJobService extends JobService {

    private JobParameters jobParameters;
    private DatabaseManager databaseManager;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.jobParameters = jobParameters;

        // before anything else, re-schedule job
        MonitoringScheduler.scheduleJob(getApplicationContext());

        // need to set a reference to this job service to the handler in order to easily access
        // these resources from else where
        Handler.getInstance().setMonitoringJobService(this);

        // start the actual monitoring service
        final Context context = getApplicationContext();
        Intent intent = new Intent(context, StockService.class);
        intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_STOCK_INFOS);
        intent.putStringArrayListExtra(ServiceParams.RequestExtra.SYMBOLS, getDatabaseManager().fetchAllSymbols());

        StockService.enqueueWork(context, intent);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // system decides to stop execution before job finished, just log it and return false,
        // or in other words, more or less ignore this...
        Timber.d("onStopJob: System decided to stop job before application manage to do it");
        return false;
    }

    /**
     * Convenience method to finish any existing job
     */
    public void notifyJobFinished() {
        if (jobParameters != null) {
            jobFinished(jobParameters, false);
        }
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(this);
        }
        return databaseManager;
    }

    /**
     * Handler class for this job service. In other words a singleton which makes it easy to handle
     * the monitoring job service instance within handler in a static way.
     */
    public static class Handler {
        private static final Handler INSTANCE = new Handler();

        private MonitoringJobService monitoringJobService;

        private Handler() {
            // private due to singleton
        }

        /**
         * To get singleton instance of this handler
         *
         * @return singleton instance of this handler
         */
        public static Handler getInstance() {
            return INSTANCE;
        }

        /**
         * Set monitoring job service of this instance
         *
         * @param monitoringJobService to be set
         */
        public void setMonitoringJobService(MonitoringJobService monitoringJobService) {
            this.monitoringJobService = monitoringJobService;
        }

        /**
         * Convenience method to finish any existing job
         */
        public void notifyJobFinished() {
            if (monitoringJobService != null) {
                monitoringJobService.notifyJobFinished();
            }
        }
    }
}
