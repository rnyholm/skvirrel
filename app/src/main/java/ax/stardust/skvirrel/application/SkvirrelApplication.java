package ax.stardust.skvirrel.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.jetbrains.annotations.NotNull;

import ax.stardust.skvirrel.BuildConfig;
import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.SkvirrelCrashReportDialog;
import ax.stardust.skvirrel.notification.NotificationHandler;
import ax.stardust.skvirrel.schedule.MonitoringScheduler;
import timber.log.Timber;

/**
 * Base class for maintaining global application state and different setups.
 */
public class SkvirrelApplication extends Application {

    /**
     * Creates a new instance of skvirrel application
     */
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

        // create notification handler and schedule monitoring job
        NotificationHandler.createNotificationChannel(this);
        MonitoringScheduler.scheduleJob(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // configure acra
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);

        // core configuration
        builder.withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON);

        // mail configuration
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .withSubject(getString(R.string.crash_report_email_subject))
                .withMailTo(getString(R.string.skvirrel_email))
                .withReportFileName(getString(R.string.crash_report_filename))
                .withReportAsFile(true)
                //make sure to enable all plugins you want to use:
                .withEnabled(true);

        // dialog configuration
        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class)
                .withReportDialogClass(SkvirrelCrashReportDialog.class)
                .withEnabled(true);

        ACRA.init(this, builder);
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
